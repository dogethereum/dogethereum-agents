package org.dogethereum.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "SuperblockFakeSubmitterClient")
public class SuperblockFakeSubmitterClient extends SuperblockDefenderClient {
    @Override
    protected void setupClient() {
        myAddress = ethWrapper.getMaliciousSubmitterAddress();
    }

    @Override
    protected boolean isEnabled() {
        return config.isDogeSuperblockFakeSubmitterEnabled();
    }

    @Override
    protected void reactToElapsedTime() {
        try {
            confirmFakeApprovableSuperblock();
            callBattleTimeouts();
            confirmAllSemiApprovable();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
