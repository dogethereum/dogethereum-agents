package org.sysethereum.agents.constants;

import org.libdohj.params.AbstractSyscoinParams;
import org.sysethereum.agents.core.bridge.SuperblockData;

import static org.sysethereum.agents.constants.AgentRole.CHALLENGER;

/**
 * Agent and Bridge constants.
 * Subclasses are customizations for each network
 */
public class AgentConstants {

    protected final AbstractSyscoinParams syscoinParams;

    protected final long syscoinToEthTimerTaskPeriod;
    protected final long syscoinToEthTimerTaskPeriodAggressive;
    // Minimum number of confirmations a tx has to have in order to EVALUATE relaying it to eth
    protected final SuperblockData genesisSuperblock;
    protected final long defenderTimerTaskPeriod;
    protected final long challengerTimerTaskPeriod;
    protected final long defenderConfirmations;
    protected final long challengerConfirmations;

    protected final String networkId;

    public AgentConstants(
            AbstractSyscoinParams syscoinParams,
            long syscoinToEthTimerTaskPeriod,
            long syscoinToEthTimerTaskPeriodAggressive,
            SuperblockData genesisSuperblock,
            long defenderTimerTaskPeriod,
            long challengerTimerTaskPeriod,
            long defenderConfirmations,
            long challengerConfirmations,
            String networkId
    ) {
        this.syscoinParams = syscoinParams;
        this.syscoinToEthTimerTaskPeriod = syscoinToEthTimerTaskPeriod;
        this.syscoinToEthTimerTaskPeriodAggressive = syscoinToEthTimerTaskPeriodAggressive;
        this.genesisSuperblock = genesisSuperblock;
        this.defenderTimerTaskPeriod = defenderTimerTaskPeriod;
        this.challengerTimerTaskPeriod = challengerTimerTaskPeriod;
        this.defenderConfirmations = defenderConfirmations;
        this.challengerConfirmations = challengerConfirmations;
        this.networkId = networkId;
    }

    public AbstractSyscoinParams getSyscoinParams() {
        return syscoinParams;
    }

    public long getSyscoinToEthTimerTaskPeriod() {
        return syscoinToEthTimerTaskPeriod;
    }
    public long getSyscoinToEthTimerTaskPeriodAggressive() {
        return syscoinToEthTimerTaskPeriodAggressive;
    }
    public SuperblockData getGenesisSuperblock() {
        return genesisSuperblock;
    }

    /**
     * @param agentRole
     * @return time in seconds
     */
    public long getTimerTaskPeriod(AgentRole agentRole) {
        return agentRole == CHALLENGER ? challengerTimerTaskPeriod : defenderTimerTaskPeriod;
    }
    public long getConfirmations(AgentRole agentRole) {
        return agentRole == CHALLENGER ? challengerConfirmations : defenderConfirmations;
    }

    public String getNetworkId() {
        return networkId;
    }


}
