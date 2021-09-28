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

        superblockChainTimerTaskPeriod = 10 * 1000;
        superblockSubmitterTimerTaskPeriod = 10 * 1000;
        dogeTxRelayerTimerTaskPeriod = 10 * 1000;
        dogeToEthConfirmations = 1;
        minimumLockTxValue = Coin.valueOf(300000000); // 3 doge

        // Genesis Superblock for doge mainnet
        Sha256Hash blocksMerkleRoot = Sha256Hash.wrap("629417921bc4ab79db4a4a02b4d7946a4d0dbc6a3c5bca898dd12eacaeb8b353");
        BigInteger chainWork = new BigInteger("4266257060811936889868");
        long lastDogeBlockTime = 1535743139l;
        long previousToLastDogeBlockTime = 1535743100l;
        Sha256Hash lastDogeBlockHash = Sha256Hash.wrap("e2a056368784e63b9b5f9c17b613718ef7388a799e8535ab59be397019eff798");
        long lastDogeBlockBits = 436759445;
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
    }
}
