package org.sysethereum.agents.core.syscoin;

import org.bitcoinj.core.*;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.*;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import static com.google.common.base.Preconditions.checkState;
import static org.bitcoinj.core.StoredBlock.CHAIN_WORK_BYTES;
import static org.bitcoinj.core.StoredBlock.EMPTY_BYTES;

/**
 * Copy of LevelDBBlockStore with a fix for blocks with AuxPoW
 * put() does not call StoredBlock.serializeCompact(),
 * it uses a custom serializeCompact() instead where block size is not trimmed to 80 bytes
 */
public class AltcoinLevelDBBlockStore implements BlockStore {

    private static final byte[] CHAIN_HEAD_KEY = "chainhead".getBytes();

    private final Context context;
    private DB db;
    // Block header could be huge because litecoin coinbase tx could be almost 1 mb in size
    //public static final int COMPACT_SERIALIZED_SIZE = Block.MAX_BLOCK_SIZE * 2 + CHAIN_WORK_BYTES + 4;  // for height
    public static final int COMPACT_SERIALIZED_SIZE = 20000 + CHAIN_WORK_BYTES + 4;  // for height
    private final ByteBuffer buffer = ByteBuffer.allocate(COMPACT_SERIALIZED_SIZE);
    private final File path;

    /** Creates a LevelDB SPV block store using the JNI/C++ version of LevelDB. */
    public AltcoinLevelDBBlockStore(Context context, File directory) throws BlockStoreException {
        this(context, directory, JniDBFactory.factory);
    }

    /** Creates a LevelDB SPV block store using the given factory, which is useful if you want a pure Java version. */
    public AltcoinLevelDBBlockStore(Context context, File directory, DBFactory dbFactory) throws BlockStoreException {
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

    private synchronized void tryOpen(File directory, DBFactory dbFactory, Options options)
            throws IOException, BlockStoreException {
        db = dbFactory.open(directory, options);
        initStoreIfNeeded();
    }

    private synchronized void initStoreIfNeeded() throws BlockStoreException {
        if (db.get(CHAIN_HEAD_KEY) != null)
            return;   // Already initialised.
        Block genesis = context.getParams().getGenesisBlock().cloneAsHeader();
        StoredBlock storedGenesis = new StoredBlock(genesis, genesis.getWork(), 0);
        put(storedGenesis);
        setChainHead(storedGenesis);
    }

    @Override
    public synchronized void put(StoredBlock block) throws BlockStoreException {
        buffer.clear();
        serializeCompact(block, buffer);
        int arraySize = buffer.position();
        byte[] array = new byte[arraySize];
        byte[] bufferArray = buffer.array();
        System.arraycopy(bufferArray, 0, array, 0, arraySize);
        db.put(block.getHeader().getHash().getBytes(), array);
    }

    private void serializeCompact(StoredBlock block, ByteBuffer buffer) {
        byte[] chainWorkBytes = block.getChainWork().toByteArray();
        checkState(chainWorkBytes.length <= CHAIN_WORK_BYTES, "Ran out of space to store chain work!");
        if (chainWorkBytes.length < CHAIN_WORK_BYTES) {
            // Pad to the right size.
            buffer.put(EMPTY_BYTES, 0, CHAIN_WORK_BYTES - chainWorkBytes.length);
        }
        buffer.put(chainWorkBytes);
        buffer.putInt(block.getHeight());
        // Using bitcoinSerialize instead of unsafeBitcoinSerialize as used in the original implementation because we are not going to remove the
        // trailing 00 byte
        byte[] bytes = block.getHeader().bitcoinSerialize();
        buffer.put(bytes);  // DON'T Trim the trailing 00 byte (zero transactions) - It would also trim the AuxPow.
    }


    @Override @Nullable
    public synchronized StoredBlock get(Sha256Hash hash) throws BlockStoreException {
        byte[] bits = db.get(hash.getBytes());
        if (bits == null)
            return null;
        return deserializeCompact(context.getParams(), ByteBuffer.wrap(bits));
    }

    /** De-serializes the stored block from a custom packed format. Used by {@link CheckpointManager}. */
    public StoredBlock deserializeCompact(NetworkParameters params, ByteBuffer buffer) throws ProtocolException {
        byte[] chainWorkBytes = new byte[StoredBlock.CHAIN_WORK_BYTES];
        buffer.get(chainWorkBytes);
        BigInteger chainWork = new BigInteger(1, chainWorkBytes);
        int height = buffer.getInt();  // +4 bytes
        //byte[] header = new byte[buffer.remaining() + 1];    // Extra byte for the 00 transactions length.
        byte[] header = new byte[buffer.remaining()]; // Don't add Extra byte for the 00 transactions length because it should be already included in the serialized header
        buffer.get(header, 0, buffer.remaining());
        return new StoredBlock(params.getDefaultSerializer().makeBlock(header), chainWork, height);
    }


    @Override
    public synchronized StoredBlock getChainHead() throws BlockStoreException {
        return get(Sha256Hash.wrap(db.get(CHAIN_HEAD_KEY)));
    }

    @Override
    public synchronized void setChainHead(StoredBlock chainHead) throws BlockStoreException {
        db.put(CHAIN_HEAD_KEY, chainHead.getHeader().getHash().getBytes());
    }

    @Override
    public synchronized void close() throws BlockStoreException {
        try {
            db.close();
        } catch (IOException e) {
            throw new BlockStoreException(e);
        }
    }

    /** Erases the contents of the database (but NOT the underlying files themselves)
     * and then reinitialises with the genesis block. */
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
            initStoreIfNeeded();
        } catch (IOException e) {
            throw new BlockStoreException(e);
        }
    }

    public synchronized void destroy() throws IOException {
        JniDBFactory.factory.destroy(path, new Options());
    }

    @Override
    public NetworkParameters getParams() {
        return context.getParams();
    }
}
