package org.dogethereum.dogesubmitter.constants;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Coin;
import org.bitcoinj.script.ScriptBuilder;
import org.libdohj.params.DogecoinMainNetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BridgeTestNetConstants extends BridgeConstants {

    private static final Logger logger = LoggerFactory.getLogger("BridgeTestNetConstants");

    private static BridgeTestNetConstants instance = new BridgeTestNetConstants();

    public static BridgeTestNetConstants getInstance() {
        return instance;
    }

    BridgeTestNetConstants() {
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
