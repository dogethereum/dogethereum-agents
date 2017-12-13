package org.dogethereum.dogesubmitter.core.dogecoin;


import org.bitcoinj.core.FilteredBlock;

public interface BlockListener {
    void onBlock(FilteredBlock block);
}
