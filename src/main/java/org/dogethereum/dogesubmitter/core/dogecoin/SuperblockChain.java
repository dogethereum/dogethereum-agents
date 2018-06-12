package org.dogethereum.dogesubmitter.core.dogecoin;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.time.DateUtils;
import org.bitcoinj.core.AltcoinBlock;
import org.bitcoinj.core.*;
import org.bitcoinj.store.BlockStoreException;

import org.dogethereum.dogesubmitter.constants.SystemProperties;
import org.dogethereum.dogesubmitter.util.OperatorKeyHandler;
import org.dogethereum.dogesubmitter.constants.*;
import org.dogethereum.dogesubmitter.util.*;
import org.dogethereum.dogesubmitter.core.dogecoin.Superblock;

import org.libdohj.core.ScryptHash;
import org.spongycastle.util.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileSystemUtils;
import org.web3j.crypto.Hash;

import javax.annotation.PostConstruct;
import java.io.*;
import java.math.BigInteger;
import java.util.*;

/**
 * Provides methods for interacting with a superblock chain.
 * Storage is managed by SuperblockLevelDBBlockStore.
 * @author Catalina Juarros
 */

@Slf4j(topic = "SuperblockChain")
public class SuperblockChain {
    @Autowired
    private DogecoinWrapper dogecoinWrapper; // Interface with the Doge blockchain
//    private final Context context;

    private SuperblockLevelDBBlockStore superblockStorage; // database for storing superblocks
    private NetworkParameters params;


    /* ---- CONSTRUCTION METHODS ---- */

    /**
     * Class constructor.
     * @param dogecoinWrapper Dogecoin blockchain interface.
     *                        Must be the same `DogecoinWrapper` being used in the `DogeToEthClient` object
     *                        where this SuperblockChain is being instantiated.
     * @param context Dogecoin blockchain context. Same requirement as `dogecoinWrapper` regarding `DogeToEthClient`.
     * @param directory Directory where a disk copy of the superblock chain will be kept.
     * @throws BlockStoreException if superblockStorage is not properly initialized.
     */
    public SuperblockChain(DogecoinWrapper dogecoinWrapper, Context context, File directory, NetworkParameters params) throws BlockStoreException {
        this.dogecoinWrapper = dogecoinWrapper;
        File chainFile = new File(directory.getAbsolutePath() + "/SuperblockChain"); //TODO: look into file types
        this.superblockStorage = new SuperblockLevelDBBlockStore(context, chainFile, params);
        this.params = params;
    }


    /**
     * Starts syncing.
     * @param updatePeriod How often the SuperblockChain object will poll dogecoinWrapper for new Doge blocks
     *                     and update its chain file accordingly.
     * @param executionDate
     * @throws BlockStoreException
     * @throws java.io.IOException
     */
    public void initialize(int updatePeriod, Date executionDate) throws BlockStoreException, java.io.IOException {
        new Timer("Update superblock chain").scheduleAtFixedRate(new UpdateSuperblocksTimerTask(), executionDate, updatePeriod);
    }

    /**
     * Builds and maintains a chain of superblocks from the whole Dogecoin blockchain.
     * Writes it to disk as specified by SuperblockLevelDBBlockStore.
     * @throws BlockStoreException
     * @throws IOException
     */
    public void updateChain() throws Exception, BlockStoreException, IOException {
        Superblock bestSuperblock = getChainHead();
        Sha256Hash bestSuperblockLastBlockHash = bestSuperblock.getLastDogeBlockHash();

        // get all the Dogecoin blocks that haven't yet been hashed into a superblock
        Stack<Sha256Hash> allDogeHashesToHash = getDogeBlockHashesNewerThan(bestSuperblockLastBlockHash);
        storeSuperblocks(allDogeHashesToHash, bestSuperblock.getSuperblockId(), this.params); // group them in superblocks accordingly
    }

    private Stack<Sha256Hash> getDogeBlockHashesNewerThan(Sha256Hash blockHash) throws BlockStoreException {
        Stack<Sha256Hash> hashes = new Stack<>();
        StoredBlock currentStoredBlock = dogecoinWrapper.getChainHead();

        while (!currentStoredBlock.getHeader().getHash().equals(blockHash)) {
            hashes.push(currentStoredBlock.getHeader().getHash());
            currentStoredBlock = dogecoinWrapper.getBlock(currentStoredBlock.getHeader().getPrevBlockHash());
        }

        return hashes;
    }

    /**
     * Given a stack of blocks to hash, builds and stores superblocks based on the blocks' timestamps.
     * @param allDogeHashesToHash All the Dogecoin blocks that come after the last block of the last stored superblock.
     *                            This stack should be sorted from least to most recently mined,
     *                            i.e. the top block's hash should be the previous block hash of the block underneath it and so on.
     *                            Modified by function: all the blocks up to and not including the first ('highest') one
     *                            that was mined under three hours ago are popped.
     * @param initialPreviousSuperblockHash Keccak-256 hash of the last stored superblock.
     * @throws Exception
     */
    private void storeSuperblocks(Stack<Sha256Hash> allDogeHashesToHash, byte[] initialPreviousSuperblockHash, NetworkParameters params) throws Exception {
        if (allDogeHashesToHash.empty())
            return;

        List<Sha256Hash> nextSuperblockDogeHashes = new ArrayList<>();

        Date nextSuperblockStartTime = dogecoinWrapper.getBlock(allDogeHashesToHash.peek()).getHeader().getTime();
        Date nextSuperblockEndTime = roundToNextWholeHour(nextSuperblockStartTime);

        byte[] nextSuperblockPrevHash = initialPreviousSuperblockHash.clone();
        StoredBlock nextSuperblockLastBlock;

        long nextSuperblockHeight = getChainHeight() + 1;

        // build and store all superblocks whose last block was mined three hours ago or more
        while (!allDogeHashesToHash.empty() && nextSuperblockEndTime.before(SuperblockUtils.getThreeHoursAgo())) {
            // Modify allDogeHashesToHash and get hashes for next superblock.
            nextSuperblockDogeHashes = popBlocksBeforeTime(allDogeHashesToHash, nextSuperblockEndTime);
            nextSuperblockLastBlock = dogecoinWrapper.getBlock(nextSuperblockDogeHashes.get(nextSuperblockDogeHashes.size() - 1));

            Superblock newSuperblock = new Superblock(params,
                    nextSuperblockDogeHashes,
                    nextSuperblockLastBlock.getChainWork(),
                    nextSuperblockLastBlock.getHeader().getTimeSeconds(),
                    nextSuperblockPrevHash,
                    nextSuperblockHeight);

            superblockStorage.put(newSuperblock);
            superblockStorage.setChainHead(newSuperblock); // TODO: only do this if its chainwork is more than the chain tip's!

            // set prev hash and end time for next superblock
            if (!allDogeHashesToHash.empty()) {
                nextSuperblockPrevHash = newSuperblock.getSuperblockId().clone();
                nextSuperblockStartTime = dogecoinWrapper.getBlock(allDogeHashesToHash.peek()).getHeader().getTime();
                nextSuperblockEndTime = roundToNextWholeHour(nextSuperblockStartTime);
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
    public Superblock getChainHead() throws BlockStoreException {
        return superblockStorage.getChainHead();
    }

    /**
     * Get height of tip of superblock chain.
     * @return Height of tip of superblock chain as saved on disk.
     * @throws BlockStoreException
     */
    public long getChainHeight() throws BlockStoreException {
        return getChainHead().getSuperblockHeight();
    }

    /**
     * Look up a superblock by its hash.
     * @param superblockHash Keccak-256 hash of a superblock.
     * @return Superblock with given hash if it's found in the database, null otherwise.
     */
    public Superblock getSuperblock(byte[] superblockHash) {
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


    /* ---- HELPER METHODS AND CLASSES ---- */

    /**
     * Get the earliest timestamp after the given date that has 0 as its 'minutes' and 'seconds' fields.
     * Useful for getting superblock end times.
     * @param date A timestamp, usually that of the first Dogecoin block in a superblock.
     * @return Earliest whole hour timestamp after `date`.
     */
    private Date roundToNextWholeHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * Task to keep superblock chain updated whenever the agent is running.
     */
    private class UpdateSuperblocksTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                log.debug("UpdateSuperblocksTimerTask");
                updateChain();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}