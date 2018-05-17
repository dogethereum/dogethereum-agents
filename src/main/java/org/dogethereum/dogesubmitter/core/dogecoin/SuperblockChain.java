package org.dogethereum.dogesubmitter.core.dogecoin;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.time.DateUtils;
import org.bitcoinj.core.AltcoinBlock;
import org.bitcoinj.core.*;
import org.bitcoinj.store.BlockStoreException;

import org.dogethereum.dogesubmitter.constants.SystemProperties;
import org.dogethereum.dogesubmitter.util.OperatorKeyHandler;
import org.dogethereum.dogesubmitter.constants.BridgeConstants;
import org.dogethereum.dogesubmitter.util.FileUtil;
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
    // TODO: look into using BlockChain instead of dogecoinWrapper.
    private DogecoinWrapper dogecoinWrapper; // Interface with the Doge blockchain
//    private final Context context;

    private SuperblockLevelDBBlockStore superblockStorage; // database for storing superblocks

    private ArrayList<AltcoinBlock> currentBlocksToHash; // Dogecoin blocks queued for being hashed into a superblock


    /* ---- CONSTRUCTION METHODS ---- */

    /**
     * Class constructor
     * @param dogecoinWrapper Dogecoin blockchain interface
     */
    public SuperblockChain(DogecoinWrapper dogecoinWrapper, Context context, File directory) throws BlockStoreException {
        this.dogecoinWrapper = dogecoinWrapper;
        File chainFile = new File(directory.getAbsolutePath() + "/SuperblockChain"); //TODO: look into file types
        this.superblockStorage = new SuperblockLevelDBBlockStore(context, chainFile);
    }


    /**
     * Starts syncing
     * @param updatePeriod
     * @param executionDate
     * @throws BlockStoreException
     * @throws java.io.IOException
     */
    public void initialize(int updatePeriod, Date executionDate) throws BlockStoreException, java.io.IOException {
        new Timer("Update superblock chain").scheduleAtFixedRate(new UpdateSuperblocksTimerTask(), executionDate, updatePeriod);
    }

    // testing only for now
//    private Superblock calculateGenesisBlock() throws BlockStoreException {
//        // maybe this should be a void but I love functional programming too much
//        List<AltcoinBlock> blocks = new ArrayList<>();
//        byte[] previousSuperblockHash = Hash.sha3("0000000000000000000000000000000000000000000000000000000000000000".getBytes());
//        AltcoinBlock dogeGenesis = (AltcoinBlock) dogecoinWrapper.getBlockAtHeight(0).getHeader();
//
//        int endHeight = fillWithBlocksStartingAtTime(dogeGenesis.getTime(), 0, blocks, 1) - 1; // height of last block in the superblock
//        BigInteger chainWork = dogecoinWrapper.getBlockAtHeight(endHeight).getChainWork();
//        Superblock superblock = new Superblock(blocks, previousSuperblockHash, chainWork, endHeight, 0);
//        return superblock;
//    }

    /**
     * Builds and maintains a chain of superblocks from the whole Dogecoin blockchain.
     * Writes it to disk as specified by SuperblockLevelDBBlockStore.
     * @throws BlockStoreException
     * @throws java.io.IOException
     */
    public void updateChain() throws Exception {
//        Superblock lastCorrectSuperblock = findLastCorrectSuperblock(); // see if there's been a reorg
        Superblock bestSuperblock = getChainHead();
        Sha256Hash bestSuperblockLastBlockHash = bestSuperblock.getLastDogeBlockHash();

        // get all the Dogecoin blocks that haven't yet been hashed into a superblock
        Stack<Sha256Hash> allDogeHashesToHash = getBlockHashesNewerThan(bestSuperblockLastBlockHash);
        storeSuperblocks(allDogeHashesToHash, bestSuperblock.getSuperblockHash()); // group them in superblocks accordingly
    }

    private Stack<Sha256Hash> getBlockHashesNewerThan(Sha256Hash blockHash) throws BlockStoreException {
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
    private void storeSuperblocks(Stack<Sha256Hash> allDogeHashesToHash, byte[] initialPreviousSuperblockHash) throws Exception {
        if (allDogeHashesToHash.empty())
            return;

        List<Sha256Hash> nextSuperblockDogeHashes = new ArrayList<>();

        Date nextSuperblockStartTime = dogecoinWrapper.getBlock(allDogeHashesToHash.peek()).getHeader().getTime();
        Date nextSuperblockEndTime = roundToNextWholeHour(nextSuperblockStartTime);

        byte[] nextSuperblockPrevHash = initialPreviousSuperblockHash.clone();
        StoredBlock nextSuperblockLastBlock;

        // build and store all superblocks whose last block was mined three hours ago or more
        while (!allDogeHashesToHash.empty() && nextSuperblockEndTime.before(getThreeHoursAgo())) {
            // Modify allDogeHashesToHash and get hashes for next superblock.
            nextSuperblockDogeHashes = popBlocksBeforeTime(allDogeHashesToHash, nextSuperblockEndTime);
            nextSuperblockLastBlock = dogecoinWrapper.getBlock(nextSuperblockDogeHashes.get(nextSuperblockDogeHashes.size() - 1));

            Superblock newSuperblock = new Superblock(nextSuperblockDogeHashes,
                    nextSuperblockLastBlock.getChainWork(),
                    nextSuperblockLastBlock.getHeader().getTimeSeconds(),
                    nextSuperblockPrevHash);

            superblockStorage.put(newSuperblock);
            superblockStorage.setChainHead(newSuperblock);

            // set prev hash and end time for next superblock
            nextSuperblockPrevHash = newSuperblock.getSuperblockHash().clone();
            if (!allDogeHashesToHash.empty()) {
                nextSuperblockStartTime = dogecoinWrapper.getBlock(allDogeHashesToHash.peek()).getHeader().getTime();
                nextSuperblockEndTime = roundToNextWholeHour(nextSuperblockStartTime);
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

    public Superblock getChainHead() throws BlockStoreException {
        return superblockStorage.getChainHead();
    }

//    public Superblock getGenesisBlock() {
    //        return genesisBlock;
//    }


    /* ---- HELPER METHODS AND CLASSES ---- */

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

    /**
     * Task to keep superblock chain updated whenever the agent is running
     */
    // TODO: move outside of SuperblockChain
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