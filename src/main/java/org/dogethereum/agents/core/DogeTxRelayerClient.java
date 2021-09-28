/*
 * Copyright (C) 2017 RSK Labs Ltd.
 * Copyright (C) 2018 Coinfabrik and Oscar Guindzberg.
 */
package org.dogethereum.agents.core;


import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.bitcoinj.store.BlockStoreException;
import org.dogethereum.agents.constants.AgentConstants;
import org.dogethereum.agents.constants.SystemProperties;
import org.dogethereum.agents.core.dogecoin.*;
import org.dogethereum.agents.core.eth.EthWrapper;
import org.dogethereum.agents.util.OperatorPublicKeyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Relay doge txs to the Dogethereum Contracts
 * @author Oscar Guindzberg
 * @author Catalina Juarros
 */
@Service
@Slf4j(topic = "DogeTxRelayerClient")
public class DogeTxRelayerClient {

    static final int MAXIMUM_REGISTER_DOGE_LOCK_TXS_PER_TURN = 40;

    @Autowired
    private EthWrapper ethWrapper;

    @Autowired
    private OperatorPublicKeyHandler operatorPublicKeyHandler;

    private SystemProperties config;

    private AgentConstants agentConstants;

    @Autowired
    private DogecoinWrapper dogecoinWrapper;

    @Autowired
    private SuperblockChain superblockChain;

    public DogeTxRelayerClient() {}


    @PostConstruct
    public void setup() throws Exception {
        config = SystemProperties.CONFIG;
        if (config.isDogeTxRelayerEnabled() || config.isOperatorEnabled()) {
            agentConstants = config.getAgentConstants();

            new Timer("Superblock Submitter client").scheduleAtFixedRate(new DogeTxRelayerClientTimerTask(),
                    getFirstExecutionDate(), agentConstants.getDogeTxRelayerTimerTaskPeriod());

        }
    }


    private Date getFirstExecutionDate() {
        Calendar firstExecution = Calendar.getInstance();
        firstExecution.add(Calendar.SECOND, 20);
        return firstExecution.getTime();
    }


    @SuppressWarnings("unused")
    private class DogeTxRelayerClientTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (!ethWrapper.isEthNodeSyncing()) {
                    log.debug("DogeTxRelayerClientTimerTask");
                    ethWrapper.updateContractFacadesGasPrice();
                    if (config.isDogeTxRelayerEnabled() || config.isOperatorEnabled()) {
                        updateBridgeTransactions();
                    }
                } else {
                    log.warn("DogeTxRelayerClientTimerTask skipped because the eth node is syncing blocks");
                }
            } catch (Exception e) {

                log.error(e.getMessage(), e);
            }
        }
    }


    /**
     * Relays all unprocessed transactions to Ethereum contracts by calling sendRelayTx.
     * @throws Exception
     */
    public void updateBridgeTransactions() throws Exception {
        if (ethWrapper.arePendingTransactionsForRelayTxsAddress()) {
            log.debug("Skipping relay tx, there are pending transaction for the sender address.");
            return;
        }

        Set<Transaction> operatorWalletTxSet = dogecoinWrapper.getTransactions(
                agentConstants.getDogeToEthConfirmations(),
                config.isDogeTxRelayerEnabled(),
                config.isOperatorEnabled());

        int numberOfTxsSent = 0;

        for (Transaction operatorWalletTx : operatorWalletTxSet) {
            if (!ethWrapper.wasDogeTxProcessed(operatorWalletTx.getTxId())) {
                synchronized (this) {
                    List<Proof> proofs = dogecoinWrapper.getTransactionsToSendToEth().get(operatorWalletTx.getTxId());

                    if (proofs == null || proofs.isEmpty())
                        continue;

                    StoredBlock txStoredBlock = findBestChainStoredBlockFor(operatorWalletTx);
                    PartialMerkleTree txPMT = null;

                    for (Proof proof : proofs) {
                        if (proof.getBlockHash().equals(txStoredBlock.getHeader().getHash())) {
                            txPMT = proof.getPartialMerkleTree();
                        }
                    }

                    List<TransactionOutput> txOutputs = operatorWalletTx.getOutputs();
                    if (txOutputs.size() > 3) {
                        log.debug("Tx {} not relayed because it's neither a lock nor unlock transaction." +
                                        " Block hash: {}",
                                operatorWalletTx.getTxId(), txStoredBlock.getHeader().getHash());
                        continue;
                    }

                    Superblock txSuperblock = findBestSuperblockFor(txStoredBlock.getHeader().getHash());

                    if (txSuperblock == null) {
                        // no superblock found for tx
                        log.debug("Tx {} not relayed because the superblock it's in hasn't been stored in local" +
                                        "database yet. Block hash: {}",
                                  operatorWalletTx.getTxId(), txStoredBlock.getHeader().getHash());
                        continue;
                    }

                    if (!ethWrapper.isSuperblockApproved(txSuperblock.getSuperblockId())) {
                        log.debug("Tx {} not relayed because the superblock it's in hasn't been approved yet." +
                                        "Block hash: {}, superblock ID: {}",
                                operatorWalletTx.getTxId(), txStoredBlock.getHeader().getHash(),
                                txSuperblock.getSuperblockId());
                        continue;
                    }

                    int dogeBlockIndex = txSuperblock.getDogeBlockLeafIndex(txStoredBlock.getHeader().getHash());
                    byte[] includeBits = new byte[(int) Math.ceil(txSuperblock.getDogeBlockHashes().size() / 8.0)];
                    Utils.setBitLE(includeBits, dogeBlockIndex);
                    PartialMerkleTree superblockPMT = PartialMerkleTree.buildFromLeaves(agentConstants.getDogeParams(),
                            includeBits, txSuperblock.getDogeBlockHashes());

                    ethWrapper.sendRelayTx(operatorWalletTx, operatorPublicKeyHandler.getPublicKeyHash(),
                            (AltcoinBlock) txStoredBlock.getHeader(), txSuperblock,
                            txPMT, superblockPMT, isLockTransaction(txOutputs));
                    numberOfTxsSent++;
                    // Send a maximum of 40 registerTransaction txs per turn
                    if (numberOfTxsSent >= MAXIMUM_REGISTER_DOGE_LOCK_TXS_PER_TURN) {
                        break;
                    }
                    log.debug("Invoked registerTransaction for tx {}", operatorWalletTx.getTxId());
                }
            }
        }
    }

    private boolean isLockTransaction(List<TransactionOutput> outputs) {
        if (outputs.size() == 1) {
            return false;
        }
        TransactionOutput output = outputs.get(1);
        byte[] script = output.getScriptBytes();

        // scriptPub format for the ethereum address in a lock transaction is
        // 0x6a OP_RETURN
        // 0x14 PUSH20
        // []   20 bytes of the ethereum address
        return script.length == 20+2 &&
                script[0] == 0x6a &&
                script[1] == 0x14;
    }

    /**
     * Finds the block in the best chain where supplied tx appears.
     * @throws IllegalStateException If the tx is not in the best chain
     */
    private StoredBlock findBestChainStoredBlockFor(Transaction tx) throws IllegalStateException, BlockStoreException {
        Map<Sha256Hash, Integer> blockHashes = tx.getAppearsInHashes();

        if (blockHashes != null)
            for (Sha256Hash blockHash : blockHashes.keySet()) {
                StoredBlock storedBlock = dogecoinWrapper.getBlock(blockHash);
                // Find out if that block is in the main chain
                int height = storedBlock.getHeight();
                StoredBlock storedBlockAtHeight = dogecoinWrapper.getStoredBlockAtHeight(height);
                if (storedBlockAtHeight!=null && storedBlockAtHeight.getHeader().getHash().equals(blockHash)) {
                    return storedBlockAtHeight;
                }
            }

        throw new IllegalStateException("Tx not in the best chain: " + tx.getTxId());
    }

    /**
     * Finds the superblock in the superblock main chain that contains the block identified by `blockHash`.
     * @param blockHash SHA-256 hash of a block that we want to find.
     * @return Superblock where the block can be found.
     * @throws BlockStoreException
     */
    private Superblock findBestSuperblockFor(Sha256Hash blockHash) throws BlockStoreException, IOException {
        Superblock currentSuperblock = superblockChain.getChainHead();

        while (currentSuperblock != null) {
            if (currentSuperblock.hasDogeBlock(blockHash))
                return currentSuperblock;
            currentSuperblock = superblockChain.getSuperblock(currentSuperblock.getParentId());
        }

        // current superblock is null
        return null;
    }

}

