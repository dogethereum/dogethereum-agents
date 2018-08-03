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
            validateNewSuperblocks(fromBlock, toBlock);
            challengeEverything(fromBlock, toBlock);
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
        }
    }
}
