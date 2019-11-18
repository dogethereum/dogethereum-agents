package org.sysethereum.agents.core.bridge.battle;

import org.sysethereum.agents.core.syscoin.Keccak256Hash;

public class SubmitterConvictedEvent {
    public Keccak256Hash superblockHash;
    public String submitter;

    public SubmitterConvictedEvent(Keccak256Hash superblockHash, String submitter) {
        this.superblockHash = superblockHash;
        this.submitter = submitter;
    }
}
