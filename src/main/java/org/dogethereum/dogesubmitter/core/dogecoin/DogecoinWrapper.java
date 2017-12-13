package org.dogethereum.dogesubmitter.core.dogecoin;

import org.bitcoinj.core.*;
import org.bitcoinj.store.BlockStoreException;

import java.util.List;
import java.util.Set;


public interface DogecoinWrapper {
    void setup(BlockListener blockListener, TransactionListener transactionListener, List<PeerAddress> peerAddresses);

    void start();

    void stop();

    int getBestChainHeight();

    StoredBlock getChainHead();

    StoredBlock getBlock(Sha256Hash hash) throws BlockStoreException;

    StoredBlock getBlockAtHeight(int height) throws BlockStoreException;

    Set<Transaction> getTransactions(int minconfirmations);
}
