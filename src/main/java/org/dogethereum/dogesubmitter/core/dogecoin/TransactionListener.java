package org.dogethereum.dogesubmitter.core.dogecoin;

import org.bitcoinj.core.Transaction;

public interface TransactionListener {
    void onTransaction(Transaction tx);
}
