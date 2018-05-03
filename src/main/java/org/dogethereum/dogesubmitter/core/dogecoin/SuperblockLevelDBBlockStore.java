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

    public SuperblockLevelDBBlockStore(Context context, File directory) {
        this(context, directory, JniDBFactory.factory);
    }

    public SuperblockLevelDBBlockStore(Context context, File directory, DBFactory dbFactory) throws BlockStoreException {
        this.context = context;
        this.path = directory;
        Options options = new Options();

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

//    public synchronized Superblock getChainHead() throws BlockStoreException {}

    public synchronized void put(Superblock block) throws java.io.IOException {
        buffer.clear();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        block.serialize(stream);
        buffer.put(stream.toByteArray());
        db.put(block.getSuperblockHash(), buffer.array());
    }

    public synchronized Superblock get(Sha256Hash hash) {
        byte[] bits = db.get(hash.getBytes());
        if (bits == null)
            return null;
        Superblock superblock = new Superblock(bits);
        return superblock;
    }

}
