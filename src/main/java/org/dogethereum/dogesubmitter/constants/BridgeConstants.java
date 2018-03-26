package org.dogethereum.dogesubmitter.constants;

import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
import org.libdohj.params.AbstractDogecoinParams;

import java.util.List;

public class BridgeConstants {

    protected AbstractDogecoinParams dogeParams;

    //protected Script federationPubScript;
    //protected Address federationAddress;

    protected int doge2EthMinimumAcceptableConfirmations;

    protected int updateBridgeExecutionPeriod;

    protected int maxDogeHeadersPerRound;

    protected Coin minimumLockTxValue;

    public AbstractDogecoinParams getDogeParams() {
        return dogeParams;
    }

//    public Script getFederationPubScript() {
//        return federationPubScript;
//    }

//    public Address getFederationAddress() {
//        return federationAddress;
//    }

    public int getDoge2EthMinimumAcceptableConfirmations() {
        return doge2EthMinimumAcceptableConfirmations;
    }


    public int getUpdateBridgeExecutionPeriod() { return updateBridgeExecutionPeriod; }

    public int getMaxDogeHeadersPerRound() { return maxDogeHeadersPerRound; }

    public Coin getMinimumLockTxValue() { return minimumLockTxValue; }

    // Unlock mechanism specific start


    // protected List<ECKey> federatorPublicKeys;
    // protected int federatorsRequiredToSign;
    protected int eth2DogeMinimumAcceptableConfirmations;
    protected int dogeBroadcastingMinimumAcceptableBlocks;
    protected Coin minimumReleaseTxValue;

    //public List<ECKey> getFederatorPublicKeys() {
    //     return federatorPublicKeys;
    // }
    // public int getFederatorsRequiredToSign() {
    //    return federatorsRequiredToSign;
    // }
    public int getEth2DogeMinimumAcceptableConfirmations() {
        return eth2DogeMinimumAcceptableConfirmations;
    }
    public int getDogeBroadcastingMinimumAcceptableBlocks() {
        return dogeBroadcastingMinimumAcceptableBlocks;
    }
    public Coin getMinimumReleaseTxValue() { return minimumReleaseTxValue; }

    // Unlock mechanism specific end

}
