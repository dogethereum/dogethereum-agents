package org.sysethereum.agents.core.bridge.battle;

import org.sysethereum.agents.core.syscoin.Keccak256Hash;

public class ChallengerConvictedEvent {
    public Keccak256Hash superblockHash;
    public Keccak256Hash sessionId;
    public String challenger;

    public ChallengerConvictedEvent(Keccak256Hash superblockHash, Keccak256Hash sessionId, String challenger) {
        this.superblockHash = superblockHash;
        this.sessionId = sessionId;
        this.challenger = challenger;
    }
}