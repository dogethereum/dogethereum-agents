package org.dogethereum.agents.constants;

import org.bitcoinj.core.Coin;
import org.libdohj.params.DogecoinRegTestParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AgentConstants for local tests.
 * Uses Doge RegTest and Eth Ganache.
 */
public class LocalAgentConstants extends AgentConstants {

    private static final Logger log = LoggerFactory.getLogger("LocalAgentConstants");

    private static LocalAgentConstants instance = new LocalAgentConstants();

    public static LocalAgentConstants getInstance() {
        return instance;
    }

    LocalAgentConstants() {
        dogeParams = DogecoinRegTestParams.get();
        doge2EthMinimumAcceptableConfirmations = 7;
        updateBridgeExecutionPeriod = 10 * 1000; // 10 seconds
        maxDogeHeadersPerRound = 5;
        minimumLockTxValue = Coin.valueOf(150000000); // 1.5 doge

        // Unlock mechanism specific start
        eth2DogeMinimumAcceptableConfirmations = 5;
        dogeBroadcastingMinimumAcceptableBlocks = 30;
        ethInitialCheckpoint = 0;
        // Unlock mechanism specific emd
    }
}
