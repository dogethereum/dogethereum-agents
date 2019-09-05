package org.sysethereum.agents.constants;

import com.google.common.collect.Lists;
import org.bitcoinj.core.Sha256Hash;
import org.libdohj.params.SyscoinTestNet3Params;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;
import org.sysethereum.agents.core.syscoin.Superblock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;

/**
 * AgentConstants for integration tests.
 * Uses Syscoin Mainnet and Eth Rinkeby.
 * Syscoin Mainnet is used for testing because Syscoin testnet is hard to use and mainnet syscoins are not that expensive.
 */
public class IntegrationAgentConstants extends AgentConstants {

    private static final Logger logger = LoggerFactory.getLogger("IntegrationAgentConstants");

    private static IntegrationAgentConstants instance = new IntegrationAgentConstants();

    public static IntegrationAgentConstants getInstance() {
        return instance;
    }

    IntegrationAgentConstants() {
        syscoinParams = SyscoinTestNet3Params.get();

        syscoinToEthTimerTaskPeriod = 15 * 1000;

        List<Sha256Hash> genesisSuperblockBlockList = Lists.newArrayList(Sha256Hash.wrap("000004fd0a684f4ce4d5e254f7230e7a620b3d5dc88a3facf555b8bab0a63f4b"));
        Keccak256Hash genesisSuperblockParentId = Keccak256Hash.wrap(new byte[32]); // initialised with 0s

        genesisSuperblock = new Superblock(
                syscoinParams, genesisSuperblockBlockList,
                new BigInteger("377487720"), 1566534575,504365055,
                genesisSuperblockParentId, 1);


        defenderTimerTaskPeriod = 15 * 1000;
        challengerTimerTaskPeriod = 15 * 1000;
        defenderConfirmations = 2;
        challengerConfirmations = 2;

        ethInitialCheckpoint = 4959851;
        networkId = "4"; // eth rinkeby 4; eth mainnet 1
        try {
            logger.info("genesisSuperblock Hash " + genesisSuperblock.getSuperblockId().toString());
        }
        catch(java.io.IOException exception){
            logger.info("exception " + exception.toString());
        }
        logger.info("genesisSuperblock " + genesisSuperblock.toString());

    }
}
