package org.dogethereum.dogesubmitter.core;


import org.dogethereum.dogesubmitter.core.dogecoin.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.bitcoinj.store.BlockStoreException;
import org.dogethereum.dogesubmitter.constants.AgentConstants;
import org.dogethereum.dogesubmitter.constants.SystemProperties;
import org.dogethereum.dogesubmitter.core.dogecoin.DogecoinWrapperListener;
import org.dogethereum.dogesubmitter.core.dogecoin.DogecoinWrapper;
import org.dogethereum.dogesubmitter.core.dogecoin.Proof;
import org.dogethereum.dogesubmitter.core.eth.EthWrapper;
import org.dogethereum.dogesubmitter.util.OperatorPublicKeyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Manages the process of informing DogeRelay news about the dogecoin blockchain
 * @author Oscar Guindzberg
 */
@Service
@Slf4j(topic = "DogeToEthClient")
public class DogeToEthClient implements DogecoinWrapperListener {

    static final int MAXIMUM_REGISTER_DOGE_LOCK_TXS_PER_TURN = 40;

    @Autowired
    private EthWrapper ethWrapper;

    @Autowired
    private OperatorPublicKeyHandler keyHandler;

    private SystemProperties config;

    private AgentConstants agentConstants;

    private DogecoinWrapper dogecoinWrapper;

    private SuperblockChain superblockChain;
    private File superblockChainFile;

    private Map<Sha256Hash, List<Proof>> txsToSendToEth = new ConcurrentHashMap<>();

    private File dataDirectory;

    private File proofFile;

    public DogeToEthClient() {}


    @PostConstruct
    public void setup() throws Exception {
        config = SystemProperties.CONFIG;
        if (config.isDogeBlockSubmitterEnabled() || config.isDogeTxRelayerEnabled() || config.isOperatorEnabled()) {
            agentConstants = config.getAgentConstants();

            this.dataDirectory = new File(config.dataDirectory());
            this.proofFile = new File(dataDirectory.getAbsolutePath() + "/DogeToEthClient.proofs");
            restoreProofsFromFile();
            setupDogecoinWrapper();

            // int numberOfFederators = bridgeConstants.getFederatorPublicKeys().size();
            int numberOfFederators = 1;
            new Timer("Submitter update bridge").scheduleAtFixedRate(new UpdateBridgeTimerTask(), getFirstExecutionDate(), agentConstants.getUpdateBridgeExecutionPeriod() * numberOfFederators);

            Context context = new Context(agentConstants.getDogeParams());

            superblockChain = new SuperblockChain(dogecoinWrapper, context, dataDirectory);
            superblockChain.initialize(agentConstants.getUpdateBridgeExecutionPeriod(), getFirstExecutionDate());
        }
    }

    private void setupDogecoinWrapper() throws UnknownHostException {
        dogecoinWrapper = new DogecoinWrapper(agentConstants, dataDirectory, keyHandler, config.isDogeTxRelayerEnabled() || config.isOperatorEnabled());
        // TODO: Make the dogecoin peer list configurable
        // dogecoinWrapper.setup(this, this, ethWrapper.getDogecoinPeerAddresses());
        dogecoinWrapper.setup(this, null);
        dogecoinWrapper.start();
    }

    public Map<Sha256Hash, List<Proof>> getTransactionsToSendToEth() {
        return txsToSendToEth;
    }

    public Date getFirstExecutionDate() {
        Calendar firstExecution = Calendar.getInstance();
        firstExecution.add(Calendar.SECOND, 30);
        return firstExecution.getTime();
    }

    @Override
    public void onBlock(FilteredBlock filteredBlock) {
        if (config.isDogeTxRelayerEnabled() || config.isOperatorEnabled()) {
            synchronized (this) {
                log.debug("onBlock {}", filteredBlock.getHash());
                List<Sha256Hash> hashes = new ArrayList<>();
                PartialMerkleTree tree = filteredBlock.getPartialMerkleTree();
                tree.getTxnHashAndMerkleRoot(hashes);
                for (Sha256Hash txToSendToEth : txsToSendToEth.keySet()) {
                    if (hashes.contains(txToSendToEth)) {
                        List<Proof> proofs = txsToSendToEth.get(txToSendToEth);
                        boolean alreadyIncluded = false;
                        for (Proof proof : proofs) {
                            if (proof.getBlockHash().equals(filteredBlock.getHash())) {
                                alreadyIncluded = true;
                            }
                        }
                        if (!alreadyIncluded) {
                            Proof proof = new Proof(filteredBlock.getHash(), tree);
                            proofs.add(proof);
                            log.info("New proof for tx " + txToSendToEth + " in block " + filteredBlock.getHash());
                            try {
                                flushProofs();
                            } catch (IOException e) {
                                log.error(e.getMessage(), e);
                            }
                        } else {
                            log.info("Proof for tx " + txToSendToEth + " in block " + filteredBlock.getHash() + " already stored");
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onTransaction(Transaction tx) {
        if (config.isDogeTxRelayerEnabled() || config.isOperatorEnabled()) {
            log.debug("onTransaction {}", tx.getHash());
            synchronized (this) {
                txsToSendToEth.put(tx.getHash(), new ArrayList<Proof>());
                try {
                    flushProofs();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    private class UpdateBridgeTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (!ethWrapper.isEthNodeSyncing()) {
                    log.debug("UpdateBridgeTimerTask");
                    ethWrapper.updateContractFacadesGasPrice();
                    if (config.isDogeBlockSubmitterEnabled()) {
//                        updateBridgeDogeBlockchain();
                        updateBridgeSuperblockChain();
                    }
                    if (config.isDogeTxRelayerEnabled() || config.isOperatorEnabled()) {
                        updateBridgeTransactions();
                    }
                } else {
                    log.warn("UpdateBridgeTimerTask skipped because the eth node is syncing blocks");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Update bridge with all the superblocks that the agent has but the bridge doesn't.
     * @return Number of superblocks sent to the bridge.
     * @throws Exception
     */
    public int updateBridgeSuperblockChain() throws Exception {
        List<byte[]> superblockLocator = ethWrapper.getSuperblockLocator();
        Superblock matchedSuperblock = getEarliestMatchingSuperblock(superblockLocator);

        checkNotNull(matchedSuperblock, "No best chain superblock found");
        log.debug("Matched superblock {}.", matchedSuperblock.getSuperblockHash());

        // We found the superblock in the agent's best chain. Send superblocks that the relay is missing.
        Deque<Superblock> superblocksToSendToBridge = getSuperblocksNewerThan(matchedSuperblock.getSuperblockHash());
        if (superblocksToSendToBridge.size() == 0) {
            log.debug("Bridge was just updated, no new superblocks to send. matchedSuperblock: {}.",
                    matchedSuperblock.getSuperblockHash());
            return 0;
        }
        log.debug("Superblocks missing in the bridge: {}.", superblocksToSendToBridge.size());

        // Unlike updateBridgeDogeBlockchain, this does not have an upper limit for sending superblocks,
        // because we don't expect to send them as frequently as Doge blocks. This might change later.
        ethWrapper.sendStoreSuperblocks(superblocksToSendToBridge);
        log.debug("Invoked sendStoreSuperblocks with {} blocks. First: {}, last: {}.",
                superblocksToSendToBridge.size(),
                superblocksToSendToBridge.peekFirst().getSuperblockHash(),
                superblocksToSendToBridge.peekLast().getSuperblockHash());

        return superblocksToSendToBridge.size();
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
    private Superblock getEarliestMatchingSuperblock(List<byte[]> superblockLocator) throws BlockStoreException, IOException {
        Superblock matchedSuperblock = null;

        for (int i = 0; i < superblockLocator.size(); i++) {
            byte[] superblockBridgeHash = superblockLocator.get(i);
            Superblock bridgeSuperblock = superblockChain.getSuperblock(superblockBridgeHash);

            if (bridgeSuperblock == null)
                continue;

            Superblock bestRelaySuperblockInLocalChain = superblockChain.getSuperblockByHeight(bridgeSuperblock.getSuperblockHeight());

            if (bridgeSuperblock.equals(bestRelaySuperblockInLocalChain)) {
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
     * @param superblockHash Hash of the best superblock from the bridge that was also found in the agent.
     * @return Deque of superblocks newer than the given superblock, from earliest to latest.
     * @throws BlockStoreException
     * @throws IOException
     */
    private Deque<Superblock> getSuperblocksNewerThan(byte[] superblockHash) throws BlockStoreException, IOException {
        Deque<Superblock> superblocks = new ArrayDeque<>();
        Superblock currentSuperblock = superblockChain.getChainHead();

//        while (!currentSuperblock.getSuperblockHash().equals(superblockHash)) {
        while (!Arrays.equals(currentSuperblock.getSuperblockHash(), superblockHash)) {
            superblocks.addFirst(currentSuperblock);
            currentSuperblock = superblockChain.getSuperblock(currentSuperblock.getPrevSuperblockHash());
        }

        return superblocks;
    }

    public int updateBridgeDogeBlockchain() throws Exception {
        int bridgeDogeBlockchainBestChainHeight = ethWrapper.getDogeBestBlockHeight();
        if (dogecoinWrapper.getBestChainHeight() <= bridgeDogeBlockchainBestChainHeight) {
            return 0;
        }
        // Agent's blockchain has more blocks than bridge's blockchain
        log.debug("DOGE blockchain height - Agent : {}, Bridge : {}.", dogecoinWrapper.getBestChainHeight(), bridgeDogeBlockchainBestChainHeight);

        // Search the latest shared block between the agent and the bridge contract

        // Deprecated implementation not using a block locator
//        String bridgeDogeBlockchainHeadHash = ethWrapper.getBlockchainHeadHash();
//        StoredBlock matchedBlock = null;
//        StoredBlock storedBlock = dogecoinWrapper.getBlock(Sha256Hash.wrap(bridgeDogeBlockchainHeadHash));
//        if (storedBlock != null) {
//            StoredBlock storedBlockInBestChain = dogecoinWrapper.getStoredBlockAtHeight(storedBlock.getHeight());
//            if (storedBlock.equals(storedBlockInBestChain)) {
//                matchedBlock = storedBlockInBestChain;
//            }
//        }

        // Implementation using a block locator
        List<String> blockLocator = ethWrapper.getDogeBlockchainBlockLocator();
        log.debug("Block locator size {}, first {}, last {}.", blockLocator.size(), blockLocator.get(0), blockLocator.get(blockLocator.size()-1));
        // find the last best chain block it has
        StoredBlock matchedBlock = null;
        for (int i = 0; i < blockLocator.size(); i++) {
            String blockHash = (String) blockLocator.get(i);
            StoredBlock storedBlock = dogecoinWrapper.getBlock(Sha256Hash.wrap(blockHash));
            if (storedBlock == null)
                continue;
            StoredBlock storedBlockInBestChain = dogecoinWrapper.getStoredBlockAtHeight(storedBlock.getHeight());
            if (storedBlock.equals(storedBlockInBestChain)) {
                matchedBlock = storedBlockInBestChain;
                break;
            }
        }

        checkNotNull(matchedBlock, "No best chain block found");

        log.debug("Matched block {}.", matchedBlock.getHeader().getHash());

        // We found the block in the agent's best chain. Send receiveHeaders with the blocks it is missing.
        StoredBlock current = dogecoinWrapper.getChainHead();
        List<Block> headersToSendToBridge = new LinkedList<>();
        while (!current.equals(matchedBlock)) {
            headersToSendToBridge.add(current.getHeader());
            current = dogecoinWrapper.getBlock(current.getHeader().getPrevBlockHash());
        }
        if (headersToSendToBridge.size() == 0) {
            log.debug("Bridge was just updated, no new blocks to send, matchedBlock: {}.", matchedBlock.getHeader().getHash());
            return 0;
        }
        headersToSendToBridge = Lists.reverse(headersToSendToBridge);
        log.debug("Headers missing in the bridge {}.", headersToSendToBridge.size());
        int to = Math.min(agentConstants.getMaxDogeHeadersPerRound(), headersToSendToBridge.size());
        List<Block> headersToSendToBridgeSubList = headersToSendToBridge.subList(0, to);
        ethWrapper.sendStoreHeaders(headersToSendToBridgeSubList.toArray(new Block[]{}));
        log.debug("Invoked receiveHeaders with {} blocks. First {}, Last {}.", headersToSendToBridgeSubList.size(),
                     headersToSendToBridgeSubList.get(0).getHash(), headersToSendToBridgeSubList.get(headersToSendToBridgeSubList.size()-1).getHash());
        return headersToSendToBridgeSubList.size();
    }

    public void updateBridgeTransactions() throws Exception {
        Set<Transaction> operatorWalletTxSet = dogecoinWrapper.getTransactions(agentConstants.getDoge2EthMinimumAcceptableConfirmations(), config.isDogeTxRelayerEnabled(), config.isOperatorEnabled());
        int numberOfTxsSent = 0;
        for (Transaction operatorWalletTx : operatorWalletTxSet) {
            if (!ethWrapper.wasDogeTxProcessed(operatorWalletTx.getHash())) {
                synchronized (this) {
                    List<Proof> proofs = txsToSendToEth.get(operatorWalletTx.getHash());

                    if (proofs == null || proofs.isEmpty())
                        continue;

                    StoredBlock txStoredBlock = findBestChainStoredBlockFor(operatorWalletTx);
                    PartialMerkleTree pmt = null;
                    for (Proof proof : proofs) {
                        if (proof.getBlockHash().equals(txStoredBlock.getHeader().getHash())) {
                            pmt = proof.getPartialMerkleTree();
                        }
                    }
                    int contractDogeBestBlockHeight = ethWrapper.getDogeBestBlockHeight();
                    if (contractDogeBestBlockHeight < (txStoredBlock.getHeight() + agentConstants.getDoge2EthMinimumAcceptableConfirmations() -1 )) {
                        log.debug("Tx not relayed yet because not enough confirmations yet {}. Contract height {}, Tx included in block {}",
                                  operatorWalletTx.getHash(), contractDogeBestBlockHeight, txStoredBlock.getHeight());
                        continue;
                    }
                    ethWrapper.sendRelayTx(operatorWalletTx, txStoredBlock.getHeader().getHash(), pmt);
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

    @PreDestroy
    public void tearDown() throws BlockStoreException, IOException {
        if (config.isDogeBlockSubmitterEnabled() || config.isDogeTxRelayerEnabled() || config.isOperatorEnabled()) {
            log.info("DogeToEthClient tearDown starting...");
            dogecoinWrapper.stop();

            synchronized (this) {
                flushProofs();
            }
            log.info("DogeToEthClient tearDown finished.");
        }
    }

    private void restoreProofsFromFile() throws IOException, ClassNotFoundException {
        if (proofFile.exists()) {
            synchronized (this) {
                try (
                    FileInputStream txsToSendToEthFileIs = new FileInputStream(proofFile);
                    ObjectInputStream txsToSendToEthObjectIs = new ObjectInputStream(txsToSendToEthFileIs);
                ) {
                    this.txsToSendToEth = (Map<Sha256Hash, List<Proof>> ) txsToSendToEthObjectIs.readObject();
                }
            }
        }
    }


    private void flushProofs() throws IOException {
        if (!dataDirectory.exists()) {
            if (!dataDirectory.mkdirs()) {
                throw new IOException("Could not create directory " + dataDirectory.getAbsolutePath());
            }
        }
        try (
            FileOutputStream txsToSendToEthFileOs = new FileOutputStream(proofFile);
            ObjectOutputStream txsToSendToEthObjectOs = new ObjectOutputStream(txsToSendToEthFileOs);
        ) {
            txsToSendToEthObjectOs.writeObject(this.txsToSendToEth);
        }
    }

}

