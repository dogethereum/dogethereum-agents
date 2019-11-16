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
public class EthGanacheSyscoinMainAgentConstantsFactory {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger("EthGanacheSyscoinMainAgentConstants");

    public EthGanacheSyscoinMainAgentConstantsFactory() {

    }

    public AgentConstants create() {
        var syscoinParams = SyscoinMainNetParams.get();
        var syscoinToEthTimerTaskPeriod = 45 * 1000;
        
        List<Sha256Hash> sysHashes = List.of(Sha256Hash.wrap("00000da80b8d7df5fae756f81315341a25f1e73c4760e10cbddacd9d3da05402"));

        var genesisSuperblock = new SuperblockData(
                MerkleRootComputer.computeMerkleRoot(syscoinParams, sysHashes),
                sysHashes,
                1572308963, 1572308958, 504365055,
                Keccak256Hash.wrap(new byte[32]), // initialised with 0s
                1
        );


        var defenderTimerTaskPeriod = 60 * 1000;
        var challengerTimerTaskPeriod = 60 * 1000;
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
