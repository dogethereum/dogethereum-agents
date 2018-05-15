package org.dogethereum.dogesubmitter.constants;

import org.bitcoinj.core.Coin;
import org.libdohj.params.AbstractDogecoinParams;

/**
 * Agent and Bridge constants specific to each network (regtest, testnet, mainnet)
 */
public class AgentConstants {

    protected AbstractDogecoinParams dogeParams;
    protected int doge2EthMinimumAcceptableConfirmations;
    protected int updateBridgeExecutionPeriod;
    protected int maxDogeHeadersPerRound;
    protected Coin minimumLockTxValue;

    public AbstractDogecoinParams getDogeParams() {
        return dogeParams;
    }
    public int getDoge2EthMinimumAcceptableConfirmations() { return doge2EthMinimumAcceptableConfirmations; }
    public int getUpdateBridgeExecutionPeriod() { return updateBridgeExecutionPeriod; }
    public int getMaxDogeHeadersPerRound() { return maxDogeHeadersPerRound; }
    public Coin getMinimumLockTxValue() { return minimumLockTxValue; }

    // Unlock mechanism specific start
    protected int eth2DogeMinimumAcceptableConfirmations;
    protected int dogeBroadcastingMinimumAcceptableBlocks;
    protected int ethInitialCheckpoint;

    public int getEth2DogeMinimumAcceptableConfirmations() {
        return eth2DogeMinimumAcceptableConfirmations;
    }
    public int getDogeBroadcastingMinimumAcceptableBlocks() {
        return dogeBroadcastingMinimumAcceptableBlocks;
    }
    public int getEthInitialCheckpoint() {
        return ethInitialCheckpoint;
    }
    // Unlock mechanism specific end

}
