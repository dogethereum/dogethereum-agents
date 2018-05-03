package org.dogethereum.dogesubmitter.core.dogecoin;

import org.apache.commons.lang3.time.DateUtils;
import org.bitcoinj.core.AltcoinBlock;
import org.bitcoinj.core.*;
import org.bitcoinj.store.BlockStoreException;

import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.crypto.Hash;

import javax.annotation.PostConstruct;
import java.io.File;
import java.math.BigInteger;
import java.util.*;

/**
 * Provides methods for interacting with a superblock chain.
 * Storage is managed by SuperblockLevelDBBlockStore.
 * @author Catalina Juarros
 */

public class SuperblockChain {
    @Autowired
    private DogecoinWrapper dogecoinWrapper; // Interface with the Doge blockchain
    private final Context context;

    private File dataDirectory; // TODO: see if this is even necessary
    private File chainFile; // where the superblock chain is going to be stored in the disk
    private SuperblockLevelDBBlockStore superblockStorage; // database for storing superblocks

    private byte[] previousSuperblockHash; // hash of last calculated proper superblock
    private Superblock genesisBlock; // TODO: get rid of this after testing/debugging

    private ArrayList<AltcoinBlock> currentBlocksToHash; // Dogecoin blocks queued for being hashed into a superblock


    /* ---- CONSTRUCTION METHODS ---- */

    /**
     * Class constructor
     * @param dogecoinWrapper Dogecoin blockchain interface
     */
    public SuperblockChain(DogecoinWrapper dogecoinWrapper, Context context) throws BlockStoreException {
        this.dogecoinWrapper = dogecoinWrapper;
        this.genesisBlock = calculateGenesisBlock(); // TODO: get rid of this after testing/debugging
        this.context = context;
        this.superblockStorage = new SuperblockLevelDBBlockStore(context, chainFile);
    }

    @PostConstruct
    public void initialize() throws BlockStoreException, java.io.IOException {
        updateChain();
    }

//    public void syncWithDogeBlockchain() {}

    /**
     * Gets necessary blocks for building a superblock starting at a given time
     * @param initialDate Timestamp of the first block in the superblock
     * @param initialHeight Height of the first block in the superblock
     * @param blocks List to add the blocks to
     * @return Height of the earliest Doge block that was not added to the list
     * @throws BlockStoreException
     */
    private int fillWithBlocksStartingAtTime(Date initialDate, int initialHeight, List<AltcoinBlock> blocks) throws BlockStoreException {
        int bestChainHeight = dogecoinWrapper.getBestChainHeight();
        int currentHeight = initialHeight;
        AltcoinBlock currentBlock = (AltcoinBlock) dogecoinWrapper.getBlockAtHeight(initialHeight).getHeader();
        Date currentTime = currentBlock.getTime();
        Date superblockEndTime = roundToNextWholeHour(currentTime);

        // While the current block was *not* mined an hour or more after the initial date,
        // keep adding blocks to the array.
        // This is equivalent to saying the current block's truncated date
        // is less than or equal to the initial date.
        // It's allowed to be less than the initial date
        // because a Dogecoin timestamp can be less than that of a previous block
        // and still be valid.
        while (currentTime.before(superblockEndTime) && currentHeight < bestChainHeight) {
            blocks.add(currentBlock);
            currentHeight++; // loop condition ensures that this height will always be valid
            currentBlock = (AltcoinBlock) dogecoinWrapper.getStoredBlockAtHeight(currentHeight).getHeader();
            currentDate = currentBlock.getTime();
        }

        return currentHeight;
    }

    /**
     * Builds and maintains a chain of superblocks from the whole Dogecoin blockchain.
     * Writes it to disk as specified by SuperblockLevelDBBlockStore.
     * @throws BlockStoreException
     * @throws java.io.IOException
     */
    public void updateChain() throws BlockStoreException, java.io.IOException {
        int bestChainHeight = dogecoinWrapper.getBestChainHeight();
        int currentHeight = superblockStorage.getDogeHeight();
        int bestSuperblockHeight = superblockStorage.getHeight();

        StoredBlock currentStoredBlock = dogecoinWrapper.getStoredBlockAtHeight(currentHeight); // start with genesis block
        BigInteger currentWork = currentStoredBlock.getChainWork(); // chain work for the block that will be built from currentBlocksToHash
        AltcoinBlock currentBlock = (AltcoinBlock) currentStoredBlock.getHeader(); // first block of the next superblock
        Date currentInitialDate = currentBlock.getTime(); // timestamp of the first block of the next superblock

            // build superblock and update necessary fields for next superblock
            if (!stopBuilding) {
                currentWork = dogecoinWrapper.getBlockAtHeight(currentHeight - 1).getChainWork(); // chain work of last block in the superblock
                Superblock newSuperblock = new Superblock(currentBlocksToHash, previousSuperblockHash, currentWork, currentHeight - 1, bestSuperblockHeight);
                superblockStorage.put(newSuperblock);
                superblockStorage.setChainHead(newSuperblock);
                bestSuperblockHeight++;

        while (currentHeight < bestChainHeight) {
            // builds list and advances height
            currentHeight = fillWithBlocksStartingAtTime(currentInitialDate, currentHeight, currentBlocksToHash);
            Superblock newSuperblock = new Superblock(currentBlocksToHash, previousSuperblockHash, currentWork, currentHeight);
            superblocks.add(newSuperblock);

            // update necessary fields for next superblock
            if (currentHeight <= bestChainHeight) {
                currentStoredBlock = dogecoinWrapper.getStoredBlockAtHeight(currentHeight);
                currentWork = currentStoredBlock.getChainWork();
                currentBlock = (AltcoinBlock) currentStoredBlock.getHeader();
                currentInitialDate = currentBlock.getTime();
                previousSuperblockHash = newSuperblock.getSuperblockHash();

                currentBlocksToHash.clear(); // to start again for next superblock
            }
        }
    }

//    public void buildChainFromHeight(int initialHeight) throws BlockStoreException, java.io.IOException {
//        int bestChainHeight = dogecoinWrapper.getBestChainHeight();
//        StoredBlock currentStoredBlock = dogecoinWrapper.getBlockAtHeight(initialHeight);
//        AltcoinBlock currentBlock = (AltcoinBlock) currentStoredBlock.getHeader();
//        long firstBlockOfSuperblockTime = currentStoredBlock.getHeader().getTimeSeconds(); // timestamp of the first block in the superblock being built
//        byte[] previousSuperblockHash = new byte[1]; // dummy value for now
//        BigInteger currentInitialWork = currentStoredBlock.getChainWork(); // chain work of the first block in the superblock being built
//
//        // list of blocks that a superblock will be composed of
//        // this must be cleared after the superblock is built
//        List<AltcoinBlock> blocksToHash = new ArrayList<>();
//        blocksToHash.add(currentBlock);
//        // I'll probably do without this when I figure out how to write a superblock to disk
//        List<Superblock> superblocks = new ArrayList<>();
//
//        for (int i = initialHeight + 1; i < bestChainHeight; i++) {
//            currentStoredBlock = dogecoinWrapper.getBlockAtHeight(i);
//            currentBlock = (AltcoinBlock) currentStoredBlock.getHeader();
//
//            if (currentBlock.getTimeSeconds() - firstBlockOfSuperblockTime > 3600) {
//                // blocksToHash already contains blocks mined within an hour,
//                // so they must be hashed into a superblock and the list must be cleared
//                Superblock newSuperblock = new Superblock(blocksToHash, previousSuperblockHash, currentInitialWork);
//                superblocks.add(newSuperblock);
//                previousSuperblockHash = newSuperblock.getHash(); // TODO: maybe I need to hash the whole data - ask later
//
//                // restart list and set currentBlock as the first block of the next superblock
//                blocksToHash = new ArrayList<>();
//                blocksToHash.add(currentBlock);
//                currentInitialWork = currentStoredBlock.getChainWork();
//            } else {
//                blocksToHash.add(currentBlock);
//            }
//        }
//    }

    /* ---- GETTERS ---- */

    public Superblock getGenesisBlock() {
        return genesisBlock;
    }

    public Superblock getChainHead() throws BlockStoreException {
        return superblockStorage.getChainHead();
    }


    /* ---- HELPER METHODS ---- */

    // I hate that I have to define this inside the SuperblockChain class
    // instead of just a function that can be applied to dates
    private Date roundToNextWholeHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    private Date getThreeHoursAgo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -3);
        return calendar.getTime();
    }
}