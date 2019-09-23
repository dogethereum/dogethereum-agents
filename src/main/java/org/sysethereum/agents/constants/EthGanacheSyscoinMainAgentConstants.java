package org.sysethereum.agents.constants;

import com.google.common.collect.Lists;
import org.bitcoinj.core.Sha256Hash;
import org.libdohj.params.SyscoinMainNetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sysethereum.agents.core.bridge.SuperblockData;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;
import org.sysethereum.agents.service.rest.MerkleRootComputer;

import java.math.BigInteger;
import java.util.List;

/**
 * AgentConstants for Syscoin Mainnet and Eth Ganache.
 */
public class EthGanacheSyscoinMainAgentConstants extends AgentConstants {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger("EthGanacheSyscoinMainAgentConstants");

    public EthGanacheSyscoinMainAgentConstants() {

        syscoinParams = SyscoinMainNetParams.get();
        syscoinToEthTimerTaskPeriod = 10 * 1000;

        List<Sha256Hash> genesisSuperblockBlockList = Lists.newArrayList(syscoinParams.getGenesisBlock().getHash());
        Keccak256Hash genesisSuperblockParentId = Keccak256Hash.wrap(new byte[32]); // initialised with 0s

        genesisSuperblock = new SuperblockData(
                MerkleRootComputer.computeMerkleRoot(syscoinParams, genesisSuperblockBlockList),
                genesisSuperblockBlockList,
                new BigInteger("0x0000000000000000000000000000000000000000000b5aea51981d092e7d9739"),
                1562016306,
                0,
                genesisSuperblockParentId,
                0
        );

        defenderTimerTaskPeriod = 15 * 1000;
        challengerTimerTaskPeriod = 15 * 1000;
        defenderConfirmations = 1;
        challengerConfirmations = 1;

        ethInitialCheckpoint = 0;
        networkId = "32000"; // eth mainnet
    }
}
