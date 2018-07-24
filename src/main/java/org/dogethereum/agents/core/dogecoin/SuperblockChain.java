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

    private int SUPERBLOCK_DURATION; // time window for a superblock (in seconds)
    private int SUPERBLOCK_DELAY; // time to wait before building a superblock


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
        // TODO: initialise in SuperblockChainClient
    }


//    /**
//     * Starts syncing.
//     * @param updatePeriod How often the SuperblockChain object will poll dogecoinWrapper for new Doge blocks
//     *                     and update its chain file accordingly.
//     * @param executionDate
//     * @throws BlockStoreException
//     * @throws java.io.IOException
//     */
//    private void initialize(int updatePeriod, Date executionDate) throws BlockStoreException, java.io.IOException {
//        new Timer("Update superblock chain").scheduleAtFixedRate(
//                new UpdateSuperblocksTimerTask(), executionDate, updatePeriod);
//    }

//    /**
//     * Builds and maintains a chain of superblocks from the whole Dogecoin blockchain.
//     * Writes it to disk as specified by SuperblockLevelDBBlockStore.
//     * @throws BlockStoreException
//     * @throws IOException
//     */
//    public void updateChain() throws Exception, BlockStoreException, IOException {
//        Superblock bestSuperblock = getChainHead();
//        Sha256Hash bestSuperblockLastBlockHash = bestSuperblock.getLastDogeBlockHash();
//
//        // get all the Dogecoin blocks that haven't yet been hashed into a superblock
//        Stack<Sha256Hash> allDogeHashesToHash = getDogeBlockHashesNewerThan(bestSuperblockLastBlockHash);
//        storeSuperblocks(allDogeHashesToHash, bestSuperblock.getSuperblockId()); // group them in superblocks
//    }
//
//    private Stack<Sha256Hash> getDogeBlockHashesNewerThan(Sha256Hash blockHash) throws BlockStoreException {
//        Stack<Sha256Hash> hashes = new Stack<>();
//        StoredBlock currentStoredBlock = dogecoinWrapper.getChainHead();
//
//        while (!currentStoredBlock.getHeader().getHash().equals(blockHash)) {
//            hashes.push(currentStoredBlock.getHeader().getHash());
//            currentStoredBlock = dogecoinWrapper.getBlock(currentStoredBlock.getHeader().getPrevBlockHash());
//        }
//
//        return hashes;
//    }

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

        Date nextSuperblockStartTime = dogecoinWrapper.getBlock(allDogeHashesToHash.peek()).getHeader().getTime();
        Date nextSuperblockEndTime = getEndTime(nextSuperblockStartTime);

        List<Sha256Hash> nextSuperblockDogeHashes = new ArrayList<>();
        Keccak256Hash nextSuperblockPrevHash = initialPreviousSuperblockHash;
        long nextSuperblockHeight = getChainHeight() + 1;

        // build and store all superblocks whose last block was mined three hours ago or more
        while (!allDogeHashesToHash.empty() && nextSuperblockEndTime.before(getStopTime())) {
            // Modify allDogeHashesToHash and get hashes for next superblock.
            nextSuperblockDogeHashes = popBlocksBeforeTime(allDogeHashesToHash, nextSuperblockEndTime);
            StoredBlock nextSuperblockLastBlock = dogecoinWrapper.getBlock(
                    nextSuperblockDogeHashes.get(nextSuperblockDogeHashes.size() - 1));
            StoredBlock nextSuperblockPreviousToLastBlock =
                    dogecoinWrapper.getBlock(nextSuperblockLastBlock.getHeader().getHash());

            Superblock newSuperblock = new Superblock(this.params, nextSuperblockDogeHashes,
                    nextSuperblockLastBlock.getChainWork(), nextSuperblockLastBlock.getHeader().getTimeSeconds(),
                    nextSuperblockPreviousToLastBlock.getHeader().getTimeSeconds(),
                    nextSuperblockLastBlock.getHeader().getDifficultyTarget(), nextSuperblockPrevHash,
                    nextSuperblockHeight);

            superblockStorage.put(newSuperblock);
            if (newSuperblock.getChainWork().compareTo(superblockStorage.getChainHeadWork()) > 0)
                superblockStorage.setChainHead(newSuperblock);

            // set prev hash and end time for next superblock
            if (!allDogeHashesToHash.empty()) {
                nextSuperblockPrevHash = newSuperblock.getSuperblockId();
                nextSuperblockStartTime = dogecoinWrapper.getBlock(allDogeHashesToHash.peek()).getHeader().getTime();
                nextSuperblockEndTime = getEndTime(nextSuperblockStartTime);
                nextSuperblockHeight++;
            }

            nextSuperblockDogeHashes.clear();
        }
    }

    /**
     * Given a stack of blocks sorted from least to most recently mined,
     * returns a list of hashes belonging those which were mined before a certain time.
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
     * Get tip of superblock chain.
     * @return Tip of superblock chain as saved on disk.
     * @throws BlockStoreException
     */
    public Superblock getChainHead() throws BlockStoreException, IOException {
        return superblockStorage.getChainHead();
    }

    /**
     * Get height of tip of superblock chain.
     * @return Height of tip of superblock chain as saved on disk.
     * @throws BlockStoreException
     */
    public long getChainHeight() throws BlockStoreException, IOException {
        return getChainHead().getSuperblockHeight();
    }

    /**
     * Get highest approved superblock.
     * @return Highest approved superblock as saved on disk.
     * @throws BlockStoreException
     */
    public Superblock getApprovedHead() throws BlockStoreException {
        throw new UnsupportedOperationException("to be implemented");
        // Oscar says: commented the line bellow because code does not compiles. There is no SuperblockLevelDBBlockStore.getApprovedHead()
        // return superblockStorage.getApprovedHead();
    }

    /**
     * Look up a superblock by its hash.
     * @param superblockHash Keccak-256 hash of a superblock.
     * @return Superblock with given hash if it's found in the database, null otherwise.
     */
    public Superblock getSuperblock(Keccak256Hash superblockHash) throws IOException {
        return superblockStorage.get(superblockHash);
    }

    /**
     * Look up a superblock by its height.
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
     * Find a superblock with a given parentId.
     * @param superblockId parentId of desired superblock.
     * @return Best superblock in main chain with superblockId as its parentId if said superblock exists,
     *         null otherwise.
     * @throws BlockStoreException
     */
    public Superblock getFirstDescendant(Keccak256Hash superblockId) throws BlockStoreException, IOException {
        Superblock currentSuperblock = getChainHead();

        while (currentSuperblock != null && !currentSuperblock.getParentId().equals(superblockId)) {
            currentSuperblock = getSuperblock(currentSuperblock.getParentId());
        }

        return currentSuperblock;
    }


    /* ---- HELPER METHODS AND CLASSES ---- */

    private Date getEndTime(Date startTime) {
        if (SUPERBLOCK_DURATION < 3600) {
            return SuperblockUtils.roundToNNextWholeMinutes(startTime, SUPERBLOCK_DURATION / 60);
        } else {
            return SuperblockUtils.roundToNNextWholeHours(startTime, SUPERBLOCK_DURATION / 3600);
        }
    }

    private Date getStopTime() {
        return SuperblockUtils.getNSecondsAgo(SUPERBLOCK_DELAY);
    }

//    /**
//     * Task to keep superblock chain updated whenever the agent is running.
//     */
//    private class UpdateSuperblocksTimerTask extends TimerTask {
//        @Override
//        public void run() {
//            try {
//                log.debug("UpdateSuperblocksTimerTask");
//                updateChain();
//            } catch (Exception e) {
//                log.error(e.getMessage(), e);
//            }
//        }
//    }
}