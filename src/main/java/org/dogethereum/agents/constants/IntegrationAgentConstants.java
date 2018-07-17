package org.dogethereum.agents.constants;

import com.google.common.collect.Lists;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Sha256Hash;
import org.dogethereum.agents.core.dogecoin.Superblock;
import org.dogethereum.agents.core.dogecoin.SuperblockUtils;
import org.libdohj.params.DogecoinMainNetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        doge2EthMinimumAcceptableConfirmations = 7;
        updateBridgeExecutionPeriod = 1 * 60 * 1000; // 30 seconds
        maxDogeHeadersPerRound = 5;
        minimumLockTxValue = Coin.valueOf(150000000); // 1.5 doge

        // Genesis Superblock for doge mainnet
        // from block 6d18ea4de0f92208e52bdb6bd8320cf1ced16d4234a06dc7f1cb4dc5633a22b0
        // to block c577a73270eb1fccd4a702402089f653c771749763e0d7ebb877f47e81eb4395
        Sha256Hash blocksMerkleRoot = Sha256Hash.wrap("49548a60f34bef021845d5d6a8485f276ed89d391ed6779e9e4cbdf3bd2d39e5");
        BigInteger chainWork = new BigInteger("3294865331135006206033");
        long lastDogeBlockTime = 1531295965l;
        long previousToLastDogeBlockTime = 1531295930l;
        long lastDogeBlockBits = 436591711;
        Sha256Hash lastDogeBlockHash = Sha256Hash.wrap("c577a73270eb1fccd4a702402089f653c771749763e0d7ebb877f47e81eb4395");
        byte[] genesisSuperblockParentId = new byte[32]; // initialised with 0s
        long superblockHeight = 0;
        BigInteger status = SuperblockUtils.STATUS_APPROVED;
        long newSuperblockEventTime = 0;
        genesisSuperblock = new Superblock(
                blocksMerkleRoot, chainWork, lastDogeBlockTime,
                previousToLastDogeBlockTime, lastDogeBlockHash, lastDogeBlockBits,
                genesisSuperblockParentId, superblockHeight, status, newSuperblockEventTime);

        // Unlock mechanism specific start
        eth2DogeMinimumAcceptableConfirmations = 20;
        dogeBroadcastingMinimumAcceptableBlocks = 30;
        ethInitialCheckpoint = 3069702;
        // Unlock mechanism specific emd
    }
}
