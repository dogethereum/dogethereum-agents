package org.dogethereum.agents.constants;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Sha256Hash;
import org.dogethereum.agents.core.dogecoin.Keccak256Hash;
import org.dogethereum.agents.core.dogecoin.Superblock;
import org.dogethereum.agents.core.dogecoin.SuperblockUtils;
import org.libdohj.params.DogecoinMainNetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

/**
 * AgentConstants for integration tests.
 * Uses Doge Mainnet and Eth Rinkeby.
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

        dogeToEthTimerTaskPeriod = 60 * 1000;
        dogeToEthConfirmations = 2;
        minimumLockTxValue = Coin.valueOf(150000000); // 1.5 doge

        // Genesis Superblock for doge mainnet
        // from block 12ae7ab0226b11881b6899bbf2e5b902b774601b84d4eaac203b53005aaae33a
        // to block b0dbc74bc6e258e882a527206d67579cc231be743b23500a28015c96d66ed05a
        Sha256Hash blocksMerkleRoot = Sha256Hash.wrap("b56e1308bc44483551a5d3ba426d83c2b7634b6c985a3128e48544e8d0fe4ec2");
        BigInteger chainWork = new BigInteger("3752336556886305017875");
        long lastDogeBlockTime = 1533320063l;
        long previousToLastDogeBlockTime = 1533320029l;
        Sha256Hash lastDogeBlockHash = Sha256Hash.wrap("b0dbc74bc6e258e882a527206d67579cc231be743b23500a28015c96d66ed05a");
        long lastDogeBlockBits = 436473103;
        Keccak256Hash genesisSuperblockParentId = Keccak256Hash.wrap(new byte[32]); // initialised with 0s
        long superblockHeight = 0;
        BigInteger status = SuperblockUtils.STATUS_APPROVED;
        long newSuperblockEventTime = 0;
        genesisSuperblock = new Superblock(
                blocksMerkleRoot, chainWork, lastDogeBlockTime,
                previousToLastDogeBlockTime, lastDogeBlockHash, lastDogeBlockBits,
                genesisSuperblockParentId, superblockHeight);

        defenderTimerTaskPeriod = 15 * 1000;
        challengerTimerTaskPeriod = 15 * 1000;
        defenderConfirmations = 2;
        challengerConfirmations = 2;

        ethToDogeTimerTaskPeriod = 30 * 1000;
        unlockConfirmations = 4;
        ethInitialCheckpoint = 2766716;

        priceOracleTimerTaskPeriod = 3600 * 1000;
    }
}
