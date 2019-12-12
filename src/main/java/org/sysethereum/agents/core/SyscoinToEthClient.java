/*
 * Copyright (C) 2017 RSK Labs Ltd.
 * Copyright (C) 2018 Coinfabrik and Oscar Guindzberg.
 * Copyright (C) 2019 Jagdeep Sidhu
 */
package org.sysethereum.agents.core;


import org.springframework.beans.factory.annotation.Autowired;
import org.sysethereum.agents.constants.EthAddresses;
import org.sysethereum.agents.core.bridge.Superblock;
import org.sysethereum.agents.core.bridge.SuperblockContractApi;
import org.sysethereum.agents.core.eth.SuperblockSPVProof;
import org.sysethereum.agents.core.syscoin.*;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.sysethereum.agents.constants.AgentConstants;
import org.sysethereum.agents.core.eth.EthWrapper;
import org.sysethereum.agents.util.RestError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.web3j.utils.Numeric;

import javax.annotation.Nullable;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

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
    private final SuperblockChain localSuperblockChain;
    private final SuperblockContractApi superblockContractApi;
    private final EthAddresses ethAddresses;

    private final AgentConstants agentConstants;
    private Timer timer;
    private final Context syscoinContext;
    @Autowired
    public SyscoinToEthClient(
            Context syscoinContext,
            AgentConstants agentConstants,
            SuperblockChain superblockChain,
            SyscoinWrapper syscoinWrapper,
            EthWrapper ethWrapper,
            SuperblockContractApi superblockContractApi,
            EthAddresses ethAddresses
    ) {
        this.syscoinContext = syscoinContext;
        this.agentConstants = agentConstants;
        this.localSuperblockChain = superblockChain;
        this.syscoinWrapper = syscoinWrapper;
        this.ethWrapper = ethWrapper;
        this.superblockContractApi = superblockContractApi;
        this.ethAddresses = ethAddresses;
        this.timer = new Timer("Syscoin to Eth client", true);
    }
    public boolean setupTimer(){
        try {
            timer.cancel();
            timer.purge();
            timer = new Timer("Syscoin to Eth client", true);
            timer.scheduleAtFixedRate(new SyscoinToEthClientTimerTask(), ethWrapper.getAggressiveMode()? 0: 20_000, ethWrapper.getAggressiveMode()? agentConstants.getSyscoinToEthTimerTaskPeriodAggressive(): agentConstants.getSyscoinToEthTimerTaskPeriod());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    public boolean setup() {
        return setupTimer();
    }

    public void cleanUp() {
        timer.cancel();
        timer.purge();
        logger.info("cleanUp: Timer was canceled.");
    }

    private class SyscoinToEthClientTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (!ethWrapper.isEthNodeSyncing()) {
                    logger.debug("SyscoinToEthClientTimerTask");
                    ethWrapper.updateContractFacadesGasPrice();
                    updateBridgeSuperblockChain();
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
     * @throws Exception
     */
    public void updateBridgeSuperblockChain() throws Exception {
        if (ethWrapper.arePendingTransactionsForSendSuperblocksAddress()) {
            logger.debug("Skipping sending superblocks, there are pending transaction for the sender address.");
            return;
        }
        Keccak256Hash bestSuperblockId = superblockContractApi.getBestSuperblockId();
        checkNotNull(bestSuperblockId, "No best chain superblock found");
        logger.debug("Best superblock {}.", bestSuperblockId);

        Keccak256Hash highestDescendantId ;
        Superblock highestDescendant = ethWrapper.getHighestSemiApprovedOrApprovedDescendant(bestSuperblockId);
        if (highestDescendant == null) {
            highestDescendantId = bestSuperblockId;
        }
        else
            highestDescendantId = highestDescendant.getHash();

        Superblock toConfirm = localSuperblockChain.getFirstDescendant(highestDescendantId);
        if (toConfirm == null) {
            logger.info("No new superblock to submit found in local database. Last processed superblockId {}. Stopping.", highestDescendantId);
            return;
        }

        if (!localSuperblockChain.sendingTimePassed(toConfirm)) {
            logger.debug("Too early to send superblock {}, will try again in a few seconds.", toConfirm.getHash());
            return;
        }

        ethWrapper.sendStoreSuperblock(toConfirm, ethAddresses.generalPurposeAddress);
    }

    /**
     * Relays all unprocessed transactions to Ethereum contracts by calling sendRelayTx.
     * @throws Exception
     */
    public Object getSuperblockSPVProof(Sha256Hash blockHash, int height, boolean isApprovedCheck) throws Exception {
        synchronized (this) {
            Context.propagate(syscoinContext);
            StoredBlock txStoredBlock;
            if(blockHash != null)
                txStoredBlock  = syscoinWrapper.getBlock(blockHash);
            else
                txStoredBlock  = syscoinWrapper.getStoredBlockAtHeight(height);
            if (txStoredBlock == null) {
                return new RestError("Block has not been stored in local database. Block hash: " + blockHash);
            }

            Superblock txSuperblock = localSuperblockChain.findBySysBlockHash(txStoredBlock.getHeader().getHash());

            if (txSuperblock == null) {
                return new RestError("Superblock has not been stored in local database yet. " +
                        "Block hash: " + txStoredBlock.getHeader().getHash());
            }

            if (isApprovedCheck && !superblockContractApi.isApproved(txSuperblock.getHash())) {
                return new RestError("Superblock has not been approved yet. " +
                        "Block hash: " + txStoredBlock.getHeader().getHash() + ", superblock ID: " + txSuperblock.getHash());
            }

            int syscoinBlockIndex = txSuperblock.getSyscoinBlockLeafIndex(txStoredBlock.getHeader().getHash());
            byte[] includeBits = new byte[(int) Math.ceil(txSuperblock.getSyscoinBlockHashes().size() / 8.0)];
            Utils.setBitLE(includeBits, syscoinBlockIndex);
            SuperblockPartialMerkleTree superblockPMT = SuperblockPartialMerkleTree.buildFromLeaves(agentConstants.getSyscoinParams(),
                    includeBits, txSuperblock.getSyscoinBlockHashes());

            return getSuperblockSPVProof((AltcoinBlock) txStoredBlock.getHeader(), txSuperblock, superblockPMT);
        }
    }

    /**
     * Returns an SPV Proof to the superblock for a Syscoin transaction to Sysethereum contracts.
     * @param syscoinBlock Syscoin block that the transaction is in.
     * @param pmt Partial Merkle tree for constructing an SPV proof
     *                      of the Syscoin block's existence in the superblock.
     * @throws Exception
     */
    private Object getSuperblockSPVProof(AltcoinBlock syscoinBlock, Superblock superblock, SuperblockPartialMerkleTree pmt) {

        // Construct SPV proof for block
        int syscoinBlockIndex = pmt.getTransactionIndex(syscoinBlock.getHash());
        List<String> siblings = pmt.getTransactionPath(syscoinBlock.getHash())
                .stream().map(Sha256Hash::toString).collect(toList());

        return new SuperblockSPVProof(syscoinBlockIndex, siblings, superblock.getHash().toString());
    }
    /**
     * Tx SPV Proof for challengeCancelBridgeTransfer
     * @throws Exception
     */
    public void fillBlockSPVProof(BlockSPVProof blockSPVProof, Sha256Hash txHash) {
        List<Sha256Hash> sha256Siblings = blockSPVProof.siblings.stream().map(Sha256Hash::wrap).collect(toList());
        byte[] includeBits = new byte[(int) Math.ceil(blockSPVProof.siblings.size() / 8.0)];
        Utils.setBitLE(includeBits, blockSPVProof.index);
        SuperblockPartialMerkleTree superblockPMT = SuperblockPartialMerkleTree.buildFromLeaves(agentConstants.getSyscoinParams(),
                includeBits, sha256Siblings);

        blockSPVProof.siblings = superblockPMT.getTransactionPath(txHash)
                .stream().map(Sha256Hash::toString).collect(toList());

    }
    private static class SuperBlockResponse {
        public final String merkleRoot;
        public final long lastSyscoinBlockTime;
        public final long lastSyscoinBlockMedianTime;
        public final String lastSyscoinBlockHash;
        public final long lastSyscoinBlockBits;
        public final String parentId;
        public final String superblockId;
        public final long superblockHeight;
        public final boolean approved;

        public SuperBlockResponse(Superblock sbIn, boolean approvedIn) {
            this.merkleRoot = sbIn.getMerkleRoot().toString();
            this.lastSyscoinBlockTime = sbIn.getLastSyscoinBlockTime();
            this.lastSyscoinBlockMedianTime = sbIn.getLastSyscoinBlockMedianTime();
            this.lastSyscoinBlockHash = sbIn.getLastSyscoinBlockHash().toString();
            this.lastSyscoinBlockBits = sbIn.getlastSyscoinBlockBits();
            this.parentId = sbIn.getParentId().toString();
            this.superblockId = sbIn.getHash().toString();
            this.superblockHeight = sbIn.getHeight();
            this.approved = approvedIn;
        }
    }

    public Object getSuperblock(@Nullable Keccak256Hash superblockId, int height) throws Exception {
        synchronized (this) {
            Superblock sb;

            if (superblockId != null) {
                sb = localSuperblockChain.getByHash(superblockId);
            } else {
                sb = localSuperblockChain.getByHeight(height);
            }

            return handleSuperblock(sb);
        }
    }

    public Object getSuperblockBySyscoinBlock(@Nullable Sha256Hash blockHash, int height) throws Exception {
        synchronized (this) {
            Context.propagate(syscoinContext);
            StoredBlock sb;

            if (blockHash != null) {
                sb = syscoinWrapper.getBlock(blockHash);
            } else {
                sb = syscoinWrapper.getStoredBlockAtHeight(height);
            }

            if (sb == null) {
                return new RestError("Block has not been stored in local database.");
            }

            Superblock txSuperblock = localSuperblockChain.findBySysBlockHash(sb.getHeader().getHash());

            return handleSuperblock(txSuperblock);
        }
    }

    private Object handleSuperblock(@Nullable Superblock sb) throws Exception {
        if (sb == null) {
            return new RestError("Superblock has not been stored in local database yet.");
        }
        return new SuperBlockResponse(sb, superblockContractApi.isApproved(sb.getHash()));
    }

}