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
 * AgentConstants for Doge Mainnet and Eth Ganache.
 */
public class EthGanacheDogeMainAgentConstants extends AgentConstants {

    private static final Logger log = LoggerFactory.getLogger("LocalAgentConstants");

    private static EthGanacheDogeMainAgentConstants instance = new EthGanacheDogeMainAgentConstants();

    public static EthGanacheDogeMainAgentConstants getInstance() {
        return instance;
    }

    EthGanacheDogeMainAgentConstants() {
        dogeParams = DogecoinMainNetParams.get();

        dogeToEthTimerTaskPeriod = 10 * 1000;
        dogeToEthConfirmations = 1;
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
        defenderConfirmations = 1;
        challengerConfirmations = 1;

        ethToDogeTimerTaskPeriod = 15 * 1000;
        unlockConfirmations = 2;
        ethInitialCheckpoint = 0;

        priceOracleTimerTaskPeriod = 60 * 1000;
    }
}
