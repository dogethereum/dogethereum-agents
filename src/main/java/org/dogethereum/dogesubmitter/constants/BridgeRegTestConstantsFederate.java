package org.dogethereum.dogesubmitter.constants;

import com.google.common.collect.Lists;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bouncycastle.util.encoders.Hex;
import org.libdohj.params.DogecoinRegTestParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;


public class BridgeRegTestConstantsFederate extends BridgeConstants {

    private static final Logger logger = LoggerFactory.getLogger("BridgeRegTestConstants");

    private static BridgeRegTestConstantsFederate instance = new BridgeRegTestConstantsFederate();

    public static BridgeRegTestConstantsFederate getInstance() {
        return instance;
    }

    BridgeRegTestConstantsFederate() {
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

        // To recreate the value use
        // federationAddressCreationTime = new GregorianCalendar(2018,1,1).getTimeInMillis() / 1000;
        // Currently set to:
        // Thu Feb 01 00:00:00 ART 2018

//        federationAddressCreationTime = 1517454000l;

        doge2EthMinimumAcceptableConfirmations = 7;

        updateBridgeExecutionPeriod = 20 * 1000; // 10 seconds

        maxDogeHeadersPerRound = 5;

        minimumLockTxValue = Coin.valueOf(1000000);

        // Unlock mechanism specific start

        ECKey federator0PrivateKey = ECKey.fromPrivate(Sha256Hash.hash("federator1".getBytes(StandardCharsets.UTF_8)));
        ECKey federator1PrivateKey = ECKey.fromPrivate(Sha256Hash.hash("federator2".getBytes(StandardCharsets.UTF_8)));
        ECKey federator2PrivateKey = ECKey.fromPrivate(Sha256Hash.hash("federator3".getBytes(StandardCharsets.UTF_8)));

        List federatorPrivateKeys = Lists.newArrayList(federator0PrivateKey, federator1PrivateKey, federator2PrivateKey);

        // To recalculate federator private keys
        // Hex.toHexString(ECKey.fromPrivate(HashUtil.sha3("federator1".getBytes())).getPubKey())
        // Current federator secrets: federator1, federator2, federator3
        ECKey federator0PublicKey = ECKey.fromPublicOnly(Hex.decode("0362634ab57dae9cb373a5d536e66a8c4f67468bbcfb063809bab643072d78a124"));
        ECKey federator1PublicKey = ECKey.fromPublicOnly(Hex.decode("03c5946b3fbae03a654237da863c9ed534e0878657175b132b8ca630f245df04db"));
        ECKey federator2PublicKey = ECKey.fromPublicOnly(Hex.decode("02cd53fc53a07f211641a677d250f6de99caf620e8e77071e811a28b3bcddf0be1"));

//        federatorPublicKeys = Lists.newArrayList(federator0PublicKey, federator1PublicKey, federator2PublicKey);

//        federatorsRequiredToSign = 2;

//        Script redeemScript = ScriptBuilder.createRedeemScript(federatorsRequiredToSign, federatorPublicKeys);
//        federationPubScript = ScriptBuilder.createP2SHOutputScript(redeemScript);
//      To recalculate federationAddress
//      federationAddress = Address.fromP2SHScript(btcParams, federationPubScript);
//        federationAddress = Address.fromBase58(dogeParams, "2N5muMepJizJE1gR7FbHJU6CD18V3BpNF9p");


        eth2DogeMinimumAcceptableConfirmations = 10;
        dogeBroadcastingMinimumAcceptableBlocks = 30;

        // Unlock mechanism specific emd

    }

}
