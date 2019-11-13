package org.sysethereum.agents.core.syscoin;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.store.BlockStoreException;

import org.springframework.stereotype.Service;
import org.sysethereum.agents.constants.AgentConstants;
import org.fusesource.leveldbjni.*;
import org.iq80.leveldb.*;
import org.sysethereum.agents.constants.SystemProperties;
import org.sysethereum.agents.core.bridge.Superblock;
import org.sysethereum.agents.core.bridge.SuperblockData;
import org.sysethereum.agents.core.bridge.SuperblockFactory;
import org.sysethereum.agents.core.bridge.SuperblockSerializationHelper;

import javax.annotation.concurrent.GuardedBy;
import java.io.*;
import java.nio.*;
import java.nio.file.Paths;


/**
 * LevelDB for storing and retrieving superblocks.
 * This class only takes care of storing/retrieving data that it receives from external sources;
 * the information itself is handled by SuperblockChain, primarily via the updateChain() method.
 * @author Catalina Juarros
 */
@Service
@Slf4j(topic = "SuperblockLevelDBBlockStore")
public class SuperblockLevelDBBlockStore {

    private static final byte[] CHAIN_HEAD_KEY = "chainhead".getBytes(); // to store chain head hash
    private final AgentConstants agentConstants;
    private final File path;
    private final SuperblockFactory superblockFactory;
    private final SuperblockSerializationHelper serializationHelper;

    @GuardedBy("this")
    private DB db;

    public SuperblockLevelDBBlockStore(
            AgentConstants agentConstants,
            SystemProperties config,
            SuperblockFactory superblockFactory,
            SuperblockSerializationHelper serializationHelper
    ) {
        this.agentConstants = agentConstants;
        this.path = Paths.get(config.dataDirectory(), "/SuperblockChain").toFile();
        this.superblockFactory = superblockFactory;
        this.serializationHelper = serializationHelper;

        setup();
    }

    private void setup() {
        Options options = new Options();
        options.createIfMissing();

        DBFactory dbFactory = JniDBFactory.factory;

        try {
            tryOpen(path, dbFactory, options);
        } catch (IOException e) {
            try {
                dbFactory.repair(path, options);
                tryOpen(path, dbFactory, options);
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
        }
    }

    /**
     * Open directory.
     * @param directory Where data is stored.
     * @param dbFactory Interface for opening directory.
     * @param options Directory options.
     * @throws IOException
     */
    private synchronized void tryOpen(File directory, DBFactory dbFactory, Options options) throws IOException {
        db = dbFactory.open(directory, options);
        initStoreIfNeeded();
    }

    /**
     * If the database hasn't been initialised, this method sets it up
     * by storing the genesis superblock.
     * Genesis superblock spec:
     * - its only block is the genesis block from whatever Syscoin network it's storing blocks from
     * - since it doesn't have an actual block before it, its parent block hash is hardcoded
     *   as the Keccak-256 hash of a string which consists of the character '0' 32 times
     * - its chain work is 0
     * @throws java.io.IOException
     */
    private synchronized void initStoreIfNeeded() {
        if (db.get(CHAIN_HEAD_KEY) != null)
            return; // Already initialised.
        SuperblockData data = agentConstants.getGenesisSuperblock();
        Superblock genesisSuperblock = superblockFactory.fromData(data);
        put(genesisSuperblock);
        setChainHead(genesisSuperblock);
    }

    /**
     * Writes a superblock to the database.
     * @param sb Superblock to be written.
     * @throws java.io.IOException
     */
    public synchronized void put(Superblock sb) {
        ByteArrayOutputStream stream = serializationHelper.serializeForStorage(sb.data);

        ByteBuffer buffer = ByteBuffer.allocate(stream.size());
        buffer.put(stream.toByteArray());
        db.put(sb.getHash().getBytes(), buffer.array());
    }

    /**
     * Retrieves a deserialized superblock from the database.
     * @param superblockId Keccak-256 hash of superblock.
     * @return superblock identified by hash
     */
    public synchronized Superblock get(Keccak256Hash superblockId) {
        byte[] bits = db.get(superblockId.getBytes());
        if (bits == null)
            return null;

        return superblockFactory.fromBytes(bits);
    }

    /**
     * Closes underlying database.
     * @throws BlockStoreException
     */
    public synchronized void close() {
        try {
            db.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // this was copied more or less word-by-word from bitcoinj

    /**
     * Erases the contents of the database (but NOT the underlying files themselves).
     * @throws BlockStoreException
     */
    @SuppressWarnings("unused")
    public synchronized void reset() throws BlockStoreException {
        try {
            try (WriteBatch batch = db.createWriteBatch()) {
                try (DBIterator it = db.iterator()) {
                    it.seekToFirst();
                    while (it.hasNext())
                        batch.delete(it.next().getKey());
                    db.write(batch);
                }
            }
        } catch (IOException e) {
            throw new BlockStoreException(e);
        }
    }

    @SuppressWarnings("unused")
    public synchronized void destroy() throws IOException {
        JniDBFactory.factory.destroy(path, new Options());
    }


    /* ---- CHAIN HEAD METHODS ---- */

    /**
     * Returns tip of superblock chain. Not necessarily approved in the contracts.
     * @return Highest stored superblock.
     */
    public synchronized Superblock getChainHead() {
        return get(getChainHeadId());
    }

    /**
     * Returns hash of tip of superblock chain. Not necessarily approved in the contracts.
     * @return Highest stored superblock's hash.
     */
    public synchronized Keccak256Hash getChainHeadId() {
        return Keccak256Hash.wrap(db.get(CHAIN_HEAD_KEY));
    }

    /**
     * Sets tip of superblock chain.
     * @param chainHead Superblock with the highest chain work.
     */
    public synchronized void setChainHead(Superblock chainHead) {
        db.put(CHAIN_HEAD_KEY, chainHead.getHash().getBytes());
    }

}
