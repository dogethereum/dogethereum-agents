package org.dogethereum.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Monitors the Ethereum blockchain for superblock-related events
 * and challenges invalid submissions.
 * @author Catalina Juarros
 * @author Ismael Bejarano
 */

@Service
@Slf4j(topic = "SuperblockChallengerClient")
public class SuperblockChallengerClient extends SuperblockBaseClient {

    public SuperblockChallengerClient() {
        super("Superblock challenger client");
    }

    @Override
    protected void setupClient() {
        myAddress = ethWrapper.getDogeBlockChallengerAddress();
    }

    @Override
    public void reactToEvents(long fromBlock, long toBlock) {
        try {
            latestEthBlockProcessed = toBlock;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    protected void reactToElapsedTime() {}

    /* ---- STATUS SETTERS ---- */

    /* ---- CONFIRMING/DEFENDING ---- */

    /* ---- OVERRRIDE ABSTRACT METHODS ---- */

    @Override
    protected Boolean isEnabled() {
        return config.isDogeBlockChallengerEnabled();
    }

    @Override
    protected String getLastEthBlockProcessedFilename() {
        return "SuperblockChallengerLatestEthBlockProcessedFile.dat";
    }
}
