package org.sysethereum.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.store.BlockStoreException;
import org.sysethereum.agents.constants.AgentConstants;
import org.sysethereum.agents.constants.SystemProperties;
import org.sysethereum.agents.core.syscoin.SyscoinWrapper;
import org.sysethereum.agents.core.syscoin.Superblock;
import org.sysethereum.agents.core.syscoin.SuperblockChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

/**
 * Runs a SuperblockChain.
 * @author Catalina Juarros
 */

@Service
@Slf4j(topic = "SuperblockChainClient")
public class SuperblockChainClient {

    private static final Logger logger = LoggerFactory.getLogger("SuperblockChainClient");
    private final SuperblockChain superblockChain;
    private final SyscoinWrapper syscoinWrapper;

    public SuperblockChainClient(
            SuperblockChain superblockChain,
            SyscoinWrapper syscoinWrapper
    ) {
        this.superblockChain = superblockChain;
        this.syscoinWrapper = syscoinWrapper;
    }

    @PostConstruct
    public void setup() {
        SystemProperties config = SystemProperties.CONFIG;
        AgentConstants agentConstants = config.getAgentConstants();
        if (config.isSyscoinSuperblockSubmitterEnabled() ||
                 config.isSyscoinBlockChallengerEnabled()) {
            new Timer("SuperblockChainClient").scheduleAtFixedRate(new UpdateSuperblocksTimerTask(),
                      getFirstExecutionDate(), agentConstants.getSyscoinToEthTimerTaskPeriod());
        }
    }

    private Date getFirstExecutionDate() {
        Calendar firstExecution = Calendar.getInstance();
        firstExecution.add(Calendar.SECOND, 1);
        return firstExecution.getTime();
    }

    /**
     * Builds and maintains a chain of superblocks from the whole Syscoin blockchain.
     * Writes it to disk as specified by SuperblockLevelDBBlockStore.
     * @throws BlockStoreException
     * @throws IOException
     */
    public void updateChain() throws Exception, BlockStoreException, IOException {
        Superblock bestSuperblock = superblockChain.getChainHead();
        Sha256Hash bestSuperblockLastBlockHash = bestSuperblock.getLastSyscoinBlockHash();

        // get all the Syscoin blocks that haven't yet been hashed into a superblock
        Stack<Sha256Hash> allSyscoinHashesToHash = getSyscoinBlockHashesNewerThan(bestSuperblockLastBlockHash);
        superblockChain.storeSuperblocks(allSyscoinHashesToHash, bestSuperblock.getSuperblockId()); // group them in superblocks
    }

    private Stack<Sha256Hash> getSyscoinBlockHashesNewerThan(Sha256Hash blockHash) throws BlockStoreException {
        Stack<Sha256Hash> hashes = new Stack<>();
        StoredBlock currentStoredBlock = syscoinWrapper.getChainHead();

        while (currentStoredBlock != null && !currentStoredBlock.getHeader().getHash().equals(blockHash)) {
            hashes.push(currentStoredBlock.getHeader().getHash());
            currentStoredBlock = syscoinWrapper.getBlock(currentStoredBlock.getHeader().getPrevBlockHash());
        }

        return hashes;
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