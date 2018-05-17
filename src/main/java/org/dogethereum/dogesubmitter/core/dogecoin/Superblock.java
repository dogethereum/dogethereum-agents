package org.dogethereum.dogesubmitter.core.dogecoin;

import jnr.ffi.annotations.Out;
import org.bitcoinj.core.*;

import org.web3j.crypto.Hash;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Constructs a superblock from a sequence of block hashes.
 * Also provides methods for serialising and deserialising superblocks.
 * @author Catalina Juarros
 */

// TODO: replace all instances of java.io.IOException with just IOException -- remember to do it in EVERY FILE
// TODO: same thing with classes
// TODO: check if data is big endian or little endian for documentation

public class Superblock {

    /* ---- INFO FIELDS ---- */

    private Sha256Hash merkleRoot; // Root of a Merkle tree comprised of Dogecoin block hashes. 32 bytes.
    private BigInteger chainWork; // Total chain work put into this superblock -- same as total chain work put into last block. 32 bytes.
    private Sha256Hash lastDogeBlockHash; // SHA-256 hash of last mined Dogecoin block in the superblock. 32 bytes.
    private long lastDogeBlockTime; // Timestamp of last mined Dogecoin block in the superblock. 4 bytes.
    private byte[] prevSuperblockHash; // KECCAK-256 hash of previous superblock. 32 bytes.


    /* ---- EXTRA FIELDS ---- */

    private byte[] hash; // KECCAK-256 hash of superblock data
    private List<Sha256Hash> dogeBlockHashes;


    /* ---- CONSTANTS ---- */

    public static final int HASH_BYTES_LENGTH = 32;
    public static final int BIG_INTEGER_LENGTH = 32;
    public static final int UINT32_LENGTH = 4;

    public static final int MERKLE_ROOT_PAYLOAD_OFFSET = 0;
    public static final int CHAIN_WORK_PAYLOAD_OFFSET = MERKLE_ROOT_PAYLOAD_OFFSET + HASH_BYTES_LENGTH;
    public static final int LAST_BLOCK_HASH_PAYLOAD_OFFSET = CHAIN_WORK_PAYLOAD_OFFSET + BIG_INTEGER_LENGTH;
    public static final int LAST_BLOCK_TIME_PAYLOAD_OFFSET = LAST_BLOCK_HASH_PAYLOAD_OFFSET + HASH_BYTES_LENGTH;
    public static final int PREV_SUPERBLOCK_HASH_PAYLOAD_OFFSET = LAST_BLOCK_TIME_PAYLOAD_OFFSET + UINT32_LENGTH;

    public static final int NUMBER_OF_HASHES_PAYLOAD_OFFSET = PREV_SUPERBLOCK_HASH_PAYLOAD_OFFSET + HASH_BYTES_LENGTH;
    public static final int DOGE_BLOCK_HASHES_PAYLOAD_OFFSET = NUMBER_OF_HASHES_PAYLOAD_OFFSET + UINT32_LENGTH;

    public static final int COMPACT_SERIALIZED_SIZE = DOGE_BLOCK_HASHES_PAYLOAD_OFFSET + HASH_BYTES_LENGTH * 100; // this is an upper bound, make it flexible later


    /* ---- CONSTRUCTION METHODS ---- */

    /**
     * Construct a Superblock object from a list of Dogecoin blocks,
     * the previous superblock's hash and the accumulated chain work from its last block.
     * Callers should ensure that `work` is indeed the chain work corresponding
     * to the last element of `blocks`.
     * @param dogeBlockHashes List of hashes belonging to all Dogecoin blocks mined within the one hour lapse corresponding to this superblock.
     * @param chainWork Last Dogecoin block's accumulated chainwork.
     * @param prevSuperblockHash Previous superblock's SHA-256 hash.
     */
    public Superblock(List<Sha256Hash> dogeBlockHashes, BigInteger chainWork, long lastDogeBlockTime, byte[] prevSuperblockHash) {
        // hash all the block dogeBlockHashes into a Merkle tree
        this.merkleRoot = calculateMerkleRoot(dogeBlockHashes);
        this.chainWork = chainWork;
        this.lastDogeBlockHash = dogeBlockHashes.get(dogeBlockHashes.size() - 1);
        this.lastDogeBlockTime = lastDogeBlockTime;
        this.prevSuperblockHash = prevSuperblockHash.clone();

        this.dogeBlockHashes = new ArrayList<>(dogeBlockHashes);
    }

    /**
     * Construct a Superblock object from an array representing a serialized superblock.
     * @param payload Serialized superblock.
     * @throws ProtocolException if payload length doesn't match that of a serialized superblock.
     */
    // TODO: see what to do with potential exceptions when reading block fields
    public Superblock(byte[] payload) throws ProtocolException {
//        if (payload.length > COMPACT_SERIALIZED_SIZE) {
//            throw new ProtocolException("Payload too long; does not represent a proper superblock.");
//        } else if (payload.length < COMPACT_SERIALIZED_SIZE) {
//            throw new ProtocolException("Payload too short; does not represent a proper superblock.");
//        }

        this.merkleRoot = Sha256Hash.wrapReversed(readBytes(payload, MERKLE_ROOT_PAYLOAD_OFFSET, HASH_BYTES_LENGTH));
        this.chainWork = new BigInteger(Utils.reverseBytes(readBytes(payload, CHAIN_WORK_PAYLOAD_OFFSET, BIG_INTEGER_LENGTH))); // read 256 bits
        this.lastDogeBlockHash = Sha256Hash.wrapReversed(readBytes(payload, LAST_BLOCK_HASH_PAYLOAD_OFFSET, HASH_BYTES_LENGTH));
        this.lastDogeBlockTime = Utils.readUint32(payload, LAST_BLOCK_TIME_PAYLOAD_OFFSET);
        this.prevSuperblockHash = Utils.reverseBytes(readBytes(payload, PREV_SUPERBLOCK_HASH_PAYLOAD_OFFSET, HASH_BYTES_LENGTH));

        long numberOfDogeBlockHashes = Utils.readUint32(payload, NUMBER_OF_HASHES_PAYLOAD_OFFSET);
        this.dogeBlockHashes = deserializeHashes(payload, DOGE_BLOCK_HASHES_PAYLOAD_OFFSET, numberOfDogeBlockHashes); // TODO: figure out why this is size 0 when deserialising block
    }

    /**
     * Calculate the Merkle root hash of a tree containing all the blocks in `blocks`.
     * @param hashes List of hashes belonging to all Dogecoin blocks mined within the one hour lapse corresponding to this superblock.
     * @return Root of a Merkle tree with all these blocks as its leaves.
     */
    public Sha256Hash calculateMerkleRoot(List<Sha256Hash> hashes) {
        List<byte[]> tree = buildMerkleTree(hashes); // TODO: discuss replacing with PartialMerkleTree.buildFromLeaves()
        return Sha256Hash.wrap(tree.get(tree.size() - 1));
    }

    /**
     * Build a Merkle tree with all the blocks in `blocks` as its leaves.
     * @param hashes List of hashes belonging to all Dogecoin blocks mined within the one hour lapse corresponding to this superblock.
     * @return Merkle tree in List<> format, with its lower levels first and its root as the last element.
     */
    // TODO: discuss replacing with PartialMerkleTree.buildFromLeaves()
    private List<byte[]> buildMerkleTree(List<Sha256Hash> hashes) {
        // adapted from bitcoinj's implementation of Merkle trees for transactions
        List<byte[]> tree = new ArrayList<>(); // check if this should be a List or an ArrayList
        // add all the block hashes in bytes[] format
        for (Sha256Hash h : hashes) {
            tree.add(h.getBytes());
        }

        int levelOffset = 0;
        // hashes the current level; levelSize = 1 means it's reached the root and there's nothing else to hash
        for (int levelSize = hashes.size(); levelSize > 1; levelSize = (levelSize + 1) / 2) {
            // hashes each pair of nodes
            for (int left = 0; left < levelSize; left += 2) {
                // The right hand node can be the same as the left hand, in the case where we don't have enough transactions.
                int right = Math.min(left + 1, levelSize - 1); // in case left needs to be hashed with itself
                byte[] leftBytes = tree.get(levelOffset + left);
                byte[] rightBytes = tree.get(levelOffset + right);
                tree.add(Sha256Hash.hashTwice(leftBytes, 0, 32, rightBytes, 0, 32));
            }
            // Move to the next level.
            levelOffset += levelSize;
        }

        return tree;
    }

    /**
     * Calculates Keccak-256 hash of superblock data.
     * @return Superblock ID hash in bytes format.
     * @throws IOException
     */
    private byte[] calculateHash() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serialize(outputStream);
        byte[] data = outputStream.toByteArray();
        return Hash.sha3(data);
    }


    /* ---- GETTERS ---- */

    public Sha256Hash getMerkleRoot() {
        return merkleRoot;
    }

    public BigInteger getChainWork() {
        return chainWork;
    }

    public Sha256Hash getLastDogeBlockHash() {
        return lastDogeBlockHash;
    }

    public long getLastDogeBlockTime() {
        return lastDogeBlockTime;
    }

    public byte[] getPrevSuperblockHash() {
        return prevSuperblockHash;
    }

    public byte[] getSuperblockHash() throws IOException {
        if (hash == null) {
            hash = calculateHash();
        }
        return hash;
    }


    /* ---- STORAGE AND READING ---- */

    /**
     * Serializes Merkle root, chain work, last block hash, last block time and previous superblock hash
     * (in that order) to an output stream.
     * This is the information that should be used for calculating the superblock hash,
     * sending the superblock to DogeRelay and defending it in the challenges.
     * @param stream Output stream where the information will be written. Modified by the function.
     * @throws IOException
     */
    public void serialize(OutputStream stream) throws IOException {
        stream.write(merkleRoot.getReversedBytes()); // 32
        stream.write(Utils.reverseBytes(toBytes32(chainWork))); // 32
        stream.write(lastDogeBlockHash.getReversedBytes()); // 32
        Utils.uint32ToByteStreamLE(lastDogeBlockTime, stream); // 4
        stream.write(Utils.reverseBytes(prevSuperblockHash)); // 32
    }

    /**
     * Serializes every superblock field into an output stream.
     * Order: Merkle root, chain work, last block hash, last block time, previous superblock hash,
     * last block height, superblock height.
     * The last two fields should *not* be sent to DogeRelay, but they are necessary
     * for rebuilding a superblock with auxiliary information from a serialized byte array.
     * Therefore, this method should only be used for storing superblocks.
     * @param stream Output stream where the information will be written. Modified by the function.
     * @throws IOException
     */
    public void serializeForStorage(OutputStream stream) throws IOException {
        serialize(stream);

        Utils.uint32ToByteStreamLE(dogeBlockHashes.size(), stream);
        serializeHashes(dogeBlockHashes, stream);
    }

    public void serializeHashes(List<Sha256Hash> hashes, OutputStream stream) throws IOException {
        for (int i = 0; i < hashes.size(); i++)
            stream.write(hashes.get(i).getReversedBytes());
    }

    public List<Sha256Hash> deserializeHashes(byte[] payload, int offset, long numberOfHashes) {
        List<Sha256Hash> hashes = new ArrayList<>();
        int cursor = offset;

        for (int i = 0; i < numberOfHashes; i++) {
            hashes.add(Sha256Hash.wrapReversed(readBytes(payload, cursor, HASH_BYTES_LENGTH)));
            cursor += HASH_BYTES_LENGTH;
        }

        return hashes;
    }


    /* ---- HELPERS & UTILITIES ----- */

    public byte[] toBytes32(BigInteger n) throws IOException {
        byte[] hex = n.toByteArray();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int i = hex.length; i < 32; i++) outputStream.write(0); // pad with 0s
        outputStream.write(hex);
        return outputStream.toByteArray();
    }

    public byte[] toBytes32(long n) throws IOException {
        byte[] hex = longToBytes(n);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int i = 8; i < 32; i++) outputStream.write(0); // pad with 0s
        outputStream.write(hex);
        return outputStream.toByteArray();
    }

    public byte[] toBytes32(int n) throws IOException {
        byte[] hex = intToBytes(n);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int i = 4; i < 32; i++) outputStream.write(0); // pad with 0s
        outputStream.write(hex);
        return outputStream.toByteArray();
    }

    public static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= 8;
        }
        return result;
    }

    public static byte[] intToBytes(int j) {
        byte[] result = new byte[4];
        for (int i = 3; i >= 0; i--) {
            result[i] = (byte)(j & 0xFF);
            j >>= 8;
        }
        return result;
    }

    protected byte[] readBytes(byte[] payload, int offset, int length) {
        byte[] b = new byte[length];
        System.arraycopy(payload, offset, b, 0, length);
        return b;
    }
}