package org.sysethereum.agents.constants;

import com.google.common.collect.Lists;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Sha256Hash;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;
import org.sysethereum.agents.core.syscoin.Superblock;
import org.libdohj.params.SyscoinRegTestParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;

/**
 * AgentConstants for local tests.
 * Uses Syscoin RegTest and Eth Ganache.
 */
public class LocalAgentConstants extends AgentConstants {

    private static final Logger logger = LoggerFactory.getLogger("LocalAgentConstants");

    private static LocalAgentConstants instance = new LocalAgentConstants();

    public static LocalAgentConstants getInstance() {
        return instance;
    }

    LocalAgentConstants() {
        syscoinParams = SyscoinRegTestParams.get();

        syscoinToEthTimerTaskPeriod = 10 * 1000;

        List<Sha256Hash> genesisSuperblockBlockList = Lists.newArrayList(syscoinParams.getGenesisBlock().getHash());
        Keccak256Hash genesisSuperblockParentId = Keccak256Hash.wrap(new byte[32]); // initialised with 0s

        genesisSuperblock = new Superblock(
                syscoinParams, genesisSuperblockBlockList,
                BigInteger.valueOf(0), syscoinParams.getGenesisBlock().getTimeSeconds(),0,
                genesisSuperblockParentId, 0);
        defenderTimerTaskPeriod = 15 * 1000;
        challengerTimerTaskPeriod = 15 * 1000;
        defenderConfirmations = 1;
        challengerConfirmations = 1;

        ethInitialCheckpoint = 0;
        networkId = "32001"; // local eth network

    }
}
