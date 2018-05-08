package org.dogethereum.dogesubmitter.core.dogecoin;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.time.DateUtils;
import org.bitcoinj.core.AltcoinBlock;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;
import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.LevelDBBlockStore;
import org.bitcoinj.wallet.Wallet;

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
import java.io.File;
import java.io.InputStream;
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

//    private File dataDirectory;
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
    public SuperblockChain(DogecoinWrapper dogecoinWrapper, Context context, File directory) throws BlockStoreException {
        this.dogecoinWrapper = dogecoinWrapper;
        this.chainFile = new File(directory.getAbsolutePath() + "/SuperblockChain.txt"); //TODO: look into file types
        this.superblockStorage = new SuperblockLevelDBBlockStore(context, chainFile);
//        this.superblockStorage.setChainHead();
    }


    /**
     * Starts syncing
     * @param updatePeriod
     * @param executionDate
     * @throws BlockStoreException
     * @throws java.io.IOException
     */
    public void initialize(int updatePeriod, Date executionDate) throws BlockStoreException, java.io.IOException {
        new Timer("Update superblock chain").scheduleAtFixedRate(new UpdateBridgeTimerTask(), executionDate, updatePeriod);
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
            currentBlock = (AltcoinBlock) dogecoinWrapper.getBlockAtHeight(currentHeight).getHeader();
            currentTime = currentBlock.getTime();
        }

        return currentHeight;
    }

    // testing only for now
    private Superblock calculateGenesisBlock() throws BlockStoreException {
        // maybe this should be a void but I love functional programming too much
        List<AltcoinBlock> blocks = new ArrayList<>();
        byte[] previousSuperblockHash = Hash.sha3("0000000000000000000000000000000000000000000000000000000000000000".getBytes());
        AltcoinBlock dogeGenesis = (AltcoinBlock) dogecoinWrapper.getBlockAtHeight(0).getHeader();

        int endHeight = fillWithBlocksStartingAtTime(dogeGenesis.getTime(), 0, blocks) - 1; // height of last block in the superblock
        BigInteger chainWork = dogecoinWrapper.getBlockAtHeight(endHeight).getChainWork();
        Superblock superblock = new Superblock(blocks, previousSuperblockHash, chainWork, endHeight, 0);
        return superblock;
    }

    // TODO: look into refactoring this -- it could probably be rewritten as smaller, simpler methods. Maybe benchmark the two options.
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
        // TODO: why is bestSuperblockHeight not persisting???? Or maybe it is persisting but getDogeHeight() is returning 839 for some reason. Also, getHeight() is returning 0 when it should return 4.

        // The first time this function is called, currentHeight should be 0
        // and this should be the genesis block
        StoredBlock currentStoredBlock = dogecoinWrapper.getBlockAtHeight(currentHeight);
        BigInteger currentWork; // chain work for the block that will be built from currentBlocksToHash
        AltcoinBlock currentBlock = (AltcoinBlock) currentStoredBlock.getHeader(); // blocks starting from this one will be added to the queue
        Date currentInitialDate = currentBlock.getTime();
        List<AltcoinBlock> currentBlocksToHash = new ArrayList<>();

        byte[] previousSuperblockHash = superblockStorage.getChainHeadHash();

        Date stopDate = getThreeHoursAgo(); // to stop when the last block to be hashed was mined 3 hours ago
        Boolean stopBuilding = false;

        while (currentHeight < bestChainHeight && !stopBuilding) {
            // Modifies currentBlocksToHash.
            // Builds list and advances height to that of the first block that wasn't added,
            // i.e. the first block in the next superblock
            currentHeight = fillWithBlocksStartingAtTime(currentInitialDate, currentHeight, currentBlocksToHash);
            stopDate = getThreeHoursAgo(); // the method might run for a while, so this must be updated for each new block list

            if (currentBlocksToHash.get(currentBlocksToHash.size() - 1).getTime().after(stopDate))
                stopBuilding = true; // last block to hash was mined less than three hours ago, so the blocks should not be hashed yet

            // build superblock and update necessary fields for next superblock
            if (!stopBuilding) {
                currentWork = dogecoinWrapper.getBlockAtHeight(currentHeight - 1).getChainWork(); // chain work of last block in the superblock
                Superblock newSuperblock = new Superblock(currentBlocksToHash, previousSuperblockHash, currentWork, currentHeight - 1, bestSuperblockHeight);
                superblockStorage.put(newSuperblock);
                superblockStorage.setChainHead(newSuperblock);
                bestSuperblockHeight++;

                // remember: currentHeight was updated to the correct value by fillWithBlocksStartingAtTime
                currentStoredBlock = dogecoinWrapper.getBlockAtHeight(currentHeight);
                currentBlock = (AltcoinBlock) currentStoredBlock.getHeader();
                currentInitialDate = currentBlock.getTime();
                previousSuperblockHash = newSuperblock.getSuperblockHash();

                currentBlocksToHash.clear(); // to start again for next superblock
            }
        }
    }

    /* ---- GETTERS ---- */

    public Superblock getGenesisBlock() {
        return genesisBlock;
    }

    public Superblock getChainHead() throws BlockStoreException {
        return superblockStorage.getChainHead();
    }


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
    private class UpdateBridgeTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                log.debug("UpdateBridgeTimerTask");
                updateChain();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}