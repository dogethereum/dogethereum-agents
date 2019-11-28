package org.sysethereum.agents.core.bridge.battle;

import org.sysethereum.agents.core.syscoin.Keccak256Hash;
import org.web3j.abi.datatypes.generated.Uint256;

public class SuperblockFailedEvent {
    public Keccak256Hash superblockHash;
    public String challenger;
    public Uint256 processCounter;

    public SuperblockFailedEvent(Keccak256Hash superblockHash, String challenger, Uint256 processCounter) {
        this.superblockHash = superblockHash;
        this.challenger = challenger;
        this.processCounter = processCounter;
    }
}