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
        minimumLockTxValue = Coin.valueOf(150000000); // 1.5 doge

        // Genesis Superblock for doge mainnet
        Sha256Hash blocksMerkleRoot = Sha256Hash.wrap("d6ba2a22aae9bbe860f7df8be83cc05dfd584121aaa1868a1926a8acec3fecb3");
        BigInteger chainWork = new BigInteger("3832331074689355151779");
        long lastDogeBlockTime = 1533678578l;
        long previousToLastDogeBlockTime = 1533678523l;
        Sha256Hash lastDogeBlockHash = Sha256Hash.wrap("c2c204e82ff21092797bf451acaaf9a2074bd650e2f0dca366cca7dd0cd94d9e");
        long lastDogeBlockBits = 436541183;
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
