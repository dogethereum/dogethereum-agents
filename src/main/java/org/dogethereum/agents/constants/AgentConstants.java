package org.dogethereum.agents.constants;

import org.bitcoinj.core.Coin;
import org.dogethereum.agents.core.dogecoin.Superblock;
import org.libdohj.params.AbstractDogecoinParams;

import java.math.BigInteger;

/**
 * Agent and Bridge constants.
 * Subclasses are customizations for each network (doge regtest and eth ropsten, doge mainnet and eth ropsten, doge mainnet and eth prod)
 */
public class AgentConstants {


    protected AbstractDogecoinParams dogeParams;

    // Minimum number of confirmations a tx has to have in order to EVALUATE relaying it to eth
    // Tx will be relayed only if they are part of an aproved superblock, so this value is just an optimization
    // for the agent.
    protected int doge2EthMinimumAcceptableConfirmations;
    protected int updateBridgeExecutionPeriod;
    protected Coin minimumLockTxValue;
    protected Superblock genesisSuperblock;
    protected long defenderTimerTaskPeriod;
    protected long challengerTimerTaskPeriod;
    protected long defenderConfirmations;
    protected long challengerConfirmations;

    public AbstractDogecoinParams getDogeParams() {
        return dogeParams;
    }
    public int getDoge2EthMinimumAcceptableConfirmations() { return doge2EthMinimumAcceptableConfirmations; }
    public int getUpdateBridgeExecutionPeriod() { return updateBridgeExecutionPeriod; }
    public Coin getMinimumLockTxValue() { return minimumLockTxValue; }

    // Unlock mechanism specific start
    protected int eth2DogeMinimumAcceptableConfirmations;
    protected int ethInitialCheckpoint;

    public int getEth2DogeMinimumAcceptableConfirmations() {
        return eth2DogeMinimumAcceptableConfirmations;
    }
    public int getEthInitialCheckpoint() {
        return ethInitialCheckpoint;
    }

    public Superblock getGenesisSuperblock() {
        return genesisSuperblock;
    }

    public static BigInteger getSuperblockInitialDeposit() {
        return BigInteger.valueOf(1000);
    }
    // Unlock mechanism specific end

    public long getDefenderTimerTaskPeriod() {
        return defenderTimerTaskPeriod;
    }

    public long getChallengerTimerTaskPeriod() {
        return challengerTimerTaskPeriod;
    }

    public long getDefenderConfirmations() {
        return defenderConfirmations - 1;
    }

    public long getChallengerConfirmations() {
        return challengerConfirmations - 1;
    }
}
