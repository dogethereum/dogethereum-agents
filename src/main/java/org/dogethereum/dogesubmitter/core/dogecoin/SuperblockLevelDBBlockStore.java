package org.dogethereum.dogesubmitter.core.dogecoin;

import jnr.ffi.annotations.Out;
import org.dogethereum.dogesubmitter.*;
import org.dogethereum.dogesubmitter.core.dogecoin.Superblock;

import org.bitcoinj.core.*;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.core.Context;

import org.web3j.crypto.Hash;

import org.fusesource.leveldbjni.*;
import org.iq80.leveldb.*;

import com.google.common.base.Objects;

import java.math.BigInteger;
import java.io.*;
import java.nio.*;
import java.util.*;

//import static com.google.common.base.Preconditions.checkState;

/**
 * LevelDB for storing and retrieving superblocks.
 * This class only takes care of storing/retrieving data that it receives from external sources;
 * the information itself is handled by SuperblockChain, primarily via the updateChain() method.
 * @author Catalina Juarros
 */

public class SuperblockLevelDBBlockStore {
    private static final byte[] CHAIN_HEAD_KEY = "chainhead".getBytes(); // to store chain head hash

    private final Context context;
    private final File path;
    private DB db;

    /**
     * Constructor.
     * @param context
     * @param directory
     * @throws BlockStoreException
     */
    public SuperblockLevelDBBlockStore(Context context, File directory) throws BlockStoreException {
        this(context, directory, JniDBFactory.factory); // this might not work, ask later
    }

    /**
     * Helper for previous constructor.
     * @param context
     * @param directory
     * @param dbFactory
     * @throws BlockStoreException
     */
    public SuperblockLevelDBBlockStore(Context context, File directory, DBFactory dbFactory) throws BlockStoreException {
        this.context = context;
        this.path = directory;
        Options options = new Options();
        options.createIfMissing();

        try {
            tryOpen(directory, dbFactory, options);
        } catch (IOException e) {
            try {
                dbFactory.repair(directory, options);
                tryOpen(directory, dbFactory, options);
            } catch (IOException e1) {
                throw new BlockStoreException(e1);
            }
        }
    }

    private synchronized void tryOpen(File directory, DBFactory dbFactory, Options options) throws IOException, BlockStoreException {
        db = dbFactory.open(directory, options);
        initStoreIfNeeded();
    }

    /**
     * If the database hasn't been initialised, this method sets it up
     * by storing the genesis superblock.
     * Genesis superblock spec:
     * - its only block is the genesis block from whatever Doge network it's storing blocks from
     * - since it doesn't have an actual block before it, its parent block hash is hardcoded
     *   as the Keccak-256 hash of a string which consists of the character '0' 32 times
     * - its chain work is 0
     * @throws java.io.IOException
     * @throws BlockStoreException
     */
    private synchronized void initStoreIfNeeded() throws IOException, BlockStoreException {
        if (db.get(CHAIN_HEAD_KEY) != null)
            return; // Already initialised.
        List<Sha256Hash> genesisList = new ArrayList<>();
        genesisList.add(context.getParams().getGenesisBlock().getHash());
        byte[] genesisParentHash = new byte[32]; // initialised with 0s
        Superblock genesisBlock = new Superblock(genesisList, BigInteger.valueOf(0), context.getParams().getGenesisBlock().getTimeSeconds(), genesisParentHash, 0);
        put(genesisBlock);
        setChainHead(genesisBlock);
    }

    /**
     * Write a superblock to the database.
     * @param block superblock to be written
     * @throws java.io.IOException
     */
    public synchronized void put(Superblock block) throws IOException {
//        buffer.clear();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        block.serializeForStorage(stream);
        ByteBuffer buffer = ByteBuffer.allocate(stream.size());
        buffer.put(stream.toByteArray());
        db.put(block.getSuperblockId(), buffer.array());
    }

    /**
     * Retrieve a deserialised superblock from the database.
     * @param superblockId Keccak-256 hash of superblock
     * @return superblock identified by hash
     */
    public synchronized Superblock get(byte[] superblockId) {
        byte[] bits = db.get(superblockId);
        if (bits == null)
            return null;
        return new Superblock(bits);
    }

    public synchronized void close() throws BlockStoreException {
        try {
            db.close();
        } catch (IOException e) {
            throw new BlockStoreException(e);
        }
    }

    // this was copied more or less word-by-word from bitcoinj

    /**
     * Erases the contents of the database (but NOT the underlying files themselves).
     * @throws BlockStoreException
     */
    public synchronized void reset() throws BlockStoreException {
        try {
            WriteBatch batch = db.createWriteBatch();
            try {
                DBIterator it = db.iterator();
                try {
                    it.seekToFirst();
                    while (it.hasNext())
                        batch.delete(it.next().getKey());
                    db.write(batch);
                } finally {
                    it.close();
                }
            } finally {
                batch.close();
            }
        } catch (IOException e) {
            throw new BlockStoreException(e);
        }
    }

    public synchronized void destroy() throws IOException {
        JniDBFactory.factory.destroy(path, new Options());
    }

    /**
     * Gets tip of superblock chain.
     * @return Highest stored superblock.
     * @throws BlockStoreException
     */
    public synchronized Superblock getChainHead() throws BlockStoreException {
        return get(db.get(CHAIN_HEAD_KEY));
    }

    /**
     * Gets hash of tip of superblock chain.
     * @return Highest stored superblock's hash.
     * @throws BlockStoreException
     * @throws IOException
     */
    public synchronized byte[] getChainHeadId() throws BlockStoreException, IOException {
        return db.get(CHAIN_HEAD_KEY);
    }

    /**
     * Sets tip of superblock chain.
     * @param chainHead Last stored superblock.
     * @throws BlockStoreException
     * @throws java.io.IOException
     */
    public synchronized void setChainHead(Superblock chainHead) throws BlockStoreException, IOException {
        db.put(CHAIN_HEAD_KEY, chainHead.getSuperblockId());
    }

}
