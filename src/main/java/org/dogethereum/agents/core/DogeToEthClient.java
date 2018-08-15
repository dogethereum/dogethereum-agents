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
import org.dogethereum.agents.util.OperatorPublicKeyHandler;
import org.spongycastle.util.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.math.BigInteger;
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

    public DogeToEthClient() {}


    @PostConstruct
    public void setup() throws Exception {
        config = SystemProperties.CONFIG;
        if (config.isDogeSuperblockSubmitterEnabled() || config.isDogeTxRelayerEnabled() || config.isOperatorEnabled()
                || config.isDogeMaliciousSubmitterEnabled()) {
            agentConstants = config.getAgentConstants();

            new Timer("Doge to Eth client").scheduleAtFixedRate(new DogeToEthClientTimerTask(),
                    getFirstExecutionDate(), agentConstants.getDogeToEthTimerTaskPeriod());

            Context context = new Context(agentConstants.getDogeParams());
        }
    }

    private Date getFirstExecutionDate() {
        Calendar firstExecution = Calendar.getInstance();
        firstExecution.add(Calendar.SECOND, 20);
        return firstExecution.getTime();
    }
    
    private class DogeToEthClientTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (!ethWrapper.isEthNodeSyncing()) {
                    log.debug("DogeToEthClientTimerTask");
                    ethWrapper.updateContractFacadesGasPrice();
                    if (config.isDogeSuperblockSubmitterEnabled()) {
                        updateBridgeSuperblockChain();
                    } else if (config.isDogeMaliciousSubmitterEnabled()) {
                        updateBridgeSuperblockChainMalicious();
                    }
                    if (config.isDogeTxRelayerEnabled() || config.isOperatorEnabled()) {
                        updateBridgeTransactionsSuperblocks();
                    }

                } else {
                    log.warn("DogeToEthClientTimerTask skipped because the eth node is syncing blocks");
                }
            } catch (Exception e) {

                log.error(e.getMessage(), e);
            }
        }
    }

//    public void updateBridgeSuperblockTooEarly() throws Exception {
//        if (ethWrapper.arePendingTransactionsForSendSuperblocksAddress()) {
//            log.debug("Skipping sending superblocks, there are pending transaction for the sender address.");
//        }
//
//        Keccak256Hash bestFromContracts = ethWrapper.getBestSuperblockId();
//        Superblock bestFromHonestChain = superblockChain.getSuperblock(bestFromContracts);
//        Superblock bestFromTooEarlyChain = superblockTooEarlyChain.getSuperblock(bestFromContracts);
//        Date contractDate = null;
//
//        if (bestFromHonestChain != null) {
//            contractDate = new Date(bestFromHonestChain.getLastDogeBlockTime());
//        } else if (bestFromTooEarlyChain != null) {
//            contractDate = new Date(bestFromTooEarlyChain.getLastDogeBlockTime());
//        }
//
//        if (contractDate != null) {
//            Superblock toSend = getNextSuperblockTooEarly(contractDate);
//
//            if (toSend == null) {
//                log.debug("Bridge was just updated, no new superblocks to send. bestFromContracts: {}.",
//                        bestFromContracts);
//                return;
//            }
//
//            if (ethWrapper.wasSuperblockAlreadySubmitted(toSend.getSuperblockId())) {
//                log.debug("The contract already knows about the superblock, it won't be sent again: {}.",
//                        toSend.getSuperblockId());
//                return;
//            }
//
//            log.debug("First superblock missing in the bridge: {}.", toSend.getSuperblockId());
//            ethWrapper.sendStoreSuperblock(toSend);
//            log.debug("Invoked sendStoreSuperblocks with superblock {}.", toSend.getSuperblockId());
//        }
//    }
//
//    private Superblock getNextSuperblockTooEarly(Date contractDate) throws BlockStoreException, IOException {
//        Superblock currentSuperblock = superblockTooEarlyChain.getChainHead();
//        Superblock parentSuperblock = superblockTooEarlyChain.getParent(currentSuperblock);
//
//        if (parentSuperblock == null) {
//            return currentSuperblock;
//        } else {
//            Date parentEndDate = new Date(parentSuperblock.getLastDogeBlockTime());
//
//            while (parentSuperblock != null && parentEndDate.after(contractDate)) {
//                currentSuperblock = parentSuperblock;
//                parentSuperblock = superblockTooEarlyChain.getParent(currentSuperblock);
//                if (parentSuperblock != null) {
//                    parentEndDate = new Date(parentSuperblock.getLastDogeBlockTime());
//                }
//            }
//        }
//
//        return currentSuperblock;
//    }

    /**
     * Update bridge with all the superblocks that the agent has but the bridge doesn't.
     * @return Number of superblocks sent to the bridge.
     * @throws Exception
     */
    public long updateBridgeSuperblockChain() throws Exception {
        if (ethWrapper.arePendingTransactionsForSendSuperblocksAddress()) {
            log.debug("Skipping sending superblocks, there are pending transaction for the sender address.");
            return 0;
        }

        // Get the best superblock from the relay that is also in the main chain.
        List<byte[]> superblockLocator = ethWrapper.getSuperblockLocator();
        Superblock matchedSuperblock = getEarliestMatchingSuperblock(superblockLocator);

        checkNotNull(matchedSuperblock, "No best chain superblock found");
        log.debug("Matched superblock {}.", matchedSuperblock.getSuperblockId());

        // We found the superblock in the agent's best chain. Send the earliest superblock that the relay is missing.
        Superblock toSend = getNextSuperblockInMainChain(matchedSuperblock.getSuperblockId());

        if (toSend == null) {
            log.debug("Bridge was just updated, no new superblocks to send. matchedSuperblock: {}.",
                    matchedSuperblock.getSuperblockId());
            return 0;
        }

        if (ethWrapper.wasSuperblockAlreadySubmitted(toSend.getSuperblockId())) {
            log.debug("The contract already knows about the superblock, it won't be sent again: {}.",
                      toSend.getSuperblockId());
            return 0;
        }

        log.debug("First superblock missing in the bridge: {}.", toSend.getSuperblockId());
        ethWrapper.sendStoreSuperblock(toSend);
        log.debug("Invoked sendStoreSuperblocks with superblock {}.", toSend.getSuperblockId());

        return toSend.getSuperblockHeight();
    }

    public long updateBridgeSuperblockChainMalicious() throws Exception {
        if (ethWrapper.arePendingTransactionsForSendSuperblocksAddress()) {
            log.debug("Skipping sending superblocks, there are pending transaction for the sender address.");
            return 0;
        }

        // Get the best superblock from the relay that is also in the main chain.
        List<byte[]> superblockLocator = ethWrapper.getSuperblockLocator();
        Superblock matchedSuperblock = getEarliestMatchingSuperblock(superblockLocator);

        checkNotNull(matchedSuperblock, "No best chain superblock found");
        log.debug("Matched superblock {}.", matchedSuperblock.getSuperblockId());

        Superblock toSend = forgeNewChildSuperblock(matchedSuperblock);
        superblockChain.putForgedSuperblock(toSend);

//        List<Sha256Hash> dogeBlockHashes = toSend.getDogeBlockHashes();

        if (ethWrapper.wasSuperblockAlreadySubmitted(toSend.getSuperblockId())) {
            log.debug("The contract already knows about the superblock, it won't be sent again: {}.",
                    toSend.getSuperblockId());
            return 0;
        }

        log.debug("Sending malicious superblock {}.", toSend.getSuperblockId());
        ethWrapper.sendStoreSuperblock(toSend);
        log.debug("Invoked sendStoreSuperblocks with superblock {}.", toSend.getSuperblockId());

        return toSend.getSuperblockHeight();
    }

    private Superblock forgeNewSuperblock(Superblock superblock) throws BlockStoreException {
//        AltcoinBlock fakeDogeBlock = createFakeDogeBlock(superblock.getLastDogeBlockHash());
        StoredBlock extraBlock = dogecoinWrapper.getChainHead();
        AltcoinBlock extraBlockHeader = (AltcoinBlock) extraBlock.getHeader();
        AltcoinBlock prevBlockHeader =
                (AltcoinBlock) dogecoinWrapper.getBlock(extraBlockHeader.getPrevBlockHash()).getHeader();
        AltcoinBlock lastDogeBlock =
                (AltcoinBlock) dogecoinWrapper.getBlock(superblock.getLastDogeBlockHash()).getHeader();

        List<Sha256Hash> dogeBlockHashes = new ArrayList<>(superblock.getDogeBlockHashes());
        dogeBlockHashes.add(extraBlockHeader.getHash());

        return new Superblock(agentConstants.getDogeParams(), dogeBlockHashes, extraBlock.getChainWork(),
                lastDogeBlock.getTimeSeconds(), prevBlockHeader.getTimeSeconds(),
                extraBlockHeader.getDifficultyTarget(), superblock.getParentId(),
                superblock.getSuperblockHeight() + 1);
    }

    // TODO: store it somewhere
    private AltcoinBlock createFakeDogeBlock(Sha256Hash prevBlockHash, long time) throws BlockStoreException {
        AltcoinBlock prevBlock = (AltcoinBlock) dogecoinWrapper.getBlock(prevBlockHash).getHeader();
        long version = prevBlock.getVersion();
        Sha256Hash merkleRoot = Sha256Hash.twiceOf("fake block".getBytes());
        List<Transaction> transactions = new ArrayList<>();
        return new AltcoinBlock(agentConstants.getDogeParams(), version, prevBlockHash, merkleRoot, time,
                prevBlock.getDifficultyTarget(), prevBlock.getNonce(), transactions);
    }

    // Create a superblock pretending that there are no blocks between the last block from the contracts
    // and a new fake Doge block with the same chain work as the Dogecoin chain tip
    private Superblock forgeNewChildSuperblock(Superblock superblock) throws BlockStoreException, IOException {
        StoredBlock topBlock = dogecoinWrapper.getChainHead();
        long time = topBlock.getHeader().getTimeSeconds();
        BigInteger chainWork = topBlock.getChainWork();
        Sha256Hash prevBlockHash = superblock.getLastDogeBlockHash();
        AltcoinBlock prevBlock = (AltcoinBlock) dogecoinWrapper.getBlock(prevBlockHash).getHeader();

        AltcoinBlock fakeDogeBlock = createFakeDogeBlock(prevBlockHash, time);
        dogecoinWrapper.storeFakeDogeBlock(fakeDogeBlock, chainWork,
                dogecoinWrapper.getBlock(prevBlockHash).getHeight() + 1);

        List<Sha256Hash> dogeBlockHashes = new ArrayList<>();
        dogeBlockHashes.add(fakeDogeBlock.getHash());

        return new Superblock(agentConstants.getDogeParams(), dogeBlockHashes, chainWork,
                fakeDogeBlock.getTimeSeconds(), prevBlock.getTimeSeconds(),
                fakeDogeBlock.getDifficultyTarget(), superblock.getSuperblockId(),
                superblock.getSuperblockHeight() + 1);
    }

    /**
     * Helper method for updateBridgeSuperblockChain().
     * Get the earliest superblock from the bridge's superblock locator
     * that was also found in the agent's main chain.
     * @param superblockLocator List of ancestors provided by the bridge.
     * @return Earliest matched block if it is found,
     *         null otherwise.
     * @throws BlockStoreException
     * @throws IOException
     */
    private Superblock getEarliestMatchingSuperblock(List<byte[]> superblockLocator)
            throws BlockStoreException, IOException {
        Superblock matchedSuperblock = null;

        for (int i = 0; i < superblockLocator.size(); i++) {
            Keccak256Hash superblockBridgeHash = Keccak256Hash.wrap(superblockLocator.get(i));
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
     * Helper method for updateBridgeSuperblockChain().
     * Get all the superblocks from the agent's main chain that come after a certain superblock.
     * Returns a Deque object because it provides an efficient interface for adding elements to the front;
     * since the blocks are traversed from latest to earliest but they must be sent in the opposite order,
     * this data structure is useful for this method.
     * @param superblockId Hash of the best superblock from the bridge that was also found in the agent.
     * @return Deque of superblocks newer than the given superblock, from earliest to latest.
     * @throws BlockStoreException
     * @throws IOException
     */
    private Deque<Superblock> getSuperblocksNewerThan(Keccak256Hash superblockId) throws BlockStoreException, IOException {
        Deque<Superblock> superblocks = new ArrayDeque<>();
        Superblock currentSuperblock = superblockChain.getChainHead();

        while (!currentSuperblock.getSuperblockId().equals(superblockId)) {
            superblocks.addFirst(currentSuperblock);
            currentSuperblock = superblockChain.getSuperblock(currentSuperblock.getParentId());
        }

        return superblocks;
    }

    /**
     * Helper method for updateBridgeSuperblockChain().
     * Find a superblock in the main chain with a given superblock as its parent.
     * @param superblockId Parent of superblock being searched.
     * @return Immediate child of given superblock if it's in the main chain and not the tip,
     *         null if it's the tip.
     * @throws BlockStoreException If the superblock whose hash is `superblockId` is not in the main chain.
     */
    private Superblock getNextSuperblockInMainChain(Keccak256Hash superblockId)
            throws BlockStoreException, IOException {
        if (superblockChain.getSuperblock(superblockId).getSuperblockHeight() == superblockChain.getChainHeight()) {
            // There's nothing above the tip of the chain.
            return null;
        }

        // There's a superblock after superblockId. Find it.
        Superblock currentSuperblock = superblockChain.getChainHead();

        while (currentSuperblock != null && !currentSuperblock.getParentId().equals(superblockId))
            currentSuperblock = superblockChain.getSuperblock(currentSuperblock.getParentId());

        checkNotNull(currentSuperblock, "Block is not in the main chain.");
        return currentSuperblock;
    }

    // Temporary
    public void updateBridgeTransactionsSuperblocks() throws Exception {
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
            if (!ethWrapper.wasDogeTxProcessed(operatorWalletTx.getHash())) {
                synchronized (this) {
                    List<Proof> proofs = dogecoinWrapper.getTransactionsToSendToEth().get(operatorWalletTx.getHash());

                    if (proofs == null || proofs.isEmpty())
                        continue;

                    StoredBlock txStoredBlock = findBestChainStoredBlockFor(operatorWalletTx);
                    PartialMerkleTree txPMT = null;

                    for (Proof proof : proofs) {
                        if (proof.getBlockHash().equals(txStoredBlock.getHeader().getHash())) {
                            txPMT = proof.getPartialMerkleTree();
                        }
                    }

                    Superblock txSuperblock = findBestSuperblockFor(txStoredBlock.getHeader().getHash());

                    if (txSuperblock == null) {
                        // no superblock found for tx
                        log.debug("Tx {} not relayed because the superblock it's in hasn't been stored in local" +
                                        "database yet. Block hash: {}",
                                  operatorWalletTx.getHash(), txStoredBlock.getHeader().getHash());
                        continue;
                    }

                    if (!ethWrapper.isSuperblockApproved(txSuperblock.getSuperblockId())) {
                        log.debug("Tx {} not relayed because the superblock it's in hasn't been approved yet." +
                                        "Block hash: {}, superblock ID: {}",
                                operatorWalletTx.getHash(), txStoredBlock.getHeader().getHash(),
                                txSuperblock.getSuperblockId());
                        continue;
                    }

                    int dogeBlockIndex = txSuperblock.getDogeBlockLeafIndex(txStoredBlock.getHeader().getHash());
                    byte[] includeBits = new byte[(int) Math.ceil(txSuperblock.getDogeBlockHashes().size() / 8.0)];
                    Utils.setBitLE(includeBits, dogeBlockIndex);
                    PartialMerkleTree superblockPMT = PartialMerkleTree.buildFromLeaves(agentConstants.getDogeParams(),
                            includeBits, txSuperblock.getDogeBlockHashes());

                    ethWrapper.sendRelayTx(operatorWalletTx, operatorPublicKeyHandler.getPublicKeyHash(),
                            (AltcoinBlock) txStoredBlock.getHeader(), txSuperblock, txPMT, superblockPMT);
                    numberOfTxsSent++;
                    // Send a maximum of 40 registerTransaction txs per turn
                    if (numberOfTxsSent >= MAXIMUM_REGISTER_DOGE_LOCK_TXS_PER_TURN) {
                        break;
                    }
                    log.debug("Invoked registerTransaction for tx {}", operatorWalletTx.getHash());
                }
            }
        }
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

        throw new IllegalStateException("Tx not in the best chain: " + tx.getHash());
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

