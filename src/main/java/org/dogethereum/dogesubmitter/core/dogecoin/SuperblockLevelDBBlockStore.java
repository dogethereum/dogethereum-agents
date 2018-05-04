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
 * Unfortunately, it cannot implement BlockStore,
 * since some of its methods would need to return StoredBlock objects
 * as opposed to Superblock objects.
 * Possible fixes: define a StoredSuperblock class
 * that inherits from StoredBlock.
 */

public class SuperblockLevelDBBlockStore {
    private static byte[] CHAIN_HEAD_KEY;

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
    private synchronized void initStoreIfNeeded() throws java.io.IOException, BlockStoreException {
        if (db.get(CHAIN_HEAD_KEY) != null)
            return; // Already initialised.
//        Superblock genesisBlock = chain.getGenesisBlock();
//        put(genesisBlock);
//        setChainHead(genesisBlock);
    }

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
     * Retrieve an unserialised superblock from the database.
     * @param hash Keccak-256 hash of superblock
     * @return superblock identified by hash
     */
    public synchronized Superblock get(byte[] hash) {
        byte[] bits = db.get(hash);
        if (bits == null)
            return null;
        Superblock superblock = new Superblock(bits);
        return superblock;
    }

    public synchronized void close() throws BlockStoreException {
        try {
            db.close();
        } catch (IOException e) {
            throw new BlockStoreException(e);
        }
    }

    public synchronized void destroy() throws IOException {
        JniDBFactory.factory.destroy(path, new Options());
    }

    public synchronized Superblock getChainHead() throws BlockStoreException {
        return get(db.get(CHAIN_HEAD_KEY));
    }

    public synchronized void setChainHead(Superblock chainHead) throws BlockStoreException, java.io.IOException {
        db.put(CHAIN_HEAD_KEY, chainHead.getSuperblockHash());
    }
}
