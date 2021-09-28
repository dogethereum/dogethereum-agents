/*
 * Copyright (C) 2017 RSK Labs Ltd.
 * Copyright (C) 2018 Coinfabrik and Oscar Guindzberg.
 */
package org.dogethereum.agents.core.dogecoin;


import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.script.Script;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.wallet.Wallet;
import org.dogethereum.agents.constants.SystemProperties;
import org.dogethereum.agents.util.AgentUtils;
import org.dogethereum.agents.constants.AgentConstants;
import org.dogethereum.agents.util.FileUtils;
import org.dogethereum.agents.util.OperatorPublicKeyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j(topic = "DogecoinWrapper")
public class DogecoinWrapper {
    private OperatorPublicKeyHandler operatorPublicKeyHandler;

    SystemProperties config;
    private WalletAppKit kit;
    private Context dogeContext;
    private AgentConstants agentConstants;
    private File dataDirectory;

    private Map<Sha256Hash, List<Proof>> dogeTxToRelayToEthProofsMap = new ConcurrentHashMap<>();
    private File dogeTxToRelayToEthProofsFile;


    @Autowired
    public DogecoinWrapper(OperatorPublicKeyHandler operatorPublicKeyHandler) throws Exception {
        this.operatorPublicKeyHandler = operatorPublicKeyHandler;
        this.config = SystemProperties.CONFIG;
        this.agentConstants = config.getAgentConstants();
        this.dogeContext = new Context(agentConstants.getDogeParams());
        this.dataDirectory = new File(config.dataDirectory() + "/DogecoinWrapper");
        this.dogeTxToRelayToEthProofsFile = new File(dataDirectory.getAbsolutePath() + "/DogeTxToRelayToEthProofs.ser");
        restoreProofsFromFile();
        setup();
        start();
    }


    public void setup() {
        kit = new WalletAppKit(dogeContext, Script.ScriptType.P2PKH, null, dataDirectory, "dogethereumAgentLibdohj") {
            @Override
            protected void onSetupCompleted() {
                Context.propagate(dogeContext);
                if (config.isDogeLockTxRelayEnabled() || config.isOperatorEnabled()) {
                    // When we receive a block that includes a tx that sends funds to eth via peg, store the PartialMerkleTree
                    vPeerGroup.addBlocksDownloadedEventListener((peer, block, filteredBlock, blocksLeft) -> {
                        if (filteredBlock != null) {
                            // filteredBlock may be null if we are downloading just headers before fastCatchupTimeSecs
                            Context.propagate(dogeContext);
                            onBlock(filteredBlock);
                        }
                    });
                    vWallet.addCoinsReceivedEventListener((wallet, tx, prevBalance, newBalance) -> coinsReceivedOrSent(tx));
                    vWallet.addCoinsSentEventListener((wallet, tx, prevBalance, newBalance) -> coinsReceivedOrSent(tx));
                }
                vPeerGroup.setDownloadTxDependencies(0);
            }

            private void coinsReceivedOrSent(Transaction tx) {
                Context.propagate(dogeContext);
                if (AgentUtils.isLockTx(tx, vWallet, agentConstants, operatorPublicKeyHandler) || AgentUtils.isReleaseTx(tx, vWallet, operatorPublicKeyHandler)) {
                    onTransaction(tx);
                }
            }

            @Override
            protected Wallet createWallet() {
                Wallet wallet = super.createWallet();
                if (config.isDogeLockTxRelayEnabled() || config.isOperatorEnabled()) {
                    LegacyAddress address = operatorPublicKeyHandler.getAddress();
                    // Be notified when we receive doge so we call registerTransaction()
                    wallet.addWatchedAddress(address, operatorPublicKeyHandler.getAddressCreationTime());
                }
                return wallet;
            }
            @Override
            protected BlockStore provideBlockStore(File file) throws BlockStoreException {
                return new AltcoinLevelDBBlockStore(dogeContext, getChainFile());
            }
            @Override
            protected boolean chainFileDelete(File chainFile) {
                return FileUtils.recursiveDelete(chainFile.getAbsolutePath());
            }
            @Override
            protected File getChainFile() {
                return new File(directory, "chain");
            }
            @Override
            protected boolean chainFileExists(File chainFile) {
                return chainFile.exists();
            }
        };

        // TODO: Make the dogecoin peer list configurable
        // if (!peerAddresses.isEmpty()) {
        //    kit.setPeerNodes(peerAddresses.toArray(new PeerAddress[]{}));
        //}
        kit.connectToLocalHost();

        InputStream checkpoints = DogecoinWrapper.class.getResourceAsStream("/" + dogeContext.getParams().getId() + ".checkpoints");
        if (checkpoints != null) {
            kit.setCheckpoints(checkpoints);
        }
    }

    public void start() {
        Context.propagate(dogeContext);
        kit.startAsync().awaitRunning();
    }

    public void stop() {
        Context.propagate(dogeContext);
        kit.stopAsync().awaitTerminated();
    }

    public int getBestChainHeight() {
        return kit.chain().getBestChainHeight();
    }

    public StoredBlock getChainHead() {
        return kit.chain().getChainHead();
    }

    public StoredBlock getBlock(Sha256Hash hash) throws BlockStoreException {
        return kit.store().get(hash);
    }

    public StoredBlock getStoredBlockAtHeight(int height) throws BlockStoreException {
        return AgentUtils.getStoredBlockAtHeight(kit.store(), height);
    }

    public Set<Transaction> getTransactions(int minconfirmations, boolean includeLock, boolean includeUnlock) {
        Set<Transaction> txs = new HashSet<>();
        for (Transaction tx : kit.wallet().getTransactions(false)) {
            if (tx.getConfidence().getConfidenceType().equals(TransactionConfidence.ConfidenceType.BUILDING) &&
                tx.getConfidence().getDepthInBlocks() >= minconfirmations) {
                if (AgentUtils.isLockTx(tx, kit.wallet(), agentConstants, operatorPublicKeyHandler) && includeLock ||
                    AgentUtils.isReleaseTx(tx, kit.wallet(), operatorPublicKeyHandler) && includeUnlock) {
                    txs.add(tx);
                }
            }
        }
        return txs;
    }

    public Map<Sha256Hash, List<Proof>> getTransactionsToSendToEth() {
        return dogeTxToRelayToEthProofsMap;
    }

    public void onBlock(FilteredBlock filteredBlock) {
        synchronized (this) {
            log.debug("onBlock {}", filteredBlock.getHash());
            List<Sha256Hash> hashes = new ArrayList<>();
            PartialMerkleTree tree = filteredBlock.getPartialMerkleTree();
            tree.getTxnHashAndMerkleRoot(hashes);
            for (Sha256Hash txToSendToEth : dogeTxToRelayToEthProofsMap.keySet()) {
                if (hashes.contains(txToSendToEth)) {
                    List<Proof> proofs = dogeTxToRelayToEthProofsMap.get(txToSendToEth);
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

    public void onTransaction(Transaction tx) {
        log.debug("onTransaction {}", tx.getTxId());
        synchronized (this) {
            dogeTxToRelayToEthProofsMap.put(tx.getTxId(), new ArrayList<Proof>());
            try {
                flushProofs();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }


    @PreDestroy
    public void tearDown() throws BlockStoreException, IOException {
        log.info("DogecoinWrapper tearDown starting...");
        stop();

        synchronized (this) {
            flushProofs();
        }
        log.info("DogecoinWrapper tearDown finished.");
    }

    private void restoreProofsFromFile() throws IOException, ClassNotFoundException {
        if (dogeTxToRelayToEthProofsFile.exists()) {
            synchronized (this) {
                try (
                        FileInputStream txsToSendToEthFileIs = new FileInputStream(dogeTxToRelayToEthProofsFile);
                        ObjectInputStream txsToSendToEthObjectIs = new ObjectInputStream(txsToSendToEthFileIs);
                ) {
                    this.dogeTxToRelayToEthProofsMap = (Map<Sha256Hash, List<Proof>> ) txsToSendToEthObjectIs.readObject();
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
                FileOutputStream txsToSendToEthFileOs = new FileOutputStream(dogeTxToRelayToEthProofsFile);
                ObjectOutputStream txsToSendToEthObjectOs = new ObjectOutputStream(txsToSendToEthFileOs);
        ) {
            txsToSendToEthObjectOs.writeObject(this.dogeTxToRelayToEthProofsMap);
        }
    }


    public void broadcastDogecoinTransaction(Transaction tx) {
        kit.peerGroup().broadcastTransaction(tx);
    }
}
