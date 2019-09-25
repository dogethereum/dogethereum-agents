package org.sysethereum.agents.constants;

import org.bitcoinj.core.Sha256Hash;
import org.libdohj.params.SyscoinTestNet3Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sysethereum.agents.core.bridge.SuperblockData;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;
import org.sysethereum.agents.service.rest.MerkleRootComputer;

import java.math.BigInteger;
import java.util.List;

/**
 * AgentConstants for integration tests.
 * Uses Syscoin Mainnet and Eth Rinkeby.
 * Syscoin Mainnet is used for testing because Syscoin testnet is hard to use and mainnet syscoins are not that expensive.
 */
public class IntegrationAgentConstants {

    private static final Logger logger = LoggerFactory.getLogger("IntegrationAgentConstants");

    public IntegrationAgentConstants() {
    }

    public AgentConstants create() {
        var syscoinParams = SyscoinTestNet3Params.get();

        var syscoinToEthTimerTaskPeriod = 15 * 1000;

        List<Sha256Hash> sysHashes = List.of(Sha256Hash.wrap("000004fd0a684f4ce4d5e254f7230e7a620b3d5dc88a3facf555b8bab0a63f4b"));

        var genesisSuperblock = new SuperblockData(
                MerkleRootComputer.computeMerkleRoot(syscoinParams, sysHashes),
                sysHashes,
                new BigInteger("377487720"), 1566534575, 1566534575, 504365055,
                Keccak256Hash.wrap(new byte[32]), // initialised with 0s
                1
        );

        var defenderTimerTaskPeriod = 15 * 1000;
        var challengerTimerTaskPeriod = 15 * 1000;
        var defenderConfirmations = 2;
        var challengerConfirmations = 2;

        var ethInitialCheckpoint = 4959851;
        var networkId = "4"; // eth rinkeby 4; eth mainnet 1

        logger.info("genesisSuperblock " + genesisSuperblock.toString());

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
