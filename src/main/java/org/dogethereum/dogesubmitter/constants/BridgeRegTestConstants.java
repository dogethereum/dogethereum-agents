package org.dogethereum.dogesubmitter.constants;

import org.bitcoinj.core.*;
import org.libdohj.params.DogecoinRegTestParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BridgeRegTestConstants extends BridgeConstants {

    private static final Logger log = LoggerFactory.getLogger("BridgeRegTestConstants");

    private static BridgeRegTestConstants instance = new BridgeRegTestConstants();

    public static BridgeRegTestConstants getInstance() {
        return instance;
    }

    BridgeRegTestConstants() {
        dogeParams = DogecoinRegTestParams.get();
        doge2EthMinimumAcceptableConfirmations = 7;
        updateBridgeExecutionPeriod = 20 * 1000; // 10 seconds
        maxDogeHeadersPerRound = 5;
        minimumLockTxValue = Coin.valueOf(1000000);

        // Unlock mechanism specific start
        eth2DogeMinimumAcceptableConfirmations = 5;
        dogeBroadcastingMinimumAcceptableBlocks = 30;
        ethInitialCheckpoint = 0;
        // Unlock mechanism specific emd
    }

}
