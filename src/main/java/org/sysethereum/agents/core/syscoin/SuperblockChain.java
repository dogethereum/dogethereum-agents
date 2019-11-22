package org.sysethereum.agents.core.syscoin;

import lombok.extern.slf4j.Slf4j;

import org.bitcoinj.core.*;
import org.bitcoinj.store.BlockStoreException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sysethereum.agents.core.bridge.Superblock;
import org.sysethereum.agents.core.bridge.SuperblockFactory;
import org.sysethereum.agents.service.rest.MerkleRootComputer;

import javax.annotation.Nullable;

import java.math.BigInteger;
import java.util.*;

/**
 * Provides methods for interacting with a superblock chain.
 * Storage is managed by SuperblockLevelDBBlockStore.
 * @author Catalina Juarros
 */
@Service
@Slf4j(topic = "SuperblockChain")
public class SuperblockChain {

    private static final Logger logger = LoggerFactory.getLogger("SuperblockChain");

    private final SyscoinWrapper syscoinWrapper; // Interface with the Syscoin blockchain
    private final MerkleRootComputer merkleRootComputer;
    private final SuperblockFactory superblockFactory;
    private final SuperblockLevelDBBlockStore superblockStorage; // database for storing superblocks

    public final int SUPERBLOCK_DURATION; // num blocks in a superblock
    private final int SUPERBLOCK_DELAY; // time to wait before building a superblock
    private final int SUPERBLOCK_STORING_WINDOW; // time window between storing and sending to avoid losing sync
    private final Context syscoinContext;

    @Autowired
    public SuperblockChain(
            Context syscoinContext,
            SyscoinWrapper syscoinWrapper,
            SuperblockFactory superblockFactory,
            SuperblockLevelDBBlockStore superblockLevelDBBlockStore,
            MerkleRootComputer merkleRootComputer,
            BigInteger superblockDuration,
            BigInteger superblockDelay
    ) {
        this.syscoinContext = syscoinContext;
        this.syscoinWrapper = syscoinWrapper;
        this.superblockFactory = superblockFactory;
        this.superblockStorage = superblockLevelDBBlockStore;
        this.merkleRootComputer = merkleRootComputer;

        this.SUPERBLOCK_DURATION = superblockDuration.intValue();
        this.SUPERBLOCK_DELAY = superblockDelay.intValue();
        this.SUPERBLOCK_STORING_WINDOW = SUPERBLOCK_DELAY * 2/3 ; // store superblocks 2 hr before they should be sent
    }

    /**
     * Given a stack of blocks to hash, builds and stores superblocks based on the blocks' timestamps.
     * @param allSyscoinHashesToHash All the Syscoin blocks that come after the last block of the last stored superblock.
     *                            This stack should be sorted from least to most recently mined,
     *                            i.e. the top block's hash should be the previous block hash of the block underneath it
     *                            and so on.
     *                            Modified by function: all the blocks up to and not including the first ('highest') one
     *                            that was mined under three hours ago are popped.
     * @param initialPreviousSuperblockHash Keccak-256 hash of the last stored superblock.
     * @throws Exception
     */
    public void storeSuperblocks(Stack<Sha256Hash> allSyscoinHashesToHash, Keccak256Hash initialPreviousSuperblockHash)
            throws Exception {
        Context.propagate(syscoinContext);
        if (allSyscoinHashesToHash.empty())
            return;

        List<Sha256Hash> nextSuperblockSyscoinHashes;
        Keccak256Hash nextSuperblockPrevHash = initialPreviousSuperblockHash;
        long nextSuperblockHeight = getChainHeight() + 1;
        // build and store all superblocks whose last block was mined three hours ago or more
        while (!allSyscoinHashesToHash.empty()) {
            // Modify allSyscoinHashesToHash and get hashes for next superblock.
            nextSuperblockSyscoinHashes = popBlocksBeforeTime(allSyscoinHashesToHash, getStoringStopTime());
            // if we don't have a collection of 60 blocks that are at least 1 hour old we exit
            if(nextSuperblockSyscoinHashes.isEmpty()){
                break;
            }
            StoredBlock nextSuperblockLastBlock = syscoinWrapper.getBlock(
                    nextSuperblockSyscoinHashes.get(nextSuperblockSyscoinHashes.size() - 1));

            Superblock newSuperblock = superblockFactory.make(
                    merkleRootComputer.computeMerkleRoot(nextSuperblockSyscoinHashes),
                    nextSuperblockSyscoinHashes,
                    nextSuperblockLastBlock.getHeader().getTimeSeconds(),
                    syscoinWrapper.getMedianTimestamp(nextSuperblockLastBlock),
                    nextSuperblockLastBlock.getHeader().getDifficultyTarget(),
                    nextSuperblockPrevHash,
                    nextSuperblockHeight
            );

            superblockStorage.put(newSuperblock);
            superblockStorage.setChainHead(newSuperblock);
            logger.info("New superblock chain head {}", newSuperblock);

            // set prev hash and end time for next superblock
            if (!allSyscoinHashesToHash.empty()) {
                nextSuperblockPrevHash = newSuperblock.getHash();
                nextSuperblockHeight++;
            }

            nextSuperblockSyscoinHashes.clear();
        }
    }

    /**
     * Given a stack of blocks sorted from least to most recently mined based on median timestamp,
     * returns a list of 60 hashes belonging to those which were mined before a certain time.
     * These blocks can be used for constructing a superblock mined before a certain time.
     * @param hashStack All the Syscoin blocks that come after the last block of the last stored superblock.
     *                  Must not be empty.
     *                  Modified by function: all the blocks up to and not including the first ('highest') one
     *                  that was mined after endTime are popped requiring atleast 60 blocks or nothing is popped.
     * @param endTime Time limit of the superblock that this method is being used to construct.
     * @return List of superblocks mined before endTime, sorted from least to most recently mined.
     * @throws Exception if list is empty.
     */
    private List<Sha256Hash> popBlocksBeforeTime(Stack<Sha256Hash> hashStack, Date endTime) throws Exception {
        Context.propagate(syscoinContext);
        if (hashStack.empty()) {
            throw new Exception("List of blocks to pop must not be empty.");
        }

        List<Sha256Hash> poppedBlocks = new ArrayList<>();
        boolean haveEnoughForDuration = false;
        while (!hashStack.empty() && new Date(syscoinWrapper.getMedianTimestamp(syscoinWrapper.getBlock(hashStack.peek()))*1000L).before(endTime)) {
            poppedBlocks.add(hashStack.pop());
            if(poppedBlocks.size() >= SUPERBLOCK_DURATION) {
                haveEnoughForDuration = true;
                break;
            }
        }

        // if we don't have SUPERBLOCK_DURATION amount then just clear, we don't have enough to create a superblock yet
        if(!haveEnoughForDuration)
            poppedBlocks.clear();
        return poppedBlocks;
    }


    /* ---- GETTERS ---- */

    /**
     * Returns tip of superblock chain.
     * @return Tip of superblock chain as saved on disk.
     * @throws BlockStoreException
     */
    public Superblock getChainHead() {
        return superblockStorage.getChainHead();
    }

    /**
     * Returns height of tip of superblock chain.
     * @return Height of tip of superblock chain as saved on disk.
     * @throws BlockStoreException
     */
    public long getChainHeight() {
        return getChainHead().getHeight();
    }

    /**
     * Looks up a superblock by its hash.
     * @param superblockHash Keccak-256 hash of a superblock.
     * @return Superblock with given hash if it's found in the database, null otherwise.
     */
    @Nullable
    public Superblock getByHash(Keccak256Hash superblockHash) {
        return superblockStorage.get(superblockHash);
    }

    /**
     * Looks up a superblock by its height.
     * Slower than looking up by hash, as it traverses the chain backwards.
     * @param superblockHeight Height of a superblock
     * @return Superblock with the given height if said height is less than that of the chain tip,
     *         null otherwise.
     */
    public Superblock getByHeight(long superblockHeight) {
        Superblock currentSuperblock = getChainHead();
        if (superblockHeight > currentSuperblock.getHeight())
            return null; // Superblock does not exist.

        // Superblock exists.
        while (currentSuperblock.getHeight() > superblockHeight)
            currentSuperblock = getByHash(currentSuperblock.getParentId());

        return currentSuperblock;
    }

    /**
     * Finds a superblock with a given parentId.
     * @param parentId parentId of desired superblock.
     * @return Best superblock in main chain with superblockId as its parentId if said superblock exists,
     *         null otherwise.
     */
    public Superblock getFirstDescendant(Keccak256Hash parentId) {
        Superblock sb = getByHash(parentId);

        if (sb == null) {
            // The superblock isn't in the main chain.
            logger.info("Superblock {} is not in the main chain. Returning from getFirstDescendant.", parentId);
            return null;
        }

        if (sb.getHeight() == getChainHeight()) {
            // There's nothing above the tip of the chain.
            return null;
        }
        Superblock currentSuperblock = getChainHead();

        while (currentSuperblock != null && !currentSuperblock.getParentId().equals(parentId)) {
            currentSuperblock = getByHash(currentSuperblock.getParentId());
        }

        return currentSuperblock;
    }

    /**
     * Finds the superblock in the superblock main chain that contains the block identified by `blockHash`.
     * @param blockHash SHA-256 hash of a block that we want to find.
     * @return Superblock where the block can be found.
     */
    @Nullable
    public Superblock findBySysBlockHash(Sha256Hash blockHash) {
        Superblock sb = getChainHead();

        while (sb != null) {
            if (sb.hasSyscoinBlock(blockHash))
                return sb;
            sb = getByHash(sb.getParentId());
        }

        // current superblock is null
        return null;
    }

    /* ---- HELPER METHODS AND CLASSES ---- */

    /**
     * To be used when building a superblock.
     * Returns the time limit for storing superblocks - for example, if SUPERBLOCK_DELAY is 3600
     * and SUPERBLOCK_STORING_WINDOW is 60, superblocks built will be from 59 minutes ago or earlier.
     * The reason for this is that, without the storing window, there's a very low chance that the challenger agent
     * might not recognise an honest superblock just because the superblock chain agent hasn't finished building it;
     * the storing window ensures that the challenger knows about all honest superblocks before they're submitted.
     * @return Earliest acceptable time for building a superblock.
     */
    private Date getStoringStopTime() {
        return SuperblockUtils.getNSecondsAgo(SUPERBLOCK_DELAY - SUPERBLOCK_STORING_WINDOW);
    }

    /**
     * To be used when sending a superblock.
     * Get the time limit for sending superblocks to the bridge.
     * @return Earliest acceptable date for sending a superblock.
     */
    public Date getSendingStopTime() {
        return SuperblockUtils.getNSecondsAgo(SUPERBLOCK_DELAY);
    }

    /**
     * Checks whether a superblock's time frame is long enough ago that it can be sent to the bridge.
     * @param superblock Superblock to be sent to the bridge.
     * @return True if superblock can be sent to the bridge, false otherwise.
     */
    public boolean sendingTimePassed(Superblock superblock) {
        return new Date(superblock.getLastSyscoinBlockMedianTime()*1000L).before(getSendingStopTime());
    }
}