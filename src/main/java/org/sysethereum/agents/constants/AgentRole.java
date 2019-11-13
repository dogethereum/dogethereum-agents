package org.sysethereum.agents.constants;

public enum AgentRole {
    CHALLENGER ,
    SUBMITTER;

    public String getTimerTaskName() {
        if (this == CHALLENGER) {
            return "agents.superblock.CHALLENGER";
        } else {
            return "agents.superblock.SUBMITTER";
        }
    }
}
