package org.dogethereum.dogesubmitter.core.dogecoin;

import org.bitcoinj.core.AltcoinBlock;
import org.bitcoinj.core.ProtocolException;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;

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

// TODO: check if data is big endian or little endian for documentation

public class Superblock extends org.bitcoinj.core.Message {

    /* ---- INFO FIELDS ---- */

    private Sha256Hash merkleRoot; // Root of a Merkle tree comprised of Dogecoin block hashes. 32 bytes.
    private BigInteger chainWork; // Total chain work put into this superblock -- same as total chain work put into last block. 32 bytes.
    private Sha256Hash lastBlockHash; // SHA-256 hash of last mined Dogecoin block in the superblock. 32 bytes.
    private long lastBlockTime; // Timestamp of last mined Dogecoin block in the superblock. 4 bytes.
    private byte[] prevSuperblockHash; // KECCAK-256 hash of previous superblock. 32 bytes.


    /* ---- EXTRA FIELDS ---- */

    public static final int COMPACT_SERIALIZED_SIZE = 140; // Size of all data in bytes: 32*4 + 4*3 = 128 + 12 = 140.

    private int height; // helper to keep chain updated
    private int lastBlockHeight; // Height of last mined Dogecoin block in the superblock within the Dogecoin blockchain

    private byte[] hash; // KECCAK-256 hash of superblock data
    private int offset = 0; // offset is inherited from Message. Since it's only going to be used for raw blocks which must be read from the beginning, it can be initialised to 0.


    /* ---- CONSTRUCTION METHODS ---- */

    /**
     * Construct a Superblock object from a list of Dogecoin blocks,
     * the previous superblock's hash and the accumulated chain work from its last block.
     * Callers should ensure that `work` is indeed the chain work corresponding
     * to the last element of `blocks`.
     * @param blocks List of all Dogecoin blocks mined within the last hour.
     * @param superblockHash Previous superblock's SHA-256 hash.
     * @param work Last Dogecoin block's accumulated chainwork.
     */
    public Superblock(List<AltcoinBlock> blocks, byte[] superblockHash, BigInteger work, int dogeHeight, int superHeight) {
        // hash all the block hashes into a Merkle tree
        merkleRoot = calculateMerkleRoot(blocks);
        chainWork = work;
        lastBlockHash = blocks.get(blocks.size() - 1).getHash();
        lastBlockTime = blocks.get(blocks.size() - 1).getTimeSeconds(); // maybe this should be a Date object, check later
        prevSuperblockHash = superblockHash;

        lastBlockHeight = dogeHeight;
        height = superHeight;
    }

    public Superblock(byte[] payload) {
        this.payload = payload;
        parseSuperblock();
    }

    /**
     * Calculate the Merkle root hash of a tree containing all the blocks in `blocks`.
     * @param blocks List of all Dogecoin blocks mined within the last hour.
     * @return Root of a Merkle tree with all these blocks as its leaves.
     */
    public Sha256Hash calculateMerkleRoot(List<AltcoinBlock> blocks) {

        List<byte[]> tree = buildMerkleTree(blocks);
        return Sha256Hash.wrap(tree.get(tree.size() - 1));
    }

    /**
     * Build a Merkle tree with all the blocks in `blocks` as its leaves.
     * @param blocks List of all Dogecoin blocks mined within the last hour.
     * @return Merkle tree in List<> format, with its lower levels first and its root as the last element.
     */
    private List<byte[]> buildMerkleTree(List<AltcoinBlock> blocks) {
        // adapted from bitcoinj's implementation of Merkle trees for transactions
        List<byte[]> tree = new ArrayList<>(); // check if this should be a List or an ArrayList
        // add all the block hashes in bytes[] format
        for (AltcoinBlock b : blocks) {
            tree.add(b.getHash().getBytes());
        }

        int levelOffset = 0;
        // hashes the current level; levelSize = 1 means it's reached the root and there's nothing else to hash
        for (int levelSize = blocks.size(); levelSize > 1; levelSize = (levelSize + 1) / 2) {
            // hashes each pair of nodes
            for (int left = 0; left < levelSize; left += 2) {
                int right = Math.min(left + 1, levelSize - 1); // in case left needs to be hashed with itself
                byte[] leftBytes = tree.get(levelOffset + left);
                byte[] rightBytes = tree.get(levelOffset + right);
                tree.add(Sha256Hash.hashTwice(leftBytes, 0, 32, rightBytes, 0, 32));
            }
            
            levelOffset += levelSize;
        }

        return tree;
    }

    /**
     * Calculates Keccak-256 hash of superblock data.
     * @return Superblock ID hash in bytes format
     */

    private byte[] calculateHash() throws java.io.IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        outputStream.write(merkleRoot.getBytes());
        outputStream.write(toBytes32(chainWork));
        outputStream.write(lastBlockHash.getBytes());
        outputStream.write(toBytes32(lastBlockTime));
        outputStream.write(prevSuperblockHash);

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

    public Sha256Hash getLastBlockHash() {
        return lastBlockHash;
    }

    public long getLastBlockTime() {
        return lastBlockTime;
    }

    public byte[] getPrevSuperblockHash() {
        return prevSuperblockHash;
    }

    public byte[] getSuperblockHash() throws java.io.IOException {
        if (hash == null) {
            hash = calculateHash();
        }
        return hash;
    }

    public int getHeight() {
        return height;
    }

    public int getLastBlockHeight() {
        return lastBlockHeight;
    }


    /* ---- STORAGE AND READING ---- */

    // TODO: this might need to be implemented like bitcoinSerialize

    public void serialize(OutputStream stream) throws java.io.IOException {
        stream.write(merkleRoot.getReversedBytes()); // 32
        stream.write(Utils.reverseBytes(toBytes32(chainWork))); // 32
        stream.write(lastBlockHash.getReversedBytes()); // 32
        Utils.uint32ToByteStreamLE(lastBlockTime, stream); // 4
        stream.write(Utils.reverseBytes(prevSuperblockHash)); // 32

        stream.write(lastBlockHeight); // 4
        stream.write(height); // 4
    }

    @Override
    protected void parse() {
        System.out.println("I seriously cannot believe this WON'T LET ME OVERRIDE A METHOD.");
    }

    private void parseSuperblock() throws ProtocolException {
        cursor = 0; // to make sure parse() is NEVER called with a different offset, neither accidentally nor maliciously

        merkleRoot = readHash();
        chainWork = new BigInteger(Utils.reverseBytes(readBytes(32))); // read 256 bits
        lastBlockHash = readHash();
        lastBlockTime = readUint32();
        prevSuperblockHash = Utils.reverseBytes(readBytes(32));

        lastBlockHeight = (int) readUint32(); // ask about this!
        height = (int) readUint32();
    }


    /* ---- HELPERS ----- */

    public byte[] toBytes32(BigInteger n) throws java.io.IOException {
        byte[] hex = n.toByteArray();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int i = hex.length; i < 32; i++) outputStream.write(0); // pad with 0s
        outputStream.write(hex);
        return outputStream.toByteArray();
    }

    public byte[] toBytes32(long n) throws java.io.IOException {
        byte[] hex = longToBytes(n);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int i = 8; i < 32; i++) outputStream.write(0); // pad with 0s
        outputStream.write(hex);
        return outputStream.toByteArray();
    }

    public byte[] toBytes32(int n) throws java.io.IOException {
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

}