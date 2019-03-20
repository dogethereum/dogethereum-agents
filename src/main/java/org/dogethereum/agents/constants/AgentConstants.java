package org.dogethereum.agents.constants;

import org.bitcoinj.core.Coin;
import org.dogethereum.agents.core.dogecoin.Superblock;
import org.libdohj.params.AbstractDogecoinParams;

import java.math.BigInteger;

/**
 * Agent and Bridge constants.
 * Subclasses are customizations for each network (doge regtest and eth ganache, doge mainnet and eth rinkeby, doge mainnet and eth prod)
 */
public class AgentConstants {


    protected AbstractDogecoinParams dogeParams;

    protected long dogeToEthTimerTaskPeriod;
    // Minimum number of confirmations a tx has to have in order to EVALUATE relaying it to eth
    protected Superblock genesisSuperblock;
    protected long defenderTimerTaskPeriod;
    protected long challengerTimerTaskPeriod;
    protected long defenderConfirmations;
    protected long challengerConfirmations;

    protected long ethToDogeTimerTaskPeriod;
    protected int unlockConfirmations;
    protected int ethInitialCheckpoint;


    public AbstractDogecoinParams getDogeParams() {
        return dogeParams;
    }

    public long getDogeToEthTimerTaskPeriod() { return dogeToEthTimerTaskPeriod; }
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

    public long getEthToDogeTimerTaskPeriod() { return ethToDogeTimerTaskPeriod; }
    public int getUnlockConfirmations() {
        return unlockConfirmations;
    }
    public int getEthInitialCheckpoint() {
        return ethInitialCheckpoint;
    }


}
