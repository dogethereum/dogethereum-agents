package org.sysethereum.agents.constants;

import org.bitcoinj.core.Coin;
import org.sysethereum.agents.core.syscoin.Superblock;
import org.libdohj.params.AbstractSyscoinParams;

import java.math.BigInteger;

/**
 * Agent and Bridge constants.
 * Subclasses are customizations for each network (syscoin regtest and eth ganache, syscoin mainnet and eth rinkeby, syscoin mainnet and eth prod)
 */
public class AgentConstants {


    protected AbstractSyscoinParams syscoinParams;

    protected long syscoinToEthTimerTaskPeriod;
    // Minimum number of confirmations a tx has to have in order to EVALUATE relaying it to eth
    protected Superblock genesisSuperblock;
    protected long defenderTimerTaskPeriod;
    protected long challengerTimerTaskPeriod;
    protected long defenderConfirmations;
    protected long challengerConfirmations;

    protected int ethInitialCheckpoint;
    protected String networkId;


    public AbstractSyscoinParams getSyscoinParams() {
        return syscoinParams;
    }

    public long getSyscoinToEthTimerTaskPeriod() { return syscoinToEthTimerTaskPeriod; }
    public Superblock getGenesisSuperblock() {
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
