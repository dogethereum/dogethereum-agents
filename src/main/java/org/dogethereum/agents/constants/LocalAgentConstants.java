package org.dogethereum.agents.constants;

import com.google.common.collect.Lists;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Sha256Hash;
import org.dogethereum.agents.core.dogecoin.Keccak256Hash;
import org.dogethereum.agents.core.dogecoin.Superblock;
import org.libdohj.params.DogecoinRegTestParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;

/**
 * AgentConstants for local tests.
 * Uses Doge RegTest and Eth Ganache.
 */
public class LocalAgentConstants extends AgentConstants {

    private static final Logger log = LoggerFactory.getLogger("LocalAgentConstants");

    private static LocalAgentConstants instance = new LocalAgentConstants();

    public static LocalAgentConstants getInstance() {
        return instance;
    }

    LocalAgentConstants() {
        dogeParams = DogecoinRegTestParams.get();

        dogeToEthTimerTaskPeriod = 10 * 1000;
        dogeToEthConfirmations = 1;
        minimumLockTxValue = Coin.valueOf(300000000); // 3 doge

        List<Sha256Hash> genesisSuperblockBlockList = Lists.newArrayList(dogeParams.getGenesisBlock().getHash());
        Keccak256Hash genesisSuperblockParentId = Keccak256Hash.wrap(new byte[32]); // initialised with 0s
        long lastBlockHeight = 0;
        genesisSuperblock = new Superblock(
                dogeParams, genesisSuperblockBlockList,
                BigInteger.valueOf(0), dogeParams.getGenesisBlock().getTimeSeconds(), 0,
                dogeParams.getGenesisBlock().getDifficultyTarget(), genesisSuperblockParentId, 0, lastBlockHeight);
        defenderTimerTaskPeriod = 15 * 1000;
        challengerTimerTaskPeriod = 15 * 1000;
        defenderConfirmations = 1;
        challengerConfirmations = 1;

        ethToDogeTimerTaskPeriod = 15 * 1000;
        unlockConfirmations = 2;
        ethInitialCheckpoint = 0;

    }
}
