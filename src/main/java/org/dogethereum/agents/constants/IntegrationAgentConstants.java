package org.dogethereum.agents.constants;

import org.bitcoinj.core.Coin;
import org.libdohj.params.DogecoinMainNetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AgentConstants for integration tests.
 * Uses Doge Mainnet and Eth Ropsten.
 * Doge Mainnet is used for testing because Doge testnet is hard to use and mainnet doges are not that expensive.
 */
public class IntegrationAgentConstants extends AgentConstants {

    private static final Logger logger = LoggerFactory.getLogger("IntegrationAgentConstants");

    private static IntegrationAgentConstants instance = new IntegrationAgentConstants();

    public static IntegrationAgentConstants getInstance() {
        return instance;
    }

    IntegrationAgentConstants() {
        dogeParams = DogecoinMainNetParams.get();
        doge2EthMinimumAcceptableConfirmations = 7;
        updateBridgeExecutionPeriod = 1 * 60 * 1000; // 30 seconds
        maxDogeHeadersPerRound = 5;
        minimumLockTxValue = Coin.valueOf(150000000); // 1.5 doge

        // Unlock mechanism specific start
        eth2DogeMinimumAcceptableConfirmations = 20;
        dogeBroadcastingMinimumAcceptableBlocks = 30;
        ethInitialCheckpoint = 3069702;
        // Unlock mechanism specific emd
    }
}
