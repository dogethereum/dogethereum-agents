package org.sysethereum.agents.core.bridge.battle;

import org.sysethereum.agents.core.syscoin.Keccak256Hash;
import org.web3j.abi.datatypes.generated.Uint256;

public class SuperblockSuccessfulEvent {
    public Keccak256Hash superblockHash;
    public String submitter;
    public Uint256 processCounter;

    public SuperblockSuccessfulEvent(Keccak256Hash superblockHash, String submitter, Uint256 processCounter) {
        this.superblockHash = superblockHash;
        this.submitter = submitter;
        this.processCounter = processCounter;
    }
}
