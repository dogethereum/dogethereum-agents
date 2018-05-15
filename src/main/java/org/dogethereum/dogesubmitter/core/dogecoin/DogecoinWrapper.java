package org.dogethereum.dogesubmitter.core.dogecoin;

import org.bitcoinj.core.PeerAddress;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.store.BlockStoreException;

import java.util.List;
import java.util.Set;

/**
 * Wrapper to the libdohj library
 */
public interface DogecoinWrapper {
    void setup(DogecoinWrapperListener dwListener, List<PeerAddress> peerAddresses);

    void start();

    void stop();

    int getBestChainHeight();

    StoredBlock getChainHead();

    StoredBlock getBlock(Sha256Hash hash) throws BlockStoreException;

    StoredBlock getBlockAtHeight(int height) throws BlockStoreException;

    Set<Transaction> getTransactions(int minconfirmations, boolean includeLock, boolean includeUnlock);
}
