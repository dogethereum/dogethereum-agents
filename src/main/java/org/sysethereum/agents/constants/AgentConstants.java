package org.sysethereum.agents.constants;

import org.libdohj.params.AbstractSyscoinParams;
import org.sysethereum.agents.core.bridge.SuperblockData;

/**
 * Agent and Bridge constants.
 * Subclasses are customizations for each network (syscoin regtest and eth ganache, syscoin mainnet and eth rinkeby, syscoin mainnet and eth prod)
 */
public class AgentConstants {

    protected final AbstractSyscoinParams syscoinParams;

    protected final long syscoinToEthTimerTaskPeriod;
    // Minimum number of confirmations a tx has to have in order to EVALUATE relaying it to eth
    protected final SuperblockData genesisSuperblock;
    protected final long defenderTimerTaskPeriod;
    protected final long challengerTimerTaskPeriod;
    protected final long defenderConfirmations;
    protected final long challengerConfirmations;

    protected final int ethInitialCheckpoint;
    protected final String networkId;

    public AgentConstants(
            AbstractSyscoinParams syscoinParams,
            long syscoinToEthTimerTaskPeriod,
            SuperblockData genesisSuperblock,
            long defenderTimerTaskPeriod,
            long challengerTimerTaskPeriod,
            long defenderConfirmations,
            long challengerConfirmations,
            int ethInitialCheckpoint,
            String networkId
    ) {
        this.syscoinParams = syscoinParams;
        this.syscoinToEthTimerTaskPeriod = syscoinToEthTimerTaskPeriod;
        this.genesisSuperblock = genesisSuperblock;
        this.defenderTimerTaskPeriod = defenderTimerTaskPeriod;
        this.challengerTimerTaskPeriod = challengerTimerTaskPeriod;
        this.defenderConfirmations = defenderConfirmations;
        this.challengerConfirmations = challengerConfirmations;
        this.ethInitialCheckpoint = ethInitialCheckpoint;
        this.networkId = networkId;
    }

    public AbstractSyscoinParams getSyscoinParams() {
        return syscoinParams;
    }

    public long getSyscoinToEthTimerTaskPeriod() {
        return syscoinToEthTimerTaskPeriod;
    }

    public SuperblockData getGenesisSuperblock() {
        return genesisSuperblock;
    }

    public long getDefenderTimerTaskPeriod() {
        return defenderTimerTaskPeriod;
    }

    public long getChallengerTimerTaskPeriod() {
        return challengerTimerTaskPeriod;
    }

    public long getDefenderConfirmations() {
        return defenderConfirmations;
    }

    public long getChallengerConfirmations() {
        return challengerConfirmations;
    }

    public String getNetworkId() {
        return networkId;
    }

    public int getEthInitialCheckpoint() {
        return ethInitialCheckpoint;
    }

}
