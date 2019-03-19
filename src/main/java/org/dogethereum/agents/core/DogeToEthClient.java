/*
 * Copyright (C) 2017 RSK Labs Ltd.
 * Copyright (C) 2018 Coinfabrik and Oscar Guindzberg.
 */
package org.dogethereum.agents.core;


import org.dogethereum.agents.core.dogecoin.*;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.bitcoinj.store.BlockStoreException;
import org.dogethereum.agents.constants.AgentConstants;
import org.dogethereum.agents.constants.SystemProperties;
import org.dogethereum.agents.core.dogecoin.DogecoinWrapper;
import org.dogethereum.agents.core.dogecoin.Proof;
import org.dogethereum.agents.core.eth.EthWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.datatypes.generated.Bytes32;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Manages the process of informing Dogethereum Contracts news about the dogecoin blockchain
 * @author Oscar Guindzberg
 * @author Catalina Juarros
 */
@Service
@Slf4j(topic = "DogeToEthClient")
public class DogeToEthClient {
    private static final Logger log = LoggerFactory.getLogger("LocalAgentConstants");
    static final int MAXIMUM_REGISTER_DOGE_LOCK_TXS_PER_TURN = 40;

    @Autowired
    private EthWrapper ethWrapper;


    private SystemProperties config;

    private AgentConstants agentConstants;

    @Autowired
    private DogecoinWrapper dogecoinWrapper;

    @Autowired
    private SuperblockChain superblockChain;

    public DogeToEthClient() {}


    @PostConstruct
    public void setup() throws Exception {
        config = SystemProperties.CONFIG;
        if (config.isDogeSuperblockSubmitterEnabled()) {
            agentConstants = config.getAgentConstants();

            new Timer("Doge to Eth client").scheduleAtFixedRate(new DogeToEthClientTimerTask(),
                    getFirstExecutionDate(), agentConstants.getDogeToEthTimerTaskPeriod());

        }
    }


    private Date getFirstExecutionDate() {
        Calendar firstExecution = Calendar.getInstance();
        firstExecution.add(Calendar.SECOND, 20);
        return firstExecution.getTime();
    }


    @SuppressWarnings("unused")
    private class DogeToEthClientTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (!ethWrapper.isEthNodeSyncing()) {
                    log.debug("DogeToEthClientTimerTask");
                    ethWrapper.updateContractFacadesGasPrice();
                    if (config.isDogeSuperblockSubmitterEnabled()) {
                        updateBridgeSuperblockChain();
                    }
                } else {
                    log.warn("DogeToEthClientTimerTask skipped because the eth node is syncing blocks");
                }
            } catch (Exception e) {

                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Updates bridge with all the superblocks that the agent has but the bridge doesn't.
     * @return Number of superblocks sent to the bridge.
     * @throws Exception
     */
    public long updateBridgeSuperblockChain() throws Exception {
        if (ethWrapper.arePendingTransactionsForSendSuperblocksAddress()) {
            log.debug("Skipping sending superblocks, there are pending transaction for the sender address.");
            return 0;
        }

        // Get the best superblock from the relay that is also in the main chain.
        List<Bytes32> superblockLocator = ethWrapper.getSuperblockLocator();
        Superblock matchedSuperblock = getEarliestMatchingSuperblock(superblockLocator);

        checkNotNull(matchedSuperblock, "No best chain superblock found");
        log.debug("Matched superblock {}.", matchedSuperblock.getSuperblockId());

        // We found the superblock in the agent's best chain. Send the earliest superblock that the relay is missing.
        Superblock toSend = superblockChain.getFirstDescendant(matchedSuperblock.getSuperblockId());

        if (toSend == null) {
            log.debug("Bridge was just updated, no new superblocks to send. matchedSuperblock: {}.",
                    matchedSuperblock.getSuperblockId());
            return 0;
        }

        if (!superblockChain.sendingTimePassed(toSend)) {
            log.debug("Too early to send superblock {}, will try again in a few seconds.",
                    toSend.getSuperblockId());
            return 0;
        }

        if (ethWrapper.wasSuperblockAlreadySubmitted(toSend.getSuperblockId())) {
            log.debug("The contract already knows about the superblock, it won't be sent again: {}.",
                      toSend.getSuperblockId());
            return 0;
        }

        log.debug("First superblock missing in the bridge: {}.", toSend.getSuperblockId());
        ethWrapper.sendStoreSuperblock(toSend, ethWrapper.getGeneralPurposeAndSendSuperblocksAddress());
        log.debug("Invoked sendStoreSuperblocks with superblock {}.", toSend.getSuperblockId());

        return toSend.getSuperblockHeight();
    }

    /**
     * Helper method for updateBridgeSuperblockChain().
     * Gets the earliest superblock from the bridge's superblock locator
     * that was also found in the agent's main chain.
     * @param superblockLocator List of ancestors provided by the bridge.
     * @return Earliest matched block if it is found,
     *         null otherwise.
     * @throws BlockStoreException
     * @throws IOException
     */
    private Superblock getEarliestMatchingSuperblock(List<Bytes32> superblockLocator)
            throws BlockStoreException, IOException {
        Superblock matchedSuperblock = null;

        for (int i = 0; i < superblockLocator.size(); i++) {
            Keccak256Hash superblockBridgeHash = Keccak256Hash.wrap(superblockLocator.get(i).getValue());
            Superblock bridgeSuperblock = superblockChain.getSuperblock(superblockBridgeHash);

            if (bridgeSuperblock == null)
                continue;

            Superblock bestRelaySuperblockInLocalChain =
                    superblockChain.getSuperblockByHeight(bridgeSuperblock.getSuperblockHeight());

            if (bridgeSuperblock.getSuperblockId().equals(bestRelaySuperblockInLocalChain.getSuperblockId())) {
                matchedSuperblock = bestRelaySuperblockInLocalChain;
                break;
            }
        }

        return matchedSuperblock;
    }

    /**
     * Relays all unprocessed transactions to Ethereum contracts by calling sendRelayTx.
     * @throws Exception
     */
    public void getSuperblockSPVProof(Sha256Hash txToSendToEthHash) throws Exception {
        if (ethWrapper.arePendingTransactionsForRelayTxsAddress()) {
            log.debug("Skipping relay tx, there are pending transaction for the sender address.");
            return;
        }

        if (!ethWrapper.wasSyscoinTxProcessed(txToSendToEthHash)) {
            synchronized (this) {

                StoredBlock txStoredBlock = dogecoinWrapper.getBlock(txToSendToEthHash);
                if (txStoredBlock == null) {
                    // no block found for tx
                    log.debug("Tx {} not relayed because the block it's in hasn't been stored in local" +
                                    "database yet. tx hash: {}",
                            txToSendToEthHash);
                    return;
                }
                Superblock txSuperblock = findBestSuperblockFor(txStoredBlock.getHeader().getHash());

                if (txSuperblock == null) {
                    // no superblock found for tx
                    log.debug("Tx {} not relayed because the superblock it's in hasn't been stored in local" +
                                    "database yet. Block hash: {}",
                            txToSendToEthHash, txStoredBlock.getHeader().getHash());
                    return;
                }

                if (!ethWrapper.isSuperblockApproved(txSuperblock.getSuperblockId())) {
                    log.debug("Tx {} not relayed because the superblock it's in hasn't been approved yet." +
                                    "Block hash: {}, superblock ID: {}",
                            txToSendToEthHash, txStoredBlock.getHeader().getHash(),
                            txSuperblock.getSuperblockId());
                    return;
                }

                int dogeBlockIndex = txSuperblock.getDogeBlockLeafIndex(txStoredBlock.getHeader().getHash());
                byte[] includeBits = new byte[(int) Math.ceil(txSuperblock.getDogeBlockHashes().size() / 8.0)];
                Utils.setBitLE(includeBits, dogeBlockIndex);
                SuperblockPartialMerkleTree superblockPMT = SuperblockPartialMerkleTree.buildFromLeaves(agentConstants.getDogeParams(),
                        includeBits, txSuperblock.getDogeBlockHashes());

                ethWrapper.getSuperblockSPVProof((AltcoinBlock) txStoredBlock.getHeader(), txSuperblock, superblockPMT);


                log.debug("Invoked registerTransaction for tx {}", txToSendToEthHash);
            }
        }
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

