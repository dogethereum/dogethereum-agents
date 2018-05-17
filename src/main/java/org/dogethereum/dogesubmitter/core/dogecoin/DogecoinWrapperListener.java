package org.dogethereum.dogesubmitter.core.dogecoin;


import org.bitcoinj.core.FilteredBlock;
import org.bitcoinj.core.Transaction;

public interface DogecoinWrapperListener {
    void onBlock(FilteredBlock block);
    void onTransaction(Transaction tx);
}
