package org.sysethereum.agents.core.bridge.battle;

import org.sysethereum.agents.core.syscoin.Keccak256Hash;

public class ChallengerConvictedEvent {
    public Keccak256Hash superblockHash;
    public String challenger;

    public ChallengerConvictedEvent(Keccak256Hash superblockHash, String challenger) {
        this.superblockHash = superblockHash;
        this.challenger = challenger;
    }
}