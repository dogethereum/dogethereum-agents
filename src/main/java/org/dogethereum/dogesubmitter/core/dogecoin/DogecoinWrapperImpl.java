package org.dogethereum.dogesubmitter.core.dogecoin;


import org.dogethereum.dogesubmitter.BridgeUtils;
import org.dogethereum.dogesubmitter.constants.BridgeConstants;
import org.dogethereum.dogesubmitter.util.FileUtil;
import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.LevelDBBlockStore;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DogecoinWrapperImpl implements DogecoinWrapper {
    private WalletAppKit kit;
    private Context dogeContext;
    private BridgeConstants bridgeConstants;
    private File dataDirectory;

    public DogecoinWrapperImpl(BridgeConstants bridgeConstants, File dataDirectory) {
        this.dogeContext = new Context(bridgeConstants.getDogeParams());
        this.bridgeConstants = bridgeConstants;
        this.dataDirectory = dataDirectory;
    }

    @Override
    public void setup(final BlockListener blockListener, final TransactionListener transactionListener, List<PeerAddress> peerAddresses) {
        kit = new WalletAppKit(dogeContext, dataDirectory, "DogeToEthClient") {
            @Override
            protected void onSetupCompleted() {
                Context.propagate(dogeContext);
                // When we receive a block that includes a tx that sends funds to eth via peg, store the PartialMerkleTree
                vPeerGroup.addBlocksDownloadedEventListener((peer, block, filteredBlock, blocksLeft) -> {
                    if (filteredBlock != null) {
                        // filteredBlock may be null if we are downloading just headers before fastCatchupTimeSecs
                        Context.propagate(dogeContext);
                        blockListener.onBlock(filteredBlock);
                    }
                });
                vWallet.addCoinsReceivedEventListener((wallet, tx, prevBalance, newBalance) -> coinsReceivedOrSent(tx));
                vWallet.addCoinsSentEventListener((wallet, tx, prevBalance, newBalance) -> coinsReceivedOrSent(tx));
                vPeerGroup.setDownloadTxDependencies(0);
            }

            private void coinsReceivedOrSent(Transaction tx) {
                Context.propagate(dogeContext);
                if (BridgeUtils.isLockTx(tx, vWallet, bridgeConstants) || BridgeUtils.isReleaseTx(tx, bridgeConstants)) {
                    transactionListener.onTransaction(tx);
                }
            }

            @Override
            protected Wallet createWallet() {
                Wallet wallet = super.createWallet();
                Address address = bridgeConstants.getFederationAddress();
                // Be notified when we receive doge so we call registerTransaction()
                wallet.addWatchedAddress(address, bridgeConstants.getFederationAddressCreationTime());
                return wallet;
            }
            @Override
            protected BlockStore provideBlockStore(File file) throws BlockStoreException {
                return new LevelDBBlockStore(dogeContext, getChainFile());
            }
            @Override
            protected boolean chainFileDelete(File chainFile) {
                return FileUtil.recursiveDelete(chainFile.getAbsolutePath());
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

        //if (!peerAddresses.isEmpty()) {
        //    kit.setPeerNodes(peerAddresses.toArray(new PeerAddress[]{}));
        //}
        kit.connectToLocalHost();

        InputStream checkpoints = DogecoinWrapperImpl.class.getResourceAsStream("/" + dogeContext.getParams().getId() + ".checkpoints");
        if (checkpoints != null) {
            kit.setCheckpoints(checkpoints);
        }
    }

    @Override
    public void start() {
        Context.propagate(dogeContext);
        kit.startAsync().awaitRunning();
    }

    @Override
    public void stop() {
        Context.propagate(dogeContext);
        kit.stopAsync().awaitTerminated();
    }

    @Override
    public int getBestChainHeight() {
        return kit.chain().getBestChainHeight();
    }

    @Override
    public StoredBlock getChainHead() {
        return kit.chain().getChainHead();
    }

    @Override
    public StoredBlock getBlock(Sha256Hash hash) throws BlockStoreException {
        return kit.store().get(hash);
    }

    @Override
    public StoredBlock getBlockAtHeight(int height) throws BlockStoreException {
        return BridgeUtils.getStoredBlockAtHeight(kit.store(), height);
    }

    @Override
    public Set<Transaction> getTransactions(int minconfirmations) {
        Set<Transaction> txs = new HashSet<>();

        for (Transaction tx : kit.wallet().getTransactions(false)) {
            if (!tx.getConfidence().getConfidenceType().equals(TransactionConfidence.ConfidenceType.BUILDING))
                continue;

            if (tx.getConfidence().getDepthInBlocks() < minconfirmations)
                continue;

            txs.add(tx);
        }

        return txs;
    }
}
