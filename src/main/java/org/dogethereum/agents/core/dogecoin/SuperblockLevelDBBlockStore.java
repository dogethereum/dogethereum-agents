package org.dogethereum.agents.core.dogecoin;

import org.bitcoinj.core.*;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.core.Context;

import org.dogethereum.agents.constants.AgentConstants;
import org.dogethereum.agents.constants.SystemProperties;
import org.fusesource.leveldbjni.*;
import org.iq80.leveldb.*;

import java.math.BigInteger;
import java.io.*;
import java.nio.*;
import java.util.*;

//import static com.google.common.base.Preconditions.checkState;

/**
 * LevelDB for storing and retrieving superblocks.
 * This class only takes care of storing/retrieving data that it receives from external sources;
 * the information itself is handled by Superblockchain, primarily via the updateChain() method.
 * @author Catalina Juarros
 */

public class SuperblockLevelDBBlockStore {
    private static final byte[] CHAIN_HEAD_KEY = "chainhead".getBytes(); // to store chain head hash

    private final Context context;
    private final File path;
    private DB db;


    /* ---- ESSENTIAL DATABASE METHODS ---- */

    /**
     * Constructor.
     * @param context Dogecoin context.
     * @param directory Where data is stored.
     * @throws BlockStoreException
     */
    public SuperblockLevelDBBlockStore(Context context, File directory, NetworkParameters params)
            throws BlockStoreException {
        this(context, directory, JniDBFactory.factory, params); // this might not work, ask later
    }

    /**
     * Helper for previous constructor.
     * @param context Dogecoin context.
     * @param directory Where data is stored.
     * @param dbFactory Interface for opening and repairing directory if needed.
     * @throws BlockStoreException
     */
    public SuperblockLevelDBBlockStore(Context context, File directory, DBFactory dbFactory, NetworkParameters params)
            throws BlockStoreException {
        this.context = context;
        this.path = directory;
        Options options = new Options();
        options.createIfMissing();

        try {
            tryOpen(directory, dbFactory, options, params);
        } catch (IOException e) {
            try {
                dbFactory.repair(directory, options);
                tryOpen(directory, dbFactory, options, params);
            } catch (IOException e1) {
                throw new BlockStoreException(e1);
            }
        }
    }

    /**
     * Open directory.
     * @param directory Where data is stored.
     * @param dbFactory Interface for opening directory.
     * @param options Directory options.
     * @param params Dogecoin network parameters.
     * @throws IOException
     * @throws BlockStoreException
     */
    private synchronized void tryOpen(File directory, DBFactory dbFactory, Options options, NetworkParameters params)
            throws IOException, BlockStoreException {
        db = dbFactory.open(directory, options);
        initStoreIfNeeded(params);
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
    private synchronized void initStoreIfNeeded(NetworkParameters params) throws IOException, BlockStoreException {
        if (db.get(CHAIN_HEAD_KEY) != null)
            return; // Already initialised.
        SystemProperties config = SystemProperties.CONFIG;
        AgentConstants agentConstants = config.getAgentConstants();
        Superblock genesisSuperblock = agentConstants.getGenesisSuperblock();
        put(genesisSuperblock);
        setChainHead(genesisSuperblock);
    }

    /**
     * Writes a superblock to the database.
     * @param block Superblock to be written.
     * @throws java.io.IOException
     */
    public synchronized void put(Superblock block) throws IOException {
//        buffer.clear();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        block.serializeForStorage(stream);
        ByteBuffer buffer = ByteBuffer.allocate(stream.size());
        buffer.put(stream.toByteArray());
        db.put(block.getSuperblockId().getBytes(), buffer.array());
    }

    /**
     * Retrieves a deserialised superblock from the database.
     * @param superblockId Keccak-256 hash of superblock.
     * @return superblock identified by hash
     */
    public synchronized Superblock get(Keccak256Hash superblockId) throws IOException {
        byte[] bits = db.get(superblockId.getBytes());
        if (bits == null)
            return null;
        return new Superblock(bits);
    }

    /**
     * Closes underlying database.
     * @throws BlockStoreException
     */
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


    /* ---- CHAIN HEAD METHODS ---- */

    /**
     * Returns tip of superblock chain. Not necessarily approved in the contracts.
     * @return Highest stored superblock.
     * @throws BlockStoreException
     */
    public synchronized Superblock getChainHead() throws BlockStoreException, IOException {
        return get(getChainHeadId());
    }

    /**
     * Returns hash of tip of superblock chain. Not necessarily approved in the contracts.
     * @return Highest stored superblock's hash.
     * @throws BlockStoreException
     */
    public synchronized Keccak256Hash getChainHeadId() throws BlockStoreException {
        return Keccak256Hash.wrap(db.get(CHAIN_HEAD_KEY));
    }

    /**
     * Sets tip of superblock chain.
     * @param chainHead Superblock with the highest chain work.
     * @throws BlockStoreException
     */
    public synchronized void setChainHead(Superblock chainHead) throws BlockStoreException, IOException {
        db.put(CHAIN_HEAD_KEY, chainHead.getSuperblockId().getBytes());
    }

    /**
     * Returns tip work.
     * @return Chain head's accumulated work.
     * @throws BlockStoreException
     */
    public synchronized BigInteger getChainHeadWork() throws BlockStoreException, IOException {
        return getChainHead().getChainWork();
    }

}
