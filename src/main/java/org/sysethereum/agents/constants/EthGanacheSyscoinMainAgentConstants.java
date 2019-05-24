package org.sysethereum.agents.constants;

import com.google.common.collect.Lists;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Sha256Hash;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;
import org.sysethereum.agents.core.syscoin.Superblock;
import org.sysethereum.agents.core.syscoin.SuperblockUtils;
import org.libdohj.params.SyscoinMainNetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;

/**
 * AgentConstants for Syscoin Mainnet and Eth Ganache.
 */
public class EthGanacheSyscoinMainAgentConstants extends AgentConstants {

    private static final Logger log = LoggerFactory.getLogger("LocalAgentConstants");

    private static EthGanacheSyscoinMainAgentConstants instance = new EthGanacheSyscoinMainAgentConstants();

    public static EthGanacheSyscoinMainAgentConstants getInstance() {
        return instance;
    }

    EthGanacheSyscoinMainAgentConstants() {
        syscoinParams = SyscoinMainNetParams.get();

        syscoinToEthTimerTaskPeriod = 10 * 1000;

        List<Sha256Hash> genesisSuperblockBlockList = Lists.newArrayList(syscoinParams.getGenesisBlock().getHash());
        Keccak256Hash genesisSuperblockParentId = Keccak256Hash.wrap(new byte[32]); // initialised with 0s
        long lastBlockHeight = 0;
        genesisSuperblock = new Superblock(
                syscoinParams, genesisSuperblockBlockList,
                BigInteger.valueOf(0x100001), syscoinParams.getGenesisBlock().getTimeSeconds(), 0,
                syscoinParams.getGenesisBlock().getDifficultyTarget(), genesisSuperblockParentId, 0, lastBlockHeight);


        defenderTimerTaskPeriod = 15 * 1000;
        challengerTimerTaskPeriod = 15 * 1000;
        defenderConfirmations = 1;
        challengerConfirmations = 1;

        ethInitialCheckpoint = 0;
        networkId = "32000"; // eth mainnet

    }
}
