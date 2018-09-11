package org.dogethereum.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.store.BlockStoreException;
import org.dogethereum.agents.constants.AgentConstants;
import org.dogethereum.agents.constants.SystemProperties;
import org.dogethereum.agents.core.dogecoin.DogecoinWrapper;
import org.dogethereum.agents.core.dogecoin.Superblock;
import org.dogethereum.agents.core.dogecoin.SuperblockChain;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private SuperblockChain superblockChain;

    @Autowired
    private DogecoinWrapper dogecoinWrapper;


    public SuperblockChainClient() {}

    @PostConstruct
    public void setup() throws Exception {
        SystemProperties config = SystemProperties.CONFIG;
        AgentConstants agentConstants = config.getAgentConstants();
        if (config.isDogeSuperblockSubmitterEnabled() || config.isDogeTxRelayerEnabled() ||
                config.isOperatorEnabled() || config.isDogeBlockChallengerEnabled() ||
                config.isMaliciousChallengerEnabled()) {
            new Timer("SuperblockChainClient").scheduleAtFixedRate(new UpdateSuperblocksTimerTask(),
                      getFirstExecutionDate(), agentConstants.getDogeToEthTimerTaskPeriod());
        }
    }

    private Date getFirstExecutionDate() {
        Calendar firstExecution = Calendar.getInstance();
        firstExecution.add(Calendar.SECOND, 1);
        return firstExecution.getTime();
    }

    /**
     * Builds and maintains a chain of superblocks from the whole Dogecoin blockchain.
     * Writes it to disk as specified by SuperblockLevelDBBlockStore.
     * @throws BlockStoreException
     * @throws IOException
     */
    public void updateChain() throws Exception, BlockStoreException, IOException {
        Superblock bestSuperblock = superblockChain.getChainHead();
        Sha256Hash bestSuperblockLastBlockHash = bestSuperblock.getLastDogeBlockHash();

        // get all the Dogecoin blocks that haven't yet been hashed into a superblock
        Stack<Sha256Hash> allDogeHashesToHash = getDogeBlockHashesNewerThan(bestSuperblockLastBlockHash);
        superblockChain.storeSuperblocks(allDogeHashesToHash, bestSuperblock.getSuperblockId()); // group them in superblocks
    }

    private Stack<Sha256Hash> getDogeBlockHashesNewerThan(Sha256Hash blockHash) throws BlockStoreException {
        Stack<Sha256Hash> hashes = new Stack<>();
        StoredBlock currentStoredBlock = dogecoinWrapper.getChainHead();

        while (!currentStoredBlock.getHeader().getHash().equals(blockHash)) {
            hashes.push(currentStoredBlock.getHeader().getHash());
            currentStoredBlock = dogecoinWrapper.getBlock(currentStoredBlock.getHeader().getPrevBlockHash());
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
                log.debug("UpdateSuperblocksTimerTask");
                updateChain();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
