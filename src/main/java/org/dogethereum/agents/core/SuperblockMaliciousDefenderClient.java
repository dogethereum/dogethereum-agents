package org.dogethereum.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "SuperblockMaliciousDefenderClient")
public class SuperblockMaliciousDefenderClient extends SuperblockDefenderClient {

    @Override
    protected void setupClient() {
        myAddress = ethWrapper.getMaliciousSubmitterAddress();
        claimManager = ethWrapper.getMaliciousClaimManager();
    }

    @Override
    protected boolean isEnabled() {
        return config.isDogeMaliciousSubmitterEnabled();
    }
}
