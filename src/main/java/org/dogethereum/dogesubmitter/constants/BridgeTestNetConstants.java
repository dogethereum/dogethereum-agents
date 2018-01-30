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


        try {
            // Key hash taken from DogeRelay's 2_deploy_contracts.js
            // Doge address is DCDDW6wYnykQDvKHwduc9m9G8GTJnbHuo4
            // Taken from tx from 2015 https://dogechain.info/tx/718add98dca8f54288b244dde3b0e797e8fe541477a08ef4b570ea2b07dccd3f
            //federationAddress = new Address(getDogeParams(), getDogeParams().getAddressHeader(), Hex.decode("4d905b4b815d483cdfabcd292c6f86509d0fad82"));

            // Address to use with tx https://dogechain.info/tx/158a1acc6fd0e0ef1b8d19711f5e4b572d6835db9bdf2d79b76aa732ad736b58
            // DR4U7ZJXrAjX4MSGCbJ1wSc3JcJSELQnBc
            // that appears on block 2.010.001 as a lock tx


            // Address based on hash 0x0000000000000000000000000000000000000002
            // new Address(params, params.getAddressHeader(), Hex.decode("0000000000000000000000000000000000000002"))
            federationAddress = Address.fromBase58(getDogeParams(), "D596YFweJQuHY1BbjazZYmAbt8jJe1axaD");
            federationPubScript = ScriptBuilder.createOutputScript(federationAddress);
        } catch (AddressFormatException e) {
            logger.error("Federation address format is invalid");
            throw new RuntimeException(e.getMessage(), e);
        }

        // To recreate the value use
        // federationAddressCreationTime = new GregorianCalendar(2018,0,30).getTimeInMillis() / 1000;
        // Currently set to:
        // Tue Jan 30 00:00:00 ART 2018
        federationAddressCreationTime = 1517281200l;

        doge2EthMinimumAcceptableConfirmations = 7;

        updateBridgeExecutionPeriod = 1 * 60 * 1000; // 30 seconds

        maxDogeHeadersPerRound = 5;

        minimumLockTxValue = Coin.valueOf(1000000);

//        Release mechanism specific
//        ECKey federator0PublicKey = ECKey.fromPublicOnly(Hex.decode("039a060badbeb24bee49eb2063f616c0f0f0765d4ca646b20a88ce828f259fcdb9"));
//        ECKey federator1PublicKey = ECKey.fromPublicOnly(Hex.decode("02afc230c2d355b1a577682b07bc2646041b5d0177af0f98395a46018da699b6da"));
//        ECKey federator2PublicKey = ECKey.fromPublicOnly(Hex.decode("0206262e6bb2dceea515f77fae4928d87002a04b72f721034e1d4fbf3d84b16c72"));
//        ECKey federator3PublicKey = ECKey.fromPublicOnly(Hex.decode("03481d38fd2113f289347b7fd47e428127de02a078a7e28089ebe0150b74d9dcf7"));
//        ECKey federator4PublicKey = ECKey.fromPublicOnly(Hex.decode("029868937807b41dac42ff5a5b4a1d1711c4f3454f5826933465aa2614c5e90fdf"));
//        ECKey federator5PublicKey = ECKey.fromPublicOnly(Hex.decode("03c83e2dc1fbeaa54f0a8e8d482d46a32ef721322a4910a756fb07713f2dddbcb9"));
//        ECKey federator6PublicKey = ECKey.fromPublicOnly(Hex.decode("02629b1e976a5ed02194c0680f4f7b30f8388b51e935796ccee35e5b0fad915c3a"));
//        ECKey federator7PublicKey = ECKey.fromPublicOnly(Hex.decode("032ed58056d205829824c3693cc2f9285565b068a29661c37bc90f431b147f8e55"));
//        ECKey federator8PublicKey = ECKey.fromPublicOnly(Hex.decode("023552f8144c944ffa220724cd4b5f455d75eaf59b17f73bdc1a7177a3e9bf7871"));
//        ECKey federator9PublicKey = ECKey.fromPublicOnly(Hex.decode("036d9d9bf6fa85bbb00a18b34e0d8baecf32c330c4b1920419c415e1005355f498"));
//        ECKey federator10PublicKey = ECKey.fromPublicOnly(Hex.decode("03bb42b0d32e781b88319dbc3aadc43c7a032c1931b641f5ae8340b8891bfdedbd"));
//        ECKey federator11PublicKey = ECKey.fromPublicOnly(Hex.decode("03dece3c5f5b7df1ae3f4542c38dd25932e332d9e960c2c1f24712657626498705"));
//        ECKey federator12PublicKey = ECKey.fromPublicOnly(Hex.decode("033965f98e9ec741fdd3281f5cf2a2a0ae89958f4bf4f6862ee73ac9bf2b49e0c7"));
//        ECKey federator13PublicKey = ECKey.fromPublicOnly(Hex.decode("0297d72f4c58b62495adbd49398b39d8fca69c6714ecaec49bd09e9dfcd9dc35cf"));
//        ECKey federator14PublicKey = ECKey.fromPublicOnly(Hex.decode("03c5dc2281b1bf3a8db339dceb4867bbcca1a633f3a65d5f80f6e8aca35b9b191c"));
//        federatorPublicKeys = Lists.newArrayList(federator0PublicKey, federator1PublicKey, federator2PublicKey,
//                federator3PublicKey, federator4PublicKey, federator5PublicKey,
//                federator6PublicKey, federator7PublicKey, federator8PublicKey,
//                federator9PublicKey, federator10PublicKey, federator11PublicKey,
//                federator12PublicKey, federator13PublicKey, federator14PublicKey);
//        federatorsRequiredToSign = 7;
//        Script redeemScript = ScriptBuilder.createRedeemScript(federatorsRequiredToSign, federatorPublicKeys);
//        federationPubScript = ScriptBuilder.createP2SHOutputScript(redeemScript);
////      To recalculate federationAddress
////      federationAddress = Address.fromP2SHScript(dogeParams, federationPubScript);
//        eth2DogeMinimumAcceptableConfirmations = 10;
//        dogeBroadcastingMinimumAcceptableBlocks = 30;
//        minimumReleaseTxValue = Coin.valueOf(500000);


    }

}
