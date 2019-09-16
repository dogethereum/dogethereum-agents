/*
 * Copyright (C) 2017 RSK Labs Ltd.
 * Copyright (C) 2018 Coinfabrik and Oscar Guindzberg.
 * Copyright (C) 2019 Jagdeep Sidhu
 */
package org.sysethereum.agents.core;


import com.google.gson.Gson;
import org.sysethereum.agents.core.syscoin.*;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.bitcoinj.store.BlockStoreException;
import org.sysethereum.agents.constants.AgentConstants;
import org.sysethereum.agents.constants.SystemProperties;
import org.sysethereum.agents.core.syscoin.SyscoinWrapper;
import org.sysethereum.agents.core.eth.EthWrapper;
import org.sysethereum.agents.util.RestError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Manages the process of informing Sysethereum Contracts news about the syscoin blockchain
 * @author Oscar Guindzberg
 * @author Catalina Juarros
 */
@Service
@Slf4j(topic = "SyscoinToEthClient")
public class SyscoinToEthClient {
    private static final Logger logger = LoggerFactory.getLogger("SyscoinToEthClient");

    private final EthWrapper ethWrapper;
    private final SyscoinWrapper syscoinWrapper;
    private final SuperblockChain superblockChain;
    private final Gson gson;

    private SystemProperties config;
    private AgentConstants agentConstants;

    public SyscoinToEthClient(
            SuperblockChain superblockChain,
            SyscoinWrapper syscoinWrapper,
            EthWrapper ethWrapper,
            Gson gson
    ) {
        this.superblockChain = superblockChain;
        this.syscoinWrapper = syscoinWrapper;
        this.ethWrapper = ethWrapper;
        this.gson = gson;
    }

    @PostConstruct
    public void setup() {
        config = SystemProperties.CONFIG;
        if (config.isSyscoinSuperblockSubmitterEnabled()) {
            agentConstants = config.getAgentConstants();

            new Timer("Syscoin to Eth client").scheduleAtFixedRate(new SyscoinToEthClientTimerTask(),
                    getFirstExecutionDate(), agentConstants.getSyscoinToEthTimerTaskPeriod());

        }
    }

    private Date getFirstExecutionDate() {
        Calendar firstExecution = Calendar.getInstance();
        firstExecution.add(Calendar.SECOND, 20);
        return firstExecution.getTime();
    }

    @SuppressWarnings("unused")
    private class SyscoinToEthClientTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (!ethWrapper.isEthNodeSyncing()) {
                    logger.debug("SyscoinToEthClientTimerTask");
                    ethWrapper.updateContractFacadesGasPrice();
                    if (config.isSyscoinSuperblockSubmitterEnabled()) {
                        updateBridgeSuperblockChain();
                    }
                } else {
                    logger.warn("SyscoinToEthClientTimerTask skipped because the eth node is syncing blocks");
                }
            } catch (Exception e) {

                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Updates bridge with all the superblocks that the agent has but the bridge doesn't.
     * @return Number of superblocks sent to the bridge.
     * @throws Exception
     */
    public long updateBridgeSuperblockChain() throws Exception {
        if (ethWrapper.arePendingTransactionsForSendSuperblocksAddress()) {
            logger.debug("Skipping sending superblocks, there are pending transaction for the sender address.");
            return 0;
        }
        Keccak256Hash bestSuperblockId = ethWrapper.getBestSuperblockId();
        checkNotNull(bestSuperblockId, "No best chain superblock found");
        logger.debug("Best superblock {}.", bestSuperblockId);

        Keccak256Hash highestDescendantId ;
        Superblock highestDescendant = ethWrapper.getHighestSemiApprovedOrApprovedDescendant(bestSuperblockId);
        if (highestDescendant == null) {
            highestDescendantId = bestSuperblockId;
        }
        else
            highestDescendantId = highestDescendant.getSuperblockId();

        Superblock toConfirm = superblockChain.getFirstDescendant(highestDescendantId);
        if (toConfirm == null) {
            logger.info("Best superblock from contracts, {}, not found in local database. Stopping.", highestDescendantId);
            return 0;
        }

        if (!superblockChain.sendingTimePassed(toConfirm) || !ethWrapper.getAbilityToProposeNextSuperblock()) {
            logger.debug("Too early to send superblock {}, will try again in a few seconds.",
                    toConfirm.getSuperblockId());
            return 0;
        }

        if(!ethWrapper.sendStoreSuperblock(toConfirm, ethWrapper.getGeneralPurposeAndSendSuperblocksAddress())){
            return 0;
        }

        return toConfirm.getSuperblockHeight();
    }

    /**
     * Relays all unprocessed transactions to Ethereum contracts by calling sendRelayTx.
     * @throws Exception
     */
    public String getSuperblockSPVProof(Sha256Hash blockHash, int height) throws Exception {
        synchronized (this) {

            StoredBlock txStoredBlock;
            if(blockHash != null)
                txStoredBlock  = syscoinWrapper.getBlock(blockHash);
            else
                txStoredBlock  = syscoinWrapper.getStoredBlockAtHeight(height);
            if (txStoredBlock == null) {
                RestError spvProofError = new RestError("Block has not been stored in local database. Block hash: " + blockHash);
                return gson.toJson(spvProofError);
            }
            Superblock txSuperblock = findBestSuperblockFor(txStoredBlock.getHeader().getHash());

            if (txSuperblock == null) {
                RestError spvProofError = new RestError("Superblock has not been stored in local database yet. " +
                        "Block hash: " + txStoredBlock.getHeader().getHash());
                return gson.toJson(spvProofError);
            }

            if (!ethWrapper.isSuperblockApproved(txSuperblock.getSuperblockId())) {
                RestError spvProofError = new RestError("Superblock has not been approved yet. " +
                        "Block hash: " + txStoredBlock.getHeader().getHash() + ", superblock ID: " + txSuperblock.getSuperblockId());
                return gson.toJson(spvProofError);
            }

            int syscoinBlockIndex = txSuperblock.getSyscoinBlockLeafIndex(txStoredBlock.getHeader().getHash());
            byte[] includeBits = new byte[(int) Math.ceil(txSuperblock.getSyscoinBlockHashes().size() / 8.0)];
            Utils.setBitLE(includeBits, syscoinBlockIndex);
            SuperblockPartialMerkleTree superblockPMT = SuperblockPartialMerkleTree.buildFromLeaves(agentConstants.getSyscoinParams(),
                    includeBits, txSuperblock.getSyscoinBlockHashes());

            return ethWrapper.getSuperblockSPVProof((AltcoinBlock) txStoredBlock.getHeader(), txSuperblock, superblockPMT);
        }
    }

    private static class SuperBlockResponse {
        public final String merkleRoot;
        public final long lastSyscoinBlockTime;
        public final String lastSyscoinBlockHash;
        public final long lastSyscoinBlockBits;
        public final String parentId;
        public final String superblockId;
        public final long superblockHeight;
        public final boolean approved;

        public SuperBlockResponse(Superblock sbIn, boolean approvedIn) throws IOException {
            this.merkleRoot = sbIn.getMerkleRoot().toString();
            this.lastSyscoinBlockTime = sbIn.getLastSyscoinBlockTime();
            this.lastSyscoinBlockHash = sbIn.getLastSyscoinBlockHash().toString();
            this.lastSyscoinBlockBits = sbIn.getlastSyscoinBlockBits();
            this.parentId = sbIn.getParentId().toString();
            this.superblockId = sbIn.getSuperblockId().toString();
            this.superblockHeight = sbIn.getSuperblockHeight();
            this.approved = approvedIn;
        }
    }

    public String getSuperblock(Keccak256Hash superblockId, int height) throws Exception {
        synchronized (this) {

            Superblock txSuperblock;
            if (superblockId != null)
                txSuperblock =  superblockChain.getSuperblock(superblockId);
            else
                txSuperblock =  superblockChain.getSuperblockByHeight(height);

            return getJsonResponse(txSuperblock);
        }
    }

    public String getSuperblockBySyscoinBlock(Sha256Hash blockHash, int height) throws Exception {
        synchronized (this) {

            StoredBlock txStoredBlock;
            if (blockHash != null)
                txStoredBlock = syscoinWrapper.getBlock(blockHash);
            else
                txStoredBlock = syscoinWrapper.getStoredBlockAtHeight(height);
            if (txStoredBlock == null) {
                RestError spvProofError = new RestError("Block has not been stored in local database.");
                return gson.toJson(spvProofError);
            }
            Superblock txSuperblock = findBestSuperblockFor(txStoredBlock.getHeader().getHash());

            return getJsonResponse(txSuperblock);
        }
    }

    private String getJsonResponse(Superblock txSuperblock) throws Exception {
        if (txSuperblock == null) {
            RestError spvProofError = new RestError("Superblock has not been stored in local database yet.");
            return gson.toJson(spvProofError);
        }
        SuperBlockResponse response = new SuperBlockResponse(txSuperblock, ethWrapper.isSuperblockApproved(txSuperblock.getSuperblockId()));
        return gson.toJson(response);
    }

    /**
     * Finds the superblock in the superblock main chain that contains the block identified by `blockHash`.
     * @param blockHash SHA-256 hash of a block that we want to find.
     * @return Superblock where the block can be found.
     * @throws BlockStoreException
     */
    private Superblock findBestSuperblockFor(Sha256Hash blockHash) throws BlockStoreException, IOException {
        Superblock currentSuperblock = superblockChain.getChainHead();

        while (currentSuperblock != null) {
            if (currentSuperblock.hasSyscoinBlock(blockHash))
                return currentSuperblock;
            currentSuperblock = superblockChain.getSuperblock(currentSuperblock.getParentId());
        }

        // current superblock is null
        return null;
    }

}

