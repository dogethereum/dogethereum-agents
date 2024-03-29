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

    protected long superblockchainUpdaterAgentTimerTaskPeriod;
    protected long superblockSubmitterAgentTimerTaskPeriod;
    protected long dogeTxRelayAgentTimerTaskPeriod;
    // Minimum number of confirmations a tx has to have in order to EVALUATE relaying it to eth
    // Tx will be relayed only if they are part of an aproved superblock, so this value is just an optimization
    // for the agent.
    protected int dogeToEthConfirmations;
    protected Coin minimumLockTxValue;
    protected Superblock genesisSuperblock;
    protected long defenderTimerTaskPeriod;
    protected long challengerTimerTaskPeriod;
    protected long defenderConfirmations;
    protected long challengerConfirmations;

    protected long signBroadcastDogeUnlockTxAgentTimerTaskPeriod;
    protected int unlockConfirmations;
    protected int ethInitialCheckpoint;


    public AbstractDogecoinParams getDogeParams() {
        return dogeParams;
    }

    public long getSuperblockchainUpdaterAgentTimerTaskPeriod() { return superblockchainUpdaterAgentTimerTaskPeriod; }
    public long getSuperblockSubmitterAgentTimerTaskPeriod() { return superblockSubmitterAgentTimerTaskPeriod; }
    public long getDogeTxRelayAgentTimerTaskPeriod() { return dogeTxRelayAgentTimerTaskPeriod; }
    public int getDogeToEthConfirmations() { return dogeToEthConfirmations; }
    public Coin getMinimumLockTxValue() { return minimumLockTxValue; }
    public Superblock getGenesisSuperblock() {
        return genesisSuperblock;
    }
    public static BigInteger getSuperblockInitialDeposit() {
        return BigInteger.valueOf(1000);
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

    public long getSignBroadcastDogeUnlockTxAgentTimerTaskPeriod() { return signBroadcastDogeUnlockTxAgentTimerTaskPeriod; }
    public int getUnlockConfirmations() {
        return unlockConfirmations;
    }
    public int getEthInitialCheckpoint() {
        return ethInitialCheckpoint;
    }

}
