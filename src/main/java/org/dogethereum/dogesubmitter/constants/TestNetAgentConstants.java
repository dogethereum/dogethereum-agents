package org.dogethereum.dogesubmitter.constants;

import org.bitcoinj.core.Coin;
import org.libdohj.params.DogecoinMainNetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TestNet implementation of AgentConstants
 */
public class TestNetAgentConstants extends AgentConstants {

    private static final Logger logger = LoggerFactory.getLogger("TestNetAgentConstants");

    private static TestNetAgentConstants instance = new TestNetAgentConstants();

    public static TestNetAgentConstants getInstance() {
        return instance;
    }

    TestNetAgentConstants() {
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
