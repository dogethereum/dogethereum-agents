package org.dogethereum.agents.core.dogecoin;

import lombok.extern.slf4j.Slf4j;

import org.bitcoinj.core.*;
import org.bitcoinj.store.BlockStoreException;

import org.dogethereum.agents.constants.SystemProperties;
import org.dogethereum.agents.constants.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.math.BigInteger;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Provides methods for interacting with a superblock chain.
 * Storage is managed by SuperblockLevelDBBlockStore.
 * @author Catalina Juarros
 */

@Component
@Slf4j(topic = "SuperblockChain")
public class SuperblockChain {
    @Autowired
    private DogecoinWrapper dogecoinWrapper; // Interface with the Doge blockchain
    @Autowired
    private SuperblockConstantProvider provider; // Interface with the Ethereum blockchain
    private NetworkParameters params;
    private SuperblockLevelDBBlockStore superblockStorage; // database for storing superblocks

    int SUPERBLOCK_DURATION; // time window for a superblock (in seconds)
    private int SUPERBLOCK_DELAY; // time to wait before building a superblock
    private int SUPERBLOCK_STORING_WINDOW; // small time window between storing and sending to avoid losing sync


    /* ---- CONSTRUCTION METHODS ---- */

    @Autowired
    public SuperblockChain() throws Exception, BlockStoreException {}

    /**
     * Sets up variables and initialises chain.
     * @throws Exception if superblock duration or delay cannot be retrieved from SuperblockConstantProvider.
     * @throws BlockStoreException if superblockStorage is not properly initialized.
     */
    @PostConstruct
    private void setup() throws Exception, BlockStoreException {
        SystemProperties config = SystemProperties.CONFIG;
        AgentConstants agentConstants = config.getAgentConstants();
        Context context = new Context(agentConstants.getDogeParams());
        File directory = new File(config.dataDirectory());
        File chainFile = new File(directory.getAbsolutePath() + "/SuperblockChain");
        this.params = agentConstants.getDogeParams();
        this.superblockStorage = new SuperblockLevelDBBlockStore(context, chainFile, params);
        this.SUPERBLOCK_DURATION = provider.getSuperblockDuration().intValue();
        this.SUPERBLOCK_DELAY = provider.getSuperblockDelay().intValue();
        this.SUPERBLOCK_STORING_WINDOW = 60; // store superblocks one minute before they should be sent
    }

    /**
     * Given a stack of blocks to hash, builds and stores superblocks based on the blocks' timestamps.
     * @param allDogeHashesToHash All the Dogecoin blocks that come after the last block of the last stored superblock.
     *                            This stack should be sorted from least to most recently mined,
     *                            i.e. the top block's hash should be the previous block hash of the block underneath it
     *                            and so on.
     *                            Modified by function: all the blocks up to and not including the first ('highest') one
     *                            that was mined under three hours ago are popped.
     * @param initialPreviousSuperblockHash Keccak-256 hash of the last stored superblock.
     * @throws Exception
     */
    public void storeSuperblocks(Stack<Sha256Hash> allDogeHashesToHash, Keccak256Hash initialPreviousSuperblockHash)
            throws Exception {
        if (allDogeHashesToHash.empty())
            return;

        Date nextSuperblockStartTime =
                getStartTime(dogecoinWrapper.getBlock(allDogeHashesToHash.peek()).getHeader().getTime());
        Date nextSuperblockEndTime = getEndTime(nextSuperblockStartTime);

        List<Sha256Hash> nextSuperblockDogeHashes = new ArrayList<>();
        Keccak256Hash nextSuperblockPrevHash = initialPreviousSuperblockHash;
        long nextSuperblockHeight = getChainHeight() + 1;

        // build and store all superblocks whose last block was mined three hours ago or more
        while (!allDogeHashesToHash.empty() && nextSuperblockEndTime.before(getStoringStopTime())) {
            // Modify allDogeHashesToHash and get hashes for next superblock.
            nextSuperblockDogeHashes = popBlocksBeforeTime(allDogeHashesToHash, nextSuperblockEndTime);
            StoredBlock nextSuperblockLastBlock = dogecoinWrapper.getBlock(
                    nextSuperblockDogeHashes.get(nextSuperblockDogeHashes.size() - 1));
            StoredBlock nextSuperblockPreviousToLastBlock =
                    dogecoinWrapper.getBlock(nextSuperblockLastBlock.getHeader().getPrevBlockHash());

            Superblock newSuperblock = new Superblock(this.params, nextSuperblockDogeHashes,
                    nextSuperblockLastBlock.getChainWork(), nextSuperblockLastBlock.getHeader().getTimeSeconds(),
                    nextSuperblockPreviousToLastBlock.getHeader().getTimeSeconds(),
                    nextSuperblockLastBlock.getHeader().getDifficultyTarget(),
                    nextSuperblockPrevHash, nextSuperblockHeight);
            superblockStorage.put(newSuperblock);
            if (newSuperblock.getChainWork().compareTo(superblockStorage.getChainHeadWork()) > 0) {
                superblockStorage.setChainHead(newSuperblock);
                log.info("New superblock chain head {}", newSuperblock);
            }

            // set prev hash and end time for next superblock
            if (!allDogeHashesToHash.empty()) {
                nextSuperblockPrevHash = newSuperblock.getSuperblockId();
                nextSuperblockStartTime =
                        getStartTime(dogecoinWrapper.getBlock(allDogeHashesToHash.peek()).getHeader().getTime());
                nextSuperblockEndTime = getEndTime(nextSuperblockStartTime);
                nextSuperblockHeight++;
            }

            nextSuperblockDogeHashes.clear();
        }
    }

    /**
     * Given a stack of blocks sorted from least to most recently mined,
     * returns a list of hashes belonging to those which were mined before a certain time.
     * These blocks can be used for constructing a superblock mined before a certain time.
     * @param hashStack All the Dogecoin blocks that come after the last block of the last stored superblock.
     *                  Must not be empty.
     *                  Modified by function: all the blocks up to and not including the first ('highest') one
     *                  that was mined after endTime are popped.
     * @param endTime Time limit of the superblock that this method is being used to construct.
     * @return List of superblocks mined before endTime, sorted from least to most recently mined.
     * @throws Exception if list is empty.
     */
    private List<Sha256Hash> popBlocksBeforeTime(Stack<Sha256Hash> hashStack, Date endTime) throws Exception {
        if (hashStack.empty()) {
            throw new Exception("List of blocks to pop must not be empty.");
        }

        List<Sha256Hash> poppedBlocks = new ArrayList<>();

        while (!hashStack.empty() && dogecoinWrapper.getBlock(hashStack.peek()).getHeader().getTime().before(endTime)) {
            poppedBlocks.add(hashStack.pop());
        }

        return poppedBlocks;
    }


    /* ---- GETTERS ---- */

    /**
     * Returns tip of superblock chain.
     * @return Tip of superblock chain as saved on disk.
     * @throws BlockStoreException
     */
    public Superblock getChainHead() throws BlockStoreException, IOException {
        return superblockStorage.getChainHead();
    }

    /**
     * Returns height of tip of superblock chain.
     * @return Height of tip of superblock chain as saved on disk.
     * @throws BlockStoreException
     */
    public long getChainHeight() throws BlockStoreException, IOException {
        return getChainHead().getSuperblockHeight();
    }

    /**
     * Looks up a superblock by its hash.
     * @param superblockHash Keccak-256 hash of a superblock.
     * @return Superblock with given hash if it's found in the database, null otherwise.
     */
    public Superblock getSuperblock(Keccak256Hash superblockHash) throws IOException {
        return superblockStorage.get(superblockHash);
    }

    /**
     * Looks up a superblock by its height.
     * Slower than looking up by hash, as it traverses the chain backwards.
     * @param superblockHeight Height of a superblock
     * @return Superblock with the given height if said height is less than that of the chain tip,
     *         null otherwise.
     * @throws BlockStoreException
     * @throws IOException If a superblock hash cannot be calculated.
     */
    public Superblock getSuperblockByHeight(long superblockHeight) throws BlockStoreException, IOException {
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
     * @param superblockId parentId of desired superblock.
     * @return Best superblock in main chain with superblockId as its parentId if said superblock exists,
     *         null otherwise.
     * @throws BlockStoreException
     */
    public Superblock getFirstDescendant(Keccak256Hash superblockId) throws BlockStoreException, IOException {
        if (getSuperblock(superblockId) == null) {
            // The superblock isn't in the main chain.
            log.info("Superblock {} is not in the main chain. Returning from getFirstDescendant.", superblockId);
            return null;
        }

        if (getSuperblock(superblockId).getSuperblockHeight() == getChainHeight()) {
            // There's nothing above the tip of the chain.
            return null;
        }

        Superblock currentSuperblock = getChainHead();

        while (currentSuperblock != null && !currentSuperblock.getParentId().equals(superblockId)) {
            currentSuperblock = getSuperblock(currentSuperblock.getParentId());
        }

        return currentSuperblock;
    }


    /* ---- HELPER METHODS AND CLASSES ---- */

    /**
     * Returns the beginning of the latest superblock interval that starts before the time of a certain block,
     * i.e. the superblock that the block should be part of.
     * @param firstBlockTimestamp Timestamp of the first block in a superblock.
     * @return Superblock start time.
     */
    Date getStartTime(Date firstBlockTimestamp) {
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(firstBlockTimestamp);
        startTime.set(Calendar.HOUR, 0);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);
        startTime.set(Calendar.MILLISECOND, 0);
        Calendar nextStartTime = (Calendar) startTime.clone();
        nextStartTime.add(Calendar.SECOND, SUPERBLOCK_DURATION);
        while (!nextStartTime.getTime().after(firstBlockTimestamp)) {
            startTime.add(Calendar.SECOND, SUPERBLOCK_DURATION);
            nextStartTime.add(Calendar.SECOND, SUPERBLOCK_DURATION);
        }
        return startTime.getTime();
    }

    /**
     * Returns the end time for building a superblock.
     * @param startTime Superblock start time.
     * @return Superblock end time.
     */
    Date getEndTime(Date startTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        calendar.add(Calendar.SECOND, SUPERBLOCK_DURATION);
        return calendar.getTime();
    }

    /**
     * Returns the end time for an already built superblock.
     * This is useful for knowing if a superblock
     * @param superblock Already created superblock.
     * @return Superblock end time.
     */
    public Date getEndTime(Superblock superblock) {
        return getEndTime(getStartTime(new Date(superblock.getLastDogeBlockTime())));
    }

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
        return getEndTime(superblock).before(getSendingStopTime());
    }

    /**
     * Returns a superblock's parent by ID if it's in the main chain.
     * @param superblock Superblock.
     * @return Superblock parent if said parent is part of the main chain, null otherwise.
     * @throws IOException
     */
    public Superblock getParent(Superblock superblock) throws IOException {
        return getSuperblock(superblock.getParentId());
    }

}