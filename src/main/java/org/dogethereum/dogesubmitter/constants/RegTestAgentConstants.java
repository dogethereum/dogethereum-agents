package org.dogethereum.dogesubmitter.constants;

import org.bitcoinj.core.Coin;
import org.libdohj.params.DogecoinRegTestParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RegTest implementation of AgentConstants
 */
public class RegTestAgentConstants extends AgentConstants {

    private static final Logger log = LoggerFactory.getLogger("RegTestAgentConstants");

    private static RegTestAgentConstants instance = new RegTestAgentConstants();

    public static RegTestAgentConstants getInstance() {
        return instance;
    }

    RegTestAgentConstants() {
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
