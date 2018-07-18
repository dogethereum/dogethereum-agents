package org.dogethereum.agents.constants;

import com.google.common.collect.Lists;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Sha256Hash;
import org.dogethereum.agents.core.dogecoin.Superblock;
import org.dogethereum.agents.core.dogecoin.SuperblockUtils;
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
        doge2EthMinimumAcceptableConfirmations = 7;
        updateBridgeExecutionPeriod = 10 * 1000; // 10 seconds
        maxDogeHeadersPerRound = 5;
        minimumLockTxValue = Coin.valueOf(150000000); // 1.5 doge

        List<Sha256Hash> genesisSuperblockBlockList = Lists.newArrayList(dogeParams.getGenesisBlock().getHash());
        byte[] genesisSuperblockParentId = new byte[32]; // initialised with 0s
        genesisSuperblock = new Superblock(
                dogeParams, genesisSuperblockBlockList, BigInteger.valueOf(0), dogeParams.getGenesisBlock().getTimeSeconds(),
                0, dogeParams.getGenesisBlock().getDifficultyTarget(),
                genesisSuperblockParentId, 0, SuperblockUtils.STATUS_APPROVED);

        // Unlock mechanism specific start
        eth2DogeMinimumAcceptableConfirmations = 5;
        dogeBroadcastingMinimumAcceptableBlocks = 30;
        ethInitialCheckpoint = 0;
        // Unlock mechanism specific emd
    }
}
