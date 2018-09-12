package org.dogethereum.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.dogethereum.agents.core.eth.EthWrapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * This class is not intended to be in any actual releases - it's only for testing defender functionality.
 * @author Catalina Juarros
 */

@Service
@Slf4j(topic = "SuperblockMaliciousChallengerClient")
public class SuperblockMaliciousChallengerClient extends SuperblockChallengerClient {

    private boolean challenge = true;
    private int challengedSuperblocks = 0;

//    @Override
//    protected void setupClient() {
//        myAddress = ethWrapper.getMaliciousChallengerAddress();
//        submitterAddress = ethWrapper.getGeneralPurposeAndSendSuperblocksAddress();
//    }

    @Override
    public long reactToEvents(long fromBlock, long toBlock) {
        try {
            challengeFirstSuperblock(fromBlock, toBlock);
            respondToNewBattles(fromBlock, toBlock);
            respondToMerkleRootHashesEventResponses(fromBlock, toBlock);
            respondToBlockHeaderEventResponses(fromBlock, toBlock);
            respondToResolveScryptHashValidation(fromBlock, toBlock);

            // Maintain data structures
            getSemiApproved(fromBlock, toBlock);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return latestEthBlockProcessed;
        }
        return toBlock;
    }

    @Override
    protected boolean isEnabled() {
        return config.isMaliciousChallengerEnabled();
    }

    private void challengeFirstSuperblock(long fromBlock, long toBlock) throws InterruptedException, IOException {
        List<EthWrapper.SuperblockEvent> newSuperblockEvents = ethWrapper.getNewSuperblocks(fromBlock, toBlock);
        String submitterAddress = ethWrapper.getGeneralPurposeAndSendSuperblocksAddress();
        for (EthWrapper.SuperblockEvent newSuperblockEvent : newSuperblockEvents) {
            if (challenge) {
                // Only challenge the genesis superblock and first superblock submitted by the agent.
                ethWrapper.challengeSuperblock(newSuperblockEvent.superblockId);
                challengedSuperblocks++;
                if (challengedSuperblocks == 2)
                    challenge = false;
            }
        }
    }
}
