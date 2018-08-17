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

        dogeToEthTimerTaskPeriod = 15 * 1000;
        dogeToEthConfirmations = 2;
        minimumLockTxValue = Coin.valueOf(300000000); // 3 doge

        // Genesis Superblock for doge mainnet
        Sha256Hash blocksMerkleRoot = Sha256Hash.wrap("045162592c1002fa0f6cf39085881da54b86dea2634e6b5f55d8258ad2b7ee0c");
        BigInteger chainWork = new BigInteger("4018376769700331340387");
        long lastDogeBlockTime = 1534537759l;
        long previousToLastDogeBlockTime = 1534537657l;
        Sha256Hash lastDogeBlockHash = Sha256Hash.wrap("2f3053d4292e163931b61b39b6063494ad1ec0b5820b03ef787dbec30126ab2d");
        long lastDogeBlockBits = 436464932;
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
