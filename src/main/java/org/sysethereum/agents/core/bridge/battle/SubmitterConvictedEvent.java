package org.sysethereum.agents.core.bridge.battle;

import org.sysethereum.agents.core.syscoin.Keccak256Hash;

public class SubmitterConvictedEvent {
    public Keccak256Hash superblockHash;
    public Keccak256Hash sessionId;
    public String submitter;

    public SubmitterConvictedEvent(Keccak256Hash superblockHash, Keccak256Hash sessionId, String submitter) {
        this.superblockHash = superblockHash;
        this.sessionId = sessionId;
        this.submitter = submitter;
    }
}
