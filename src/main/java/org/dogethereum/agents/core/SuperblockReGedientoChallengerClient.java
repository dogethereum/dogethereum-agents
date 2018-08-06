package org.dogethereum.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.dogethereum.agents.core.eth.EthWrapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j(topic = "SuperblockReGedientoChallengerClient")
public class SuperblockReGedientoChallengerClient extends SuperblockChallengerClient {

    @Override
    public long reactToEvents(long fromBlock, long toBlock) {
        try {
            challengeEverything(fromBlock, toBlock);
            respondToNewBattles(fromBlock, toBlock);
            logVerificationGames(fromBlock, toBlock);
            deleteFinishedBattles(fromBlock, toBlock);

            getSemiApproved(fromBlock, toBlock);
            removeApproved(fromBlock, toBlock);
            removeInvalid(fromBlock, toBlock);

            synchronized (this) {
                flushSemiApprovedSet();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return latestEthBlockProcessed;
        }
        return toBlock;
    }

    private void challengeEverything(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.SuperblockEvent> newSuperblockEvents = ethWrapper.getNewSuperblocks(fromBlock, toBlock);
        log.info("Challenging everything");
        for (EthWrapper.SuperblockEvent superblockEvent : newSuperblockEvents) {
            log.info("Challenging superblock {}", superblockEvent.superblockId);
            ethWrapper.challengeSuperblock(superblockEvent.superblockId);
            Thread.sleep(200);
            log.debug("/// Superblock claim exists: {}", ethWrapper.getClaimExists(superblockEvent.superblockId));
            log.debug("/// Superblock claim verification ongoing: {}",
                    ethWrapper.getClaimVerificationOngoing(superblockEvent.superblockId));
            log.debug("/// Superblock claim challengers: {}",
                    ethWrapper.getClaimChallengers(superblockEvent.superblockId));
            log.debug(myAddress);
        }
    }

    @Override
    protected boolean isEnabled() {
        return config.isReGedientoChallengerEnabled();
    }

    private void logErrors(long fromBlock, long toBlock) throws Exception {
        log.debug("/////// Getting errors");
        List<EthWrapper.ErrorClaimEvent> errorClaimEvents = ethWrapper.getErrorClaimEvents(fromBlock, toBlock);
        for (EthWrapper.ErrorClaimEvent errorClaimEvent : errorClaimEvents) {
            log.debug("Error {} on superblock {}", errorClaimEvent.err, errorClaimEvent.superblockId);
        }
    }

    private void logVerificationGames(long fromBlock, long toBlock) throws Exception {
        log.debug("////// Getting verification games");
        List<EthWrapper.VerificationGameStartedEvent> verificationGameStartedEvents =
                ethWrapper.getVerificationGameStartedEvents(fromBlock, toBlock);
        for (EthWrapper.VerificationGameStartedEvent verificationGameStartedEvent : verificationGameStartedEvents) {
            log.debug("Verification game {} started for superblock {}",
                    verificationGameStartedEvent.sessionId, verificationGameStartedEvent.superblockId);
        }
    }
}
