package org.dogethereum.dogesubmitter.core;


import org.dogethereum.dogesubmitter.constants.SystemProperties;
import org.dogethereum.dogesubmitter.core.dogecoin.DogecoinWrapper;
import org.dogethereum.dogesubmitter.constants.BridgeConstants;
import org.dogethereum.dogesubmitter.core.dogecoin.DogecoinWrapperImpl;
import org.dogethereum.dogesubmitter.core.dogecoin.BlockListener;
import org.dogethereum.dogesubmitter.core.dogecoin.TransactionListener;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.bitcoinj.store.BlockStoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Manages the process of informing DogeRelay news about the dogecoin blockchain
 * @author Oscar Guindzberg
 */
@Service
@Slf4j(topic = "DogeToEthClient")
public class DogeToEthClient implements BlockListener, TransactionListener {

    static final int MAXIMUM_REGISTER_DOGE_LOCK_TXS_PER_TURN = 40;

    @Autowired
    private FederatorSupport federatorSupport;

    //@Autowired
    //private FedNodeSystemProperties config;

    //@Autowired
    //private Ethereum eth;

    private SystemProperties config;

    private BridgeConstants bridgeConstants;


    private DogecoinWrapper dogecoinWrapper;

    private Map<Sha256Hash, List<Proof>> txsToSendToEth = new ConcurrentHashMap<>();

    private File dataDirectory;
    private File proofFile;

    public DogeToEthClient() {}


    @PostConstruct
    public void setup() throws Exception {
        config = SystemProperties.CONFIG;
        bridgeConstants = config.getBridgeConstants();
//        if (!checkFederateRequirements()) {
//            log.error("Error validating Fed-Node Requirements");
//            System.exit(1);
//        }

        //this.dataDirectory = new File(config.databaseDir() + "/peg");
        this.dataDirectory = new File(config.dataDirectory());
        this.proofFile = new File(dataDirectory.getAbsolutePath() + "/DogeToEthClient.proofs");

        restoreProofsFromFile();
        setupDogecoinWrapper();

        // int numberOfFederators = bridgeConstants.getFederatorPublicKeys().size();
        int numberOfFederators = 1;
        new Timer("Federator update bridge").scheduleAtFixedRate(new UpdateBridgeTimerTask(), getFirstExecutionDate(), bridgeConstants.getUpdateBridgeExecutionPeriod() * numberOfFederators);
    }

//    private boolean checkFederateRequirements() {
//        FedNodeSystemProperties config = FedNodeSystemProperties.FED_CONFIG;
//        int defaultPort = bridgeConstants.getDogeParams().getPort();
//        List<String> peers = config.dogecoinPeerAddresses();
//
//        Federator federator = new Federator(config.federatorKeyFile(), new FederatorPeersChecker(defaultPort, peers));
//        return federator.validFederator();
//    }


    private void restoreProofsFromFile() throws IOException {
        if (proofFile.exists()) {
            NetworkParameters networkParameters = bridgeConstants.getDogeParams();
            synchronized (this) {
                this.txsToSendToEth = Proof.deserializeProofs(Files.readAllBytes(proofFile.toPath()), networkParameters);
            }
        }
    }

    private void setupDogecoinWrapper() throws UnknownHostException {
        dogecoinWrapper = new DogecoinWrapperImpl(bridgeConstants, dataDirectory);
        //dogecoinWrapper.setup(this, this, federatorSupport.getDogecoinPeerAddresses());
        dogecoinWrapper.setup(this, this, null);
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

    @Override
    public void onTransaction(Transaction tx) {
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

    private class UpdateBridgeTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (!federatorSupport.isEthNodeSyncing()) {
                    log.debug("UpdateBridgeTimerTask");
                    federatorSupport.updateContractFacadesGasPrice();
                    updateBridgeDogeBlockchain();
                    // Don't relay tx if DogeRelay blockchain is not fully in sync - commented out because we don't need this
                    //if (numberOfBlocksSent < bridgeConstants.getMaxDogeHeadersPerRound())
                    updateBridgeTransactions();
//                      Just used for the release process
//                      federatorSupport.sendUpdateCollections();
                } else {
                    log.warn("UpdateBridgeTimerTask skipped because the eth node is syncing blocks");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public int updateBridgeDogeBlockchain() throws Exception {
        //m_lastBlockHeight
        int bridgeDogeBlockchainBestChainHeight = federatorSupport.getDogeBestBlockHeight();
        if (dogecoinWrapper.getBestChainHeight() <= bridgeDogeBlockchainBestChainHeight) {
            return 0;
        }

        log.debug("DOGE blockchain height - Federator : {}, Bridge : {}.", dogecoinWrapper.getBestChainHeight(), bridgeDogeBlockchainBestChainHeight);
        // Federator's blockchain has more blocks than bridge's blockchain

        //getBlockchainHeadHash
//        String bridgeDogeBlockchainHeadHash = federatorSupport.getBlockchainHeadHash();
//        StoredBlock matchedBlock = null;
//        StoredBlock storedBlock = dogecoinWrapper.getBlock(Sha256Hash.wrap(bridgeDogeBlockchainHeadHash));
//        if (storedBlock != null) {
//            StoredBlock storedBlockInBestChain = dogecoinWrapper.getBlockAtHeight(storedBlock.getHeight());
//            if (storedBlock.equals(storedBlockInBestChain)) {
//                matchedBlock = storedBlockInBestChain;
//            }
//        }

        // implementation using a block locator
        List<String> blockLocator = federatorSupport.getDogeBlockchainBlockLocator();
        log.debug("Block locator size {}, first {}, last {}.", blockLocator.size(), blockLocator.get(0), blockLocator.get(blockLocator.size()-1));
        // find the last best chain block it has
        StoredBlock matchedBlock = null;
        for (int i = 0; i < blockLocator.size(); i++) {
            String blockHash = (String) blockLocator.get(i);
            StoredBlock storedBlock = dogecoinWrapper.getBlock(Sha256Hash.wrap(blockHash));
            if (storedBlock == null)
                continue;
            StoredBlock storedBlockInBestChain = dogecoinWrapper.getBlockAtHeight(storedBlock.getHeight());
            if (storedBlock.equals(storedBlockInBestChain)) {
                matchedBlock = storedBlockInBestChain;
                break;
            }
        }

        checkNotNull(matchedBlock, "No best chain block found");

        log.debug("Matched block {}.", matchedBlock.getHeader().getHash());

        // We found the block in the federator's best chain. Send receiveHeaders with the blocks it is missing.
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
        int to = Math.min(bridgeConstants.getMaxDogeHeadersPerRound(), headersToSendToBridge.size());
        List<Block> headersToSendToBridgeSubList = headersToSendToBridge.subList(0, to);
        federatorSupport.sendStoreHeaders(headersToSendToBridgeSubList.toArray(new Block[]{}));
        log.debug("Invoked receiveHeaders with {} blocks. First {}, Last {}.", headersToSendToBridgeSubList.size(),
                     headersToSendToBridgeSubList.get(0).getHash(), headersToSendToBridgeSubList.get(headersToSendToBridgeSubList.size()-1).getHash());
        return headersToSendToBridgeSubList.size();
    }

    public void updateBridgeTransactions() throws Exception {
        Set<Transaction> federatorWalletTxSet = dogecoinWrapper.getTransactions(bridgeConstants.getDoge2EthMinimumAcceptableConfirmations());
        //Object[] bridgeTxHashesAlreadyProcessedArray = federatorSupport.getDogeTxHashesAlreadyProcessed();
//        Object[] bridgeTxHashesAlreadyProcessedArray = new Object[]{};
//        Set<Sha256Hash> bridgeTxHashesAlreadyProcessedSet = new HashSet<>(bridgeTxHashesAlreadyProcessedArray.length);
//        for (Object txHash : bridgeTxHashesAlreadyProcessedArray) {
//            bridgeTxHashesAlreadyProcessedSet.add(Sha256Hash.wrap((String)txHash));
//        }
        int numberOfTxsSent = 0;
        for (Transaction federatorWalletTx : federatorWalletTxSet) {
            if (!federatorSupport.wasLockTxProcessed(federatorWalletTx.getHash())) {
                synchronized (this) {
                    List<Proof> proofs = txsToSendToEth.get(federatorWalletTx.getHash());

                    if (proofs == null || proofs.isEmpty())
                        continue;

                    StoredBlock txStoredBlock = findBestChainStoredBlockFor(federatorWalletTx);
                    //int blockHeight = txStoredBlock.getHeight();
                    PartialMerkleTree pmt = null;
                    for (Proof proof : proofs) {
                        if (proof.getBlockHash().equals(txStoredBlock.getHeader().getHash())) {
                            pmt = proof.getPartialMerkleTree();
                        }
                    }
                    int contractHeight = federatorSupport.getDogeBestBlockHeight();
                    if (contractHeight < (txStoredBlock.getHeight() + bridgeConstants.getDoge2EthMinimumAcceptableConfirmations() -1 )) {
                        log.debug("Tx not relayed yet because not enough confirmations yet {}. Contract height {}, Tx included in block {}",
                                  federatorWalletTx.getHash(), contractHeight, txStoredBlock.getHeight());
                        continue;
                    }
                    federatorSupport.sendRelayTx(federatorWalletTx, txStoredBlock.getHeader().getHash(), pmt);
                    numberOfTxsSent++;
                    // Sent a maximum of 40 registerTransaction txs per federator
                    if (numberOfTxsSent >= MAXIMUM_REGISTER_DOGE_LOCK_TXS_PER_TURN) {
                        break;
                    }
                    //txsToSendToEth.remove(federatorWalletTx.getHash());
                    log.debug("Invoked registerTransaction for tx {}", federatorWalletTx.getHash());
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
                StoredBlock storedBlockAtHeight = dogecoinWrapper.getBlockAtHeight(height);
                if (storedBlockAtHeight!=null && storedBlockAtHeight.getHeader().getHash().equals(blockHash)) {
                    return storedBlockAtHeight;
                }
            }

        throw new IllegalStateException("Tx not in the best chain: " + tx.getHash());
    }

    @PreDestroy
    public void tearDown() throws BlockStoreException, IOException {
        log.info("DogeToEthClient tearDown starting...");
        dogecoinWrapper.stop();

        synchronized (this) {
            flushProofs();
        }
        log.info("DogeToEthClient tearDown finished.");
    }

    private void flushProofs() throws IOException {
        if (!dataDirectory.exists()) {
            if (!dataDirectory.mkdirs()) {
                throw new IOException("Could not create directory " + dataDirectory.getAbsolutePath());
            }
        }
        FileOutputStream txsToSendToEthFileOs = new FileOutputStream(proofFile);
        ObjectOutputStream txsToSendToEthObjectOs = new ObjectOutputStream(txsToSendToEthFileOs);
        txsToSendToEthObjectOs.write(Proof.encodeProofs(this.txsToSendToEth));
        txsToSendToEthObjectOs.close();
        txsToSendToEthFileOs.close();
    }

}

