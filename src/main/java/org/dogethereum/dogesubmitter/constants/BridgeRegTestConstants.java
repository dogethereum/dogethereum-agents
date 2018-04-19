package org.dogethereum.dogesubmitter.constants;

import com.google.common.collect.Lists;
import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bouncycastle.util.encoders.Hex;
import org.libdohj.params.DogecoinRegTestParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;


public class BridgeRegTestConstants extends BridgeConstants {

    private static final Logger logger = LoggerFactory.getLogger("BridgeRegTestConstants");

    private static BridgeRegTestConstants instance = new BridgeRegTestConstants();

    public static BridgeRegTestConstants getInstance() {
        return instance;
    }

    BridgeRegTestConstants() {
        dogeParams = DogecoinRegTestParams.get();


        // Key hash taken from DogeRelay's 2_deploy_contracts.js
        // Doge address is DCDDW6wYnykQDvKHwduc9m9G8GTJnbHuo4
        // Taken from tx from 2015 https://dogechain.info/tx/718add98dca8f54288b244dde3b0e797e8fe541477a08ef4b570ea2b07dccd3f
        //federationAddress = new Address(getDogeParams(), getDogeParams().getAddressHeader(), Hex.decode("4d905b4b815d483cdfabcd292c6f86509d0fad82"));
        //federationPubScript = ScriptBuilder.createOutputScript(federationAddress);

        // Address to use with tx https://dogechain.info/tx/158a1acc6fd0e0ef1b8d19711f5e4b572d6835db9bdf2d79b76aa732ad736b58
        // DR4U7ZJXrAjX4MSGCbJ1wSc3JcJSELQnBc
        // that appears on block 2.010.001 as a lock tx
        // Address based on hash 0x0000000000000000000000000000000000000004
        // new Address(params, params.getAddressHeader(), Hex.decode("0000000000000000000000000000000000000004"))
        //federationAddress = Address.fromBase58(getDogeParams(), "nUCAGGgZEPN1QyknmQe1oAku817btqFwUR");
        //federationPubScript = ScriptBuilder.createOutputScript(federationAddress);

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
