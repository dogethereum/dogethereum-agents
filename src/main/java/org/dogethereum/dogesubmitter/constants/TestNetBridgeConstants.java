package org.dogethereum.dogesubmitter.constants;

import org.bitcoinj.core.Coin;
import org.libdohj.params.DogecoinMainNetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TestNet implementation of BridgeConstants
 */
public class TestNetBridgeConstants extends BridgeConstants {

    private static final Logger logger = LoggerFactory.getLogger("TestNetBridgeConstants");

    private static TestNetBridgeConstants instance = new TestNetBridgeConstants();

    public static TestNetBridgeConstants getInstance() {
        return instance;
    }

    TestNetBridgeConstants() {
        dogeParams = DogecoinMainNetParams.get();
        doge2EthMinimumAcceptableConfirmations = 7;
        updateBridgeExecutionPeriod = 1 * 60 * 1000; // 30 seconds
        maxDogeHeadersPerRound = 5;
        minimumLockTxValue = Coin.valueOf(1000000);

        // Unlock mechanism specific start
        eth2DogeMinimumAcceptableConfirmations = 20;
        dogeBroadcastingMinimumAcceptableBlocks = 30;
        ethInitialCheckpoint = 3069702;
        // Unlock mechanism specific emd
    }
}
