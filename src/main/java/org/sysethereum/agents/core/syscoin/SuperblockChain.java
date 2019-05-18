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
    @Autowired
    private SyscoinWrapper syscoinWrapper; // Interface with the Syscoin blockchain
    @Autowired
    private SuperblockConstantProvider provider; // Interface with the Ethereum blockchain
    private NetworkParameters params;
    private SuperblockLevelDBBlockStore superblockStorage; // database for storing superblocks

    int SUPERBLOCK_DURATION; // time window for a superblock (in seconds)
    private int SUPERBLOCK_DELAY; // time to wait before building a superblock
    private int SUPERBLOCK_STORING_WINDOW; // small time window between storing and sending to avoid losing sync
    private static final Logger log = LoggerFactory.getLogger("LocalAgentConstants");

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

        Date nextSuperblockStartTime =
                getStartTime(syscoinWrapper.getBlock(allSyscoinHashesToHash.peek()).getHeader().getTime());
        Date nextSuperblockEndTime = getEndTime(nextSuperblockStartTime);

        List<Sha256Hash> nextSuperblockSyscoinHashes = new ArrayList<>();
        Keccak256Hash nextSuperblockPrevHash = initialPreviousSuperblockHash;
        long nextSuperblockHeight = getChainHeight() + 1;

        // build and store all superblocks whose last block was mined three hours ago or more
        while (!allSyscoinHashesToHash.empty() && nextSuperblockEndTime.before(getStoringStopTime())) {
            // Modify allSyscoinHashesToHash and get hashes for next superblock.
            nextSuperblockSyscoinHashes = popBlocksBeforeTime(allSyscoinHashesToHash, nextSuperblockEndTime);
            StoredBlock nextSuperblockLastBlock = syscoinWrapper.getBlock(
                    nextSuperblockSyscoinHashes.get(nextSuperblockSyscoinHashes.size() - 1));

            // get the last adjustment block and target/timestamp to pass in for diff adjustment calculations in smart contract
            int lastDiffHeight = nextSuperblockLastBlock.getHeight() - (nextSuperblockLastBlock.getHeight() % this.params.getInterval());
            
            // walk back diff blocks to get the height of the last difficulty adjustment
            // we need to get the data from the block before the diff change at the target period, so minus 1 to get the one before. ie on testnet: @ 360 we want 359 timestamp and bits
            lastDiffHeight -= 1;
            if(lastDiffHeight < 0)
                lastDiffHeight = 0;
            StoredBlock lastDiffBlock = syscoinWrapper.getBlockByHeight(nextSuperblockLastBlock.getHeader().getHash(), lastDiffHeight);

            if(lastDiffBlock == null || lastDiffBlock.getHeight() != lastDiffHeight)
                throw new Exception("storeSuperblocks: last difficulty adjustment block does not fall on top of a difficulty adjustment block height");

            Superblock newSuperblock = new Superblock(this.params, nextSuperblockSyscoinHashes,
                    nextSuperblockLastBlock.getChainWork(), nextSuperblockLastBlock.getHeader().getTimeSeconds(),
                    lastDiffBlock.getHeader().getTimeSeconds(),
                    lastDiffBlock.getHeader().getDifficultyTarget(),
                    nextSuperblockPrevHash, nextSuperblockHeight, nextSuperblockLastBlock.getHeight());
            superblockStorage.put(newSuperblock);
            if (newSuperblock.getChainWork().compareTo(superblockStorage.getChainHeadWork()) > 0) {
                superblockStorage.setChainHead(newSuperblock);
                log.info("New superblock chain head {}", newSuperblock);
            }

            // set prev hash and end time for next superblock
            if (!allSyscoinHashesToHash.empty()) {
                nextSuperblockPrevHash = newSuperblock.getSuperblockId();
                nextSuperblockStartTime =
                        getStartTime(syscoinWrapper.getBlock(allSyscoinHashesToHash.peek()).getHeader().getTime());
                nextSuperblockEndTime = getEndTime(nextSuperblockStartTime);
                nextSuperblockHeight++;
            }

            nextSuperblockSyscoinHashes.clear();
        }
    }

    /**
     * Given a stack of blocks sorted from least to most recently mined,
     * returns a list of hashes belonging to those which were mined before a certain time.
     * These blocks can be used for constructing a superblock mined before a certain time.
     * @param hashStack All the Syscoin blocks that come after the last block of the last stored superblock.
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

        while (!hashStack.empty() && syscoinWrapper.getBlock(hashStack.peek()).getHeader().getTime().before(endTime)) {
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
        return getEndTime(getStartTime(new Date(superblock.getLastSyscoinBlockTime())));
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