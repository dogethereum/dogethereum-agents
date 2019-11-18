package org.sysethereum.agents.core.bridge.battle;

import org.sysethereum.agents.constants.AgentRole;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;

import static org.sysethereum.agents.constants.AgentRole.CHALLENGER;

public class NewBattleEvent {
    public Keccak256Hash superblockHash;
    public String submitter;
    public String challenger;

    public String getAddressByRole(AgentRole agentRole) {
        return agentRole == CHALLENGER ? challenger : submitter;
    }
}
