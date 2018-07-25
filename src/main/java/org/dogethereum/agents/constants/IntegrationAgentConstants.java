package org.dogethereum.agents.constants;

import com.google.common.collect.Lists;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Sha256Hash;
import org.dogethereum.agents.core.dogecoin.Keccak256Hash;
import org.dogethereum.agents.core.dogecoin.Superblock;
import org.dogethereum.agents.core.dogecoin.SuperblockUtils;
import org.libdohj.params.DogecoinMainNetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * AgentConstants for integration tests.
 * Uses Doge Mainnet and Eth Ropsten.
 * Doge Mainnet is used for testing because Doge testnet is hard to use and mainnet doges are not that expensive.
 */
public class IntegrationAgentConstants extends AgentConstants {

    private static final Logger logger = LoggerFactory.getLogger("IntegrationAgentConstants");

    private static IntegrationAgentConstants instance = new IntegrationAgentConstants();

    public static IntegrationAgentConstants getInstance() {
        return instance;
    }

    IntegrationAgentConstants() {
        dogeParams = DogecoinMainNetParams.get();
        doge2EthMinimumAcceptableConfirmations = 1;
        updateBridgeExecutionPeriod = 1 * 60 * 1000; // 30 seconds
        maxDogeHeadersPerRound = 5;
        minimumLockTxValue = Coin.valueOf(150000000); // 1.5 doge

        // Genesis Superblock for doge mainnet
        // from block 674962fc4d1ebf8620c0f772d838a957c0dbdd757898903baad7c83c606f3634
        // to block 046722472396fe2883a725f97f0e63036d2064ceb271bccc175578b724833b3f
        Sha256Hash blocksMerkleRoot = Sha256Hash.wrap("1eb62592c39990b4d33b55eac0989ec9ad69099aced17b8adc56ed561b28b473");
        BigInteger chainWork = new BigInteger("3434911961284113526919");
        long lastDogeBlockTime = 1531922574l;
        long previousToLastDogeBlockTime = 1531922557l;
        Sha256Hash lastDogeBlockHash = Sha256Hash.wrap("046722472396fe2883a725f97f0e63036d2064ceb271bccc175578b724833b3f");
        long lastDogeBlockBits = 436643408;
        Keccak256Hash genesisSuperblockParentId = Keccak256Hash.wrap(new byte[32]); // initialised with 0s
        long superblockHeight = 0;
        BigInteger status = SuperblockUtils.STATUS_APPROVED;
        long newSuperblockEventTime = 0;
        genesisSuperblock = new Superblock(
                blocksMerkleRoot, chainWork, lastDogeBlockTime,
                previousToLastDogeBlockTime, lastDogeBlockHash, lastDogeBlockBits,
                genesisSuperblockParentId, superblockHeight);

        // Unlock mechanism specific start
        eth2DogeMinimumAcceptableConfirmations = 10;
        dogeBroadcastingMinimumAcceptableBlocks = 30;
        ethInitialCheckpoint = 3657000;
        // Unlock mechanism specific emd
    }
}
