package org.sysethereum.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.store.BlockStoreException;
import org.sysethereum.agents.constants.AgentConstants;
import org.sysethereum.agents.constants.SystemProperties;
import org.sysethereum.agents.core.syscoin.SyscoinWrapper;
import org.sysethereum.agents.core.bridge.Superblock;
import org.sysethereum.agents.core.syscoin.SuperblockChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static org.sysethereum.agents.constants.AgentRole.CHALLENGER;
import static org.sysethereum.agents.constants.AgentRole.SUBMITTER;

/**
 * Runs a SuperblockChain.
 * @author Catalina Juarros
 */
@Service
@Slf4j(topic = "SuperblockChainClient")
public class SuperblockChainClient {

    private static final Logger logger = LoggerFactory.getLogger("SuperblockChainClient");

    private final SystemProperties config;
    private final AgentConstants agentConstants;
    private final SuperblockChain localSuperblockChain;
    private final SyscoinWrapper syscoinWrapper;
    private final Timer timer;

    public SuperblockChainClient(
            SystemProperties systemProperties,
            AgentConstants agentConstants,
            SuperblockChain superblockChain,
            SyscoinWrapper syscoinWrapper
    ) {
        this.config = systemProperties;
        this.agentConstants = agentConstants;
        this.localSuperblockChain = superblockChain;
        this.syscoinWrapper = syscoinWrapper;
        this.timer = new Timer("SuperblockChainClient", true);
    }

    public boolean setup() {
        if (config.isAgentRoleEnabled(CHALLENGER) || config.isAgentRoleEnabled(SUBMITTER)) {
            try {
                timer.scheduleAtFixedRate(
                        new UpdateSuperblocksTimerTask(),
                        1_000,
                        agentConstants.getSyscoinToEthTimerTaskPeriod()
                );
            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }

    public void cleanUp() {
        timer.cancel();
        timer.purge();
        logger.info("cleanUp: Timer was canceled.");
    }

    /**
     * Builds and maintains a chain of superblocks from the whole Syscoin blockchain.
     * Writes it to disk as specified by SuperblockLevelDBBlockStore.
     * @throws BlockStoreException
     * @throws IOException
     */
    public void updateChain() throws Exception, BlockStoreException, IOException {
        Superblock bestSuperblock = localSuperblockChain.getChainHead();
        Sha256Hash bestSuperblockLastBlockHash = bestSuperblock.getLastSyscoinBlockHash();

        // Get all the Syscoin blocks that haven't yet been hashed into a superblock
        Stack<Sha256Hash> allSyscoinHashesToHash = syscoinWrapper.getNewerHashesThan(bestSuperblockLastBlockHash);
        localSuperblockChain.storeSuperblocks(allSyscoinHashesToHash, bestSuperblock.getHash()); // group them in superblocks
    }


    /**
     * Task to keep superblock chain updated whenever the agent is running.
     */
    private class UpdateSuperblocksTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                logger.debug("UpdateSuperblocksTimerTask");
                updateChain();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}