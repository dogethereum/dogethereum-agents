package org.dogethereum.dogesubmitter.core.dogecoin;

import jnr.ffi.annotations.Out;
import org.dogethereum.dogesubmitter.BridgeUtils;
import org.dogethereum.dogesubmitter.constants.BridgeConstants;
import org.dogethereum.dogesubmitter.core.dogecoin.Superblock;

import org.bitcoinj.core.*;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.core.Context;

import org.fusesource.leveldbjni.*;
import org.iq80.leveldb.*;

import com.google.common.base.Objects;

import java.math.BigInteger;
import java.io.*;
import java.nio.*;
import java.util.Locale;
import java.util.Optional;

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
    private final ByteBuffer buffer = ByteBuffer.allocate(Superblock.COMPACT_SERIALIZED_SIZE);
    // TODO: hardcode genesis superblock. Keep in mind that it might not be necessary, since SuperblockChain is going to store superblocks here.

    public SuperblockLevelDBBlockStore(Context context, File directory) throws BlockStoreException {
        this(context, directory, JniDBFactory.factory); // this might not work, ask later
    }

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
    }

    /**
     * If the database hasn't been initialised, this method sets it up
     * by storing the genesis superblock.
     * @throws java.io.IOException
     * @throws BlockStoreException
     */
    // TODO: see how to get rid of this method or integrate it with SuperblockChain
    // this doesn't seem necessary honestly
//    private synchronized void initStoreIfNeeded() throws java.io.IOException, BlockStoreException {
//        if (db.get(CHAIN_HEAD_KEY) != null)
//            return; // Already initialised.
////        Superblock genesisBlock = chain.getGenesisBlock();
////        put(genesisBlock);
////        setChainHead(genesisBlock);
//    }

    /**
     * Write a superblock to the database.
     * @param block superblock to be written
     * @throws java.io.IOException
     */
    public synchronized void put(Superblock block) throws java.io.IOException {
        buffer.clear();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        block.serialize(stream);
        buffer.put(stream.toByteArray());
        db.put(block.getSuperblockHash(), buffer.array());
    }

    /**
     * Retrieve a deserialised superblock from the database.
     * @param hash Keccak-256 hash of superblock
     * @return superblock identified by hash
     */
    public synchronized Superblock get(byte[] hash) {
        byte[] bits = db.get(hash);
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

    public synchronized byte[] getChainHeadHash() throws BlockStoreException, java.io.IOException {
        return getChainHead().getSuperblockHash();
    }

    /**
     * Sets tip of superblock chain.
     * @param chainHead Last stored superblock.
     * @throws BlockStoreException
     * @throws java.io.IOException
     */
    public synchronized void setChainHead(Superblock chainHead) throws BlockStoreException, java.io.IOException {
        db.put(CHAIN_HEAD_KEY, chainHead.getSuperblockHash());
    }

    /**
     * Gets height of highest stored superblock so that SuperblockChain can stay synchronised.
     * @return Height of superblock chain tip.
     * @throws BlockStoreException
     */
    public synchronized int getHeight() throws BlockStoreException {
        return getChainHead().getHeight();
    }

    /**
     * Gets height of latest hashed Doge block so that SuperblockChain can stay synchronised.
     * @return Height of last Doge block in highest stored superblock.
     * @throws BlockStoreException
     */
    public synchronized int getDogeHeight() throws BlockStoreException {
        return getChainHead().getLastBlockHeight();
    }
}
