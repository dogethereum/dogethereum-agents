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
 * AgentConstants for Doge Mainnet and Eth Ganache.
 */
public class EthGanacheDogeMainAgentConstants extends AgentConstants {

    private static final Logger log = LoggerFactory.getLogger("LocalAgentConstants");

    private static EthGanacheDogeMainAgentConstants instance = new EthGanacheDogeMainAgentConstants();

    public static EthGanacheDogeMainAgentConstants getInstance() {
        return instance;
    }

    EthGanacheDogeMainAgentConstants() {
        dogeParams = DogecoinMainNetParams.get();
        doge2EthMinimumAcceptableConfirmations = 7;
        updateBridgeExecutionPeriod = 10 * 1000; // 10 seconds
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
        byte[] genesisSuperblockParentId = new byte[32]; // initialised with 0s
        long superblockHeight = 0;
        BigInteger status = SuperblockUtils.STATUS_APPROVED;
        long newSuperblockEventTime = 0;
        genesisSuperblock = new Superblock(
                blocksMerkleRoot, chainWork, lastDogeBlockTime,
                previousToLastDogeBlockTime, lastDogeBlockHash, lastDogeBlockBits,
                genesisSuperblockParentId, superblockHeight);

        // Unlock mechanism specific start
        eth2DogeMinimumAcceptableConfirmations = 5;
        dogeBroadcastingMinimumAcceptableBlocks = 30;
        ethInitialCheckpoint = 0;
        // Unlock mechanism specific emd
    }
}
