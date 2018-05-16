package org.dogethereum.dogesubmitter.core.dogecoin;


import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.wallet.Wallet;
import org.dogethereum.dogesubmitter.util.AgentUtils;
import org.dogethereum.dogesubmitter.constants.AgentConstants;
import org.dogethereum.dogesubmitter.util.FileUtils;
import org.dogethereum.dogesubmitter.util.OperatorPublicKeyHandler;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implements DogecoinWrapper
 */
public class DogecoinWrapper {
    private WalletAppKit kit;
    private Context dogeContext;
    private AgentConstants agentConstants;
    private File dataDirectory;
    private OperatorPublicKeyHandler keyHandler;

    public DogecoinWrapper(AgentConstants agentConstants, File dataDirectory, OperatorPublicKeyHandler keyHandler) {
        this.dogeContext = new Context(agentConstants.getDogeParams());
        this.agentConstants = agentConstants;
        this.dataDirectory = dataDirectory;
        this.keyHandler = keyHandler;
    }

    public void setup(final DogecoinWrapperListener dwListener, List<PeerAddress> peerAddresses) {
        kit = new WalletAppKit(dogeContext, dataDirectory, "DogeToEthClient") {
            @Override
            protected void onSetupCompleted() {
                Context.propagate(dogeContext);
                // When we receive a block that includes a tx that sends funds to eth via peg, store the PartialMerkleTree
                vPeerGroup.addBlocksDownloadedEventListener((peer, block, filteredBlock, blocksLeft) -> {
                    if (filteredBlock != null) {
                        // filteredBlock may be null if we are downloading just headers before fastCatchupTimeSecs
                        Context.propagate(dogeContext);
                        dwListener.onBlock(filteredBlock);
                    }
                });
                vWallet.addCoinsReceivedEventListener((wallet, tx, prevBalance, newBalance) -> coinsReceivedOrSent(tx));
                vWallet.addCoinsSentEventListener((wallet, tx, prevBalance, newBalance) -> coinsReceivedOrSent(tx));
                vPeerGroup.setDownloadTxDependencies(0);
            }

            private void coinsReceivedOrSent(Transaction tx) {
                Context.propagate(dogeContext);
                if (AgentUtils.isLockTx(tx, vWallet, agentConstants, keyHandler) || AgentUtils.isReleaseTx(tx, agentConstants, keyHandler)) {
                    dwListener.onTransaction(tx);
                }
            }

            @Override
            protected Wallet createWallet() {
                Wallet wallet = super.createWallet();
                Address address = keyHandler.getAddress();
                // Be notified when we receive doge so we call registerTransaction()
                wallet.addWatchedAddress(address, keyHandler.getAddressCreationTime());
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
                if (AgentUtils.isLockTx(tx, kit.wallet(), agentConstants, keyHandler) && includeLock ||
                    AgentUtils.isReleaseTx(tx, agentConstants, keyHandler) && includeUnlock) {
                    txs.add(tx);
                }
            }
        }
        return txs;
    }
}
