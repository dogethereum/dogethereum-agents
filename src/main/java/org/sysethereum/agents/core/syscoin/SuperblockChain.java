package org.sysethereum.agents.core.syscoin;

import lombok.extern.slf4j.Slf4j;

import org.bitcoinj.core.*;
import org.bitcoinj.store.BlockStoreException;

import org.sysethereum.agents.constants.SystemProperties;
import org.sysethereum.agents.constants.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;

import java.util.*;

/**
 * Provides methods for interacting with a superblock chain.
 * Storage is managed by SuperblockLevelDBBlockStore.
 * @author Catalina Juarros
 */
@Component
@Slf4j(topic = "SuperblockChain")
public class SuperblockChain {

    private static final Logger logger = LoggerFactory.getLogger("SuperblockChain");
    private final SyscoinWrapper syscoinWrapper; // Interface with the Syscoin blockchain
    private final SuperblockConstantProvider provider; // Interface with the Ethereum blockchain
    private NetworkParameters params;
    private SuperblockLevelDBBlockStore superblockStorage; // database for storing superblocks

    public int SUPERBLOCK_DURATION; // num blocks in a superblock
    private int SUPERBLOCK_DELAY; // time to wait before building a superblock
    private int SUPERBLOCK_STORING_WINDOW; // small time window between storing and sending to avoid losing sync

    /* ---- CONSTRUCTION METHODS ---- */

    @Autowired
    public SuperblockChain(
            SyscoinWrapper syscoinWrapper,
            SuperblockConstantProvider provider
    ) {
        this.syscoinWrapper = syscoinWrapper;
        this.provider = provider;
    }

    /**
     * Sets up variables and initialises chain.
     * @throws Exception if superblock duration or delay cannot be retrieved from SuperblockConstantProvider.
     * @throws BlockStoreException if superblockStorage is not properly initialized.
     */
    @PostConstruct
    private void setup() throws Exception, BlockStoreException {
        SystemProperties config = SystemProperties.CONFIG;
        AgentConstants agentConstants = config.getAgentConstants();
        Context context = new Context(agentConstants.getSyscoinParams());
        File directory = new File(config.dataDirectory());
        File chainFile = new File(directory.getAbsolutePath() + "/SuperblockChain");
        this.params = agentConstants.getSyscoinParams();
        this.superblockStorage = new SuperblockLevelDBBlockStore(context, chainFile, params);
        this.SUPERBLOCK_DURATION = provider.getSuperblockDuration().intValue();
        this.SUPERBLOCK_DELAY = provider.getSuperblockDelay().intValue();
        this.SUPERBLOCK_STORING_WINDOW = 60; // store superblocks one minute before they should be sent
    }

    /**
     * Closes the block storage underlying this blockchain.
     * @throws BlockStoreException if an exception is thrown during closing.
     */
    @PreDestroy
    private void close() throws BlockStoreException {
        this.superblockStorage.close();
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
        if (allSyscoinHashesToHash.empty())
            return;

        List<Sha256Hash> nextSuperblockSyscoinHashes;
        Keccak256Hash nextSuperblockPrevHash = initialPreviousSuperblockHash;
        long nextSuperblockHeight = getChainHeight() + 1;
        // build and store all superblocks whose last block was mined three hours ago or more
        while (!allSyscoinHashesToHash.empty()) {
            // Modify allSyscoinHashesToHash and get hashes for next superblock.
            nextSuperblockSyscoinHashes = popBlocksBeforeTime(allSyscoinHashesToHash, getStoringStopTime());
            // if we don't have a collection of 60 blocks that are atleast 3 hours old we exit
            if(nextSuperblockSyscoinHashes.isEmpty()){
                break;
            }
            StoredBlock nextSuperblockLastBlock = syscoinWrapper.getBlock(
                    nextSuperblockSyscoinHashes.get(nextSuperblockSyscoinHashes.size() - 1));

            Superblock newSuperblock = new Superblock(this.params, nextSuperblockSyscoinHashes,
                    nextSuperblockLastBlock.getChainWork(), nextSuperblockLastBlock.getHeader().getTimeSeconds(),nextSuperblockLastBlock.getHeader().getDifficultyTarget(),
                    nextSuperblockPrevHash, nextSuperblockHeight);
            superblockStorage.put(newSuperblock);
            if (newSuperblock.getChainWork().compareTo(superblockStorage.getChainHeadWork()) > 0) {
                superblockStorage.setChainHead(newSuperblock);
                logger.info("New superblock chain head {}", newSuperblock);
            }

            // set prev hash and end time for next superblock
            if (!allSyscoinHashesToHash.empty()) {
                nextSuperblockPrevHash = newSuperblock.getSuperblockId();
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
        return getChainHead().getSuperblockHeight();
    }

    /**
     * Looks up a superblock by its hash.
     * @param superblockHash Keccak-256 hash of a superblock.
     * @return Superblock with given hash if it's found in the database, null otherwise.
     */
    public Superblock getSuperblock(Keccak256Hash superblockHash) {
        return superblockStorage.get(superblockHash);
    }

    /**
     * Looks up a superblock by its height.
     * Slower than looking up by hash, as it traverses the chain backwards.
     * @param superblockHeight Height of a superblock
     * @return Superblock with the given height if said height is less than that of the chain tip,
     *         null otherwise.
     */
    public Superblock getSuperblockByHeight(long superblockHeight) {
        Superblock currentSuperblock = getChainHead();
        if (superblockHeight > currentSuperblock.getSuperblockHeight())
            return null; // Superblock does not exist.

        // Superblock exists.
        while (currentSuperblock.getSuperblockHeight() > superblockHeight)
            currentSuperblock = getSuperblock(currentSuperblock.getParentId());

        return currentSuperblock;
    }

    /**
     * Finds a superblock with a given parentId.
     * @param parentId parentId of desired superblock.
     * @return Best superblock in main chain with superblockId as its parentId if said superblock exists,
     *         null otherwise.
     */
    public Superblock getFirstDescendant(Keccak256Hash parentId) {
        if (getSuperblock(parentId) == null) {
            // The superblock isn't in the main chain.
            logger.info("Superblock {} is not in the main chain. Returning from getFirstDescendant.", parentId);
            return null;
        }

        if (getSuperblock(parentId).getSuperblockHeight() == getChainHeight()) {
            // There's nothing above the tip of the chain.
            return null;
        }
        Superblock currentSuperblock = getChainHead();

        while (currentSuperblock != null && !currentSuperblock.getParentId().equals(parentId)) {
            currentSuperblock = getSuperblock(currentSuperblock.getParentId());
        }

        return currentSuperblock;
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
        return new Date(superblock.getLastSyscoinBlockTime()).before(getSendingStopTime());
    }

    /**
     * Returns a superblock's parent by ID if it's in the main chain.
     * @param superblock Superblock.
     * @return Superblock parent if said parent is part of the main chain, null otherwise.
     * @throws IOException
     */
    public Superblock getParent(Superblock superblock) {
        return getSuperblock(superblock.getParentId());
    }

}