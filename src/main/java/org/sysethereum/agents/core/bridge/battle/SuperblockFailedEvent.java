package org.sysethereum.agents.core.bridge.battle;

import org.sysethereum.agents.core.syscoin.Keccak256Hash;

public class SuperblockFailedEvent {
    public Keccak256Hash superblockHash;
    public String challenger;

    public SuperblockFailedEvent(Keccak256Hash superblockHash, String challenger) {
        this.superblockHash = superblockHash;
        this.challenger = challenger;
    }
}