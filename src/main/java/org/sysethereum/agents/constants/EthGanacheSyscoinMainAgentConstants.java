package org.sysethereum.agents.constants;

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
public class EthGanacheSyscoinMainAgentConstants {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger("EthGanacheSyscoinMainAgentConstants");

    public EthGanacheSyscoinMainAgentConstants() {

    }

    public AgentConstants create() {
        var syscoinParams = SyscoinMainNetParams.get();
        var syscoinToEthTimerTaskPeriod = 10 * 1000;

        List<Sha256Hash> sysHashes = List.of(syscoinParams.getGenesisBlock().getHash());

        var genesisSuperblock = new SuperblockData(
                MerkleRootComputer.computeMerkleRoot(syscoinParams, sysHashes),
                sysHashes,
                new BigInteger("0x0000000000000000000000000000000000000000000b5aea51981d092e7d9739"),
                1562016306,
                1562016306,
                0,
                Keccak256Hash.wrap(new byte[32]), // initialised with 0s
                1
        );

        var defenderTimerTaskPeriod = 15 * 1000;
        var challengerTimerTaskPeriod = 15 * 1000;
        var defenderConfirmations = 1;
        var challengerConfirmations = 1;

        var ethInitialCheckpoint = 0;
        var networkId = "32000"; // eth mainnet

        return new AgentConstants(
            syscoinParams,
            syscoinToEthTimerTaskPeriod,
            genesisSuperblock,
            defenderTimerTaskPeriod,
            challengerTimerTaskPeriod,
            defenderConfirmations,
            challengerConfirmations,
            ethInitialCheckpoint,
            networkId
        );
    }
}
