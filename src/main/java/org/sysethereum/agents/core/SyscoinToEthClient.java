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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.datatypes.generated.Bytes32;

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
    private static final Logger log = LoggerFactory.getLogger("LocalAgentConstants");

    @Autowired
    private EthWrapper ethWrapper;

    private RestServer restServer;
    private SystemProperties config;

    private AgentConstants agentConstants;

    @Autowired
    private SyscoinWrapper syscoinWrapper;

    @Autowired
    private SuperblockChain superblockChain;

    public SyscoinToEthClient() {}


    @PostConstruct
    public void setup() throws Exception {
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
                    log.debug("SyscoinToEthClientTimerTask");
                    ethWrapper.updateContractFacadesGasPrice();
                    if (config.isSyscoinSuperblockSubmitterEnabled()) {
                        updateBridgeSuperblockChain();
                    }
                } else {
                    log.warn("SyscoinToEthClientTimerTask skipped because the eth node is syncing blocks");
                }
            } catch (Exception e) {

                log.error(e.getMessage(), e);
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
            log.debug("Skipping sending superblocks, there are pending transaction for the sender address.");
            return 0;
        }

        // Get the best superblock from the relay that is also in the main chain.
        List<Bytes32> superblockLocator = ethWrapper.getSuperblockLocator();
        Superblock matchedSuperblock = getEarliestMatchingSuperblock(superblockLocator);

        checkNotNull(matchedSuperblock, "No best chain superblock found");
        log.debug("Matched superblock {}.", matchedSuperblock.getSuperblockId());

        // We found the superblock in the agent's best chain. Send the earliest superblock that the relay is missing.
        Superblock toSend = superblockChain.getFirstDescendant(matchedSuperblock.getSuperblockId());

        if (toSend == null) {
            log.debug("Bridge was just updated, no new superblocks to send. matchedSuperblock: {}.",
                    matchedSuperblock.getSuperblockId());
            return 0;
        }

        if (!superblockChain.sendingTimePassed(toSend)) {
            log.debug("Too early to send superblock {}, will try again in a few seconds.",
                    toSend.getSuperblockId());
            return 0;
        }


        ethWrapper.sendStoreSuperblock(toSend, ethWrapper.getGeneralPurposeAndSendSuperblocksAddress());

        return toSend.getSuperblockHeight();
    }

    /**
     * Helper method for updateBridgeSuperblockChain().
     * Gets the earliest superblock from the bridge's superblock locator
     * that was also found in the agent's main chain.
     * @param superblockLocator List of ancestors provided by the bridge.
     * @return Earliest matched block if it is found,
     *         null otherwise.
     * @throws BlockStoreException
     * @throws IOException
     */
    private Superblock getEarliestMatchingSuperblock(List<Bytes32> superblockLocator)
            throws BlockStoreException, IOException {
        Superblock matchedSuperblock = null;

        for (int i = 0; i < superblockLocator.size(); i++) {
            Keccak256Hash superblockBridgeHash = Keccak256Hash.wrap(superblockLocator.get(i).getValue());
            Superblock bridgeSuperblock = superblockChain.getSuperblock(superblockBridgeHash);

            if (bridgeSuperblock == null)
                continue;

            Superblock bestRelaySuperblockInLocalChain =
                    superblockChain.getSuperblockByHeight(bridgeSuperblock.getSuperblockHeight());

            if (bestRelaySuperblockInLocalChain != null && bridgeSuperblock.getSuperblockId().equals(bestRelaySuperblockInLocalChain.getSuperblockId())) {
                matchedSuperblock = bestRelaySuperblockInLocalChain;
                break;
            }
        }

        return matchedSuperblock;
    }
    /**
     * Relays all unprocessed transactions to Ethereum contracts by calling sendRelayTx.
     * @throws Exception
     */
    public String getSuperblockSPVProof(Sha256Hash blockHash, int height) throws Exception {
        synchronized (this) {

            StoredBlock txStoredBlock = null;
            if(blockHash != null)
                txStoredBlock  = syscoinWrapper.getBlock(blockHash);
            else
                txStoredBlock  = syscoinWrapper.getStoredBlockAtHeight(height);
            if (txStoredBlock == null) {
                Gson g = new Gson();
                RestError spvProofError = new RestError("Block has not been stored in local database. Block hash: " + blockHash);
                return g.toJson(spvProofError);
            }
            Superblock txSuperblock = findBestSuperblockFor(txStoredBlock.getHeader().getHash());

            if (txSuperblock == null) {
                Gson g = new Gson();
                RestError spvProofError = new RestError("Superblock has not been stored in local database yet. " +
                        "Block hash: " + txStoredBlock.getHeader().getHash());
                return g.toJson(spvProofError);
            }

            if (!ethWrapper.isSuperblockApproved(txSuperblock.getSuperblockId())) {
                Gson g = new Gson();
                RestError spvProofError = new RestError("Superblock has not been approved yet. " +
                        "Block hash: " + txStoredBlock.getHeader().getHash() + ", superblock ID: " + txSuperblock.getSuperblockId());
                return g.toJson(spvProofError);
            }

            int syscoinBlockIndex = txSuperblock.getSyscoinBlockLeafIndex(txStoredBlock.getHeader().getHash());
            byte[] includeBits = new byte[(int) Math.ceil(txSuperblock.getSyscoinBlockHashes().size() / 8.0)];
            Utils.setBitLE(includeBits, syscoinBlockIndex);
            SuperblockPartialMerkleTree superblockPMT = SuperblockPartialMerkleTree.buildFromLeaves(agentConstants.getSyscoinParams(),
                    includeBits, txSuperblock.getSyscoinBlockHashes());

            return ethWrapper.getSuperblockSPVProof((AltcoinBlock) txStoredBlock.getHeader(), txSuperblock, superblockPMT);
        }
    }
    private class SuperBlockResponse {
        public String merkleRoot;
        public long lastSyscoinBlockTime;
        public String lastSyscoinBlockHash;
        public long previousSyscoinBlockTime;
        public long previousSyscoinBlockBits;
        public String parentId;
        public String superblockId;
        public long superblockHeight;
        public long blockHeight;
        public boolean approved;
        public SuperBlockResponse(Superblock sbIn, boolean approvedIn) throws IOException {
            this.merkleRoot = sbIn.getMerkleRoot().toString();
            this.lastSyscoinBlockTime = sbIn.getLastSyscoinBlockTime();
            this.lastSyscoinBlockHash = sbIn.getLastSyscoinBlockHash().toString();
            this.previousSyscoinBlockTime = sbIn.getpreviousSyscoinBlockTime();
            this.previousSyscoinBlockBits = sbIn.getpreviousSyscoinBlockBits();
            this.parentId = sbIn.getParentId().toString();
            this.superblockId = sbIn.getSuperblockId().toString();
            this.superblockHeight = sbIn.getSuperblockHeight();
            this.blockHeight = sbIn.getBlockHeight();
            this.approved = approvedIn;
        }
    }
    public String getSuperblock(Keccak256Hash superblockId, int height) throws Exception {
        synchronized (this) {

            Superblock txSuperblock  = null;
            if (superblockId != null)
                txSuperblock =  superblockChain.getSuperblock(superblockId);
            else
                txSuperblock =  superblockChain.getSuperblockByHeight(height);

            if (txSuperblock == null) {
                Gson g = new Gson();
                RestError spvProofError = new RestError("Superblock has not been stored in local database yet.");
                return g.toJson(spvProofError);
            }
            SuperBlockResponse response = new SuperBlockResponse(txSuperblock, ethWrapper.isSuperblockApproved(txSuperblock.getSuperblockId()));
            Gson g = new Gson();
            return g.toJson(response);
        }
    }
    public String getSuperblockBySyscoinBlock(Sha256Hash blockHash, int height) throws Exception {
        synchronized (this) {

            StoredBlock txStoredBlock = null;
            if (blockHash != null)
                txStoredBlock = syscoinWrapper.getBlock(blockHash);
            else
                txStoredBlock = syscoinWrapper.getStoredBlockAtHeight(height);
            if (txStoredBlock == null) {
                Gson g = new Gson();
                RestError spvProofError = new RestError("Block has not been stored in local database.");
                return g.toJson(spvProofError);
            }
            Superblock txSuperblock = findBestSuperblockFor(txStoredBlock.getHeader().getHash());

            if (txSuperblock == null) {
                Gson g = new Gson();
                RestError spvProofError = new RestError("Superblock has not been stored in local database yet.");
                return g.toJson(spvProofError);
            }
            SuperBlockResponse response = new SuperBlockResponse(txSuperblock, ethWrapper.isSuperblockApproved(txSuperblock.getSuperblockId()));
            Gson g = new Gson();
            return g.toJson(response);
        }
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

