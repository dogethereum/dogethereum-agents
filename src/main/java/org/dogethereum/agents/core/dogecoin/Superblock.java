package org.dogethereum.agents.core.dogecoin;

import org.bitcoinj.core.*;

import org.spongycastle.util.encoders.Hex;
import org.web3j.crypto.Hash;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Constructs a superblock from a sequence of block hashes.
 * Also provides methods for serialising and deserialising superblocks.
 * @author Catalina Juarros
 */

public class Superblock {

    /* ---- INFO FIELDS ---- */

    private Sha256Hash merkleRoot; // Root of a Merkle tree comprised of Dogecoin block hashes. 32 bytes.
    private BigInteger chainWork; // Total chain work put into this superblock -- same as total chain work put into last block. 32 bytes.
    private long lastDogeBlockTime; // Timestamp of last mined Dogecoin block in the superblock. 32 bytes to comply with Solidity version.
    private long previousToLastDogeBlockTime; // Timestamp of previous to last mined Dogecoin block in the superblock. 32 bytes to comply with Solidity version.
    private Sha256Hash lastDogeBlockHash; // SHA-256 hash of last mined Dogecoin block in the superblock. 32 bytes.
    private long lastDogeBlockBits;  // Bits (difficulty) of last mined Dogecoin block in the superblock. 32 bytes.
    private Keccak256Hash parentId; // SHA3-256 hash of previous superblock. 32 bytes.


    /* ---- EXTRA FIELDS ---- */

    private Keccak256Hash superblockId; // SHA3-256 hash of superblock data
    private long superblockHeight;
    private List<Sha256Hash> dogeBlockHashes;


    /* ---- CONSTANTS ---- */

    public static final int HASH_BYTES_LENGTH = 32;
    public static final int BIG_INTEGER_LENGTH = 32;
    public static final int UINT32_LENGTH = 4;

    private static final int MERKLE_ROOT_PAYLOAD_OFFSET = 0;
    private static final int CHAIN_WORK_PAYLOAD_OFFSET = MERKLE_ROOT_PAYLOAD_OFFSET + HASH_BYTES_LENGTH;
    private static final int LAST_BLOCK_TIME_PAYLOAD_OFFSET = CHAIN_WORK_PAYLOAD_OFFSET + BIG_INTEGER_LENGTH;
    private static final int PREVIOUS_TO_LAST_BLOCK_TIME_PAYLOAD_OFFSET =
            LAST_BLOCK_TIME_PAYLOAD_OFFSET + BIG_INTEGER_LENGTH;
    private static final int LAST_BLOCK_HASH_PAYLOAD_OFFSET =
            PREVIOUS_TO_LAST_BLOCK_TIME_PAYLOAD_OFFSET + BIG_INTEGER_LENGTH;
    private static final int LAST_BLOCK_BITS_PAYLOAD_OFFSET = LAST_BLOCK_HASH_PAYLOAD_OFFSET + HASH_BYTES_LENGTH;
    private static final int PARENT_ID_PAYLOAD_OFFSET = LAST_BLOCK_BITS_PAYLOAD_OFFSET + UINT32_LENGTH;

    private static final int SUPERBLOCK_HEIGHT_PAYLOAD_OFFSET = PARENT_ID_PAYLOAD_OFFSET + HASH_BYTES_LENGTH;
    private static final int NUMBER_OF_HASHES_PAYLOAD_OFFSET = SUPERBLOCK_HEIGHT_PAYLOAD_OFFSET + UINT32_LENGTH;
    private static final int DOGE_BLOCK_HASHES_PAYLOAD_OFFSET = NUMBER_OF_HASHES_PAYLOAD_OFFSET + UINT32_LENGTH;


    /* ---- CONSTRUCTION METHODS ---- */

    /**
     * Construct a Superblock object from a list of Dogecoin block hashes.
     * @param params
     * @param dogeBlockHashes List of hashes belonging to all Dogecoin blocks
     *                        mined within the one hour lapse corresponding to this superblock.
     * @param chainWork Last Dogecoin block's accumulated chainwork.
     * @param lastDogeBlockTime Last Dogecoin block's timestamp.
     * @param previousToLastDogeBlockTime Previous to last Dogecoin block's timestamp.
     * @param lastDogeBlockBits Last Dogecoin block's difficulty.
     * @param parentId Previous superblock's SHA-256 hash.
     * @param superblockHeight Height of this superblock within superblock chain.
     */
    public Superblock(NetworkParameters params, List<Sha256Hash> dogeBlockHashes, BigInteger chainWork,
                      long lastDogeBlockTime, long previousToLastDogeBlockTime, long lastDogeBlockBits,
                      Keccak256Hash parentId, long superblockHeight) {
        // hash all the block dogeBlockHashes into a Merkle tree
        byte[] includeBits = new byte[(int) Math.ceil(dogeBlockHashes.size() / 8.0)];
        for (int i = 0; i < dogeBlockHashes.size(); i++)
            Utils.setBitLE(includeBits, i);
        PartialMerkleTree dogeBlockHashesFullMerkleTree = PartialMerkleTree.buildFromLeaves(
                params, includeBits, dogeBlockHashes);

        this.merkleRoot = dogeBlockHashesFullMerkleTree.getTxnHashAndMerkleRoot(dogeBlockHashes);
        this.chainWork = chainWork;
        this.lastDogeBlockTime = lastDogeBlockTime;
        this.previousToLastDogeBlockTime = previousToLastDogeBlockTime;
        this.lastDogeBlockHash = dogeBlockHashes.get(dogeBlockHashes.size() - 1);
        this.lastDogeBlockBits = lastDogeBlockBits;
        this.parentId = parentId;

        // set helper fields
        this.superblockHeight = superblockHeight;
        this.dogeBlockHashes = new ArrayList<>(dogeBlockHashes);
    }

    /**
     * Construct a Superblock from an already calculated Merkle root.
     * @param merkleRoot Merkle root, already calculated from a list of Doge block hashes.
     * @param chainWork Last Dogecoin block's accumulated chainwork.
     * @param lastDogeBlockTime Last Dogecoin block's timestamp.
     * @param previousToLastDogeBlockTime Previous to last Dogecoin block's timestamp.
     * @param lastDogeBlockBits Last Dogecoin block's difficulty.
     * @param parentId Previous superblock's SHA-256 hash.
     * @param superblockHeight Height of this superblock within superblock chain.
     */
    public Superblock(Sha256Hash merkleRoot, BigInteger chainWork, long lastDogeBlockTime,
                      long previousToLastDogeBlockTime, Sha256Hash lastDogeBlockHash, long lastDogeBlockBits,
                      Keccak256Hash parentId, long superblockHeight) {
        this.merkleRoot = merkleRoot;
        this.chainWork = chainWork;
        this.lastDogeBlockTime = lastDogeBlockTime;
        this.previousToLastDogeBlockTime = previousToLastDogeBlockTime;
        this.lastDogeBlockHash = lastDogeBlockHash;
        this.lastDogeBlockBits = lastDogeBlockBits;
        this.parentId = parentId;

        // set helper fields
        this.superblockHeight = superblockHeight;
        this.dogeBlockHashes = new ArrayList<>();
    }

    /**
     * Construct a Superblock object from an array representing a serialized superblock.
     * @param payload Serialized superblock.
     * @throws ProtocolException
     */
    // TODO: see what to do with potential exceptions when reading block fields
    public Superblock(byte[] payload) throws ProtocolException {
        this.merkleRoot = Sha256Hash.wrapReversed(SuperblockUtils.readBytes(
                payload, MERKLE_ROOT_PAYLOAD_OFFSET, HASH_BYTES_LENGTH));
        this.chainWork = new BigInteger(Utils.reverseBytes(SuperblockUtils.readBytes(
                payload, CHAIN_WORK_PAYLOAD_OFFSET, BIG_INTEGER_LENGTH)));
        this.lastDogeBlockTime = Utils.readUint32(payload, LAST_BLOCK_TIME_PAYLOAD_OFFSET);
        this.previousToLastDogeBlockTime = Utils.readUint32(payload, PREVIOUS_TO_LAST_BLOCK_TIME_PAYLOAD_OFFSET);
        this.lastDogeBlockHash = Sha256Hash.wrapReversed(SuperblockUtils.readBytes(
                payload, LAST_BLOCK_HASH_PAYLOAD_OFFSET, HASH_BYTES_LENGTH));
        this.lastDogeBlockBits = Utils.readUint32(payload, LAST_BLOCK_BITS_PAYLOAD_OFFSET);
        this.parentId = Keccak256Hash.wrapReversed(
                SuperblockUtils.readBytes(payload, PARENT_ID_PAYLOAD_OFFSET, HASH_BYTES_LENGTH));

        // helper fields
        this.superblockHeight = Utils.readUint32(payload, SUPERBLOCK_HEIGHT_PAYLOAD_OFFSET);
        long numberOfDogeBlockHashes = Utils.readUint32(payload, NUMBER_OF_HASHES_PAYLOAD_OFFSET);
        this.dogeBlockHashes = deserializeHashesLE(payload, DOGE_BLOCK_HASHES_PAYLOAD_OFFSET, numberOfDogeBlockHashes);
    }

    // TODO: see if the following two methods should be deleted

    /**
     * Calculate the Merkle root hash of a tree containing all the blocks in `blocks`.
     * @param hashes List of hashes belonging to all Dogecoin blocks mined within the one hour lapse
     *               corresponding to this superblock.
     * @return Root of a Merkle tree with all these blocks as its leaves.
     */
    public Sha256Hash calculateMerkleRoot(List<Sha256Hash> hashes) {
        List<byte[]> tree = buildMerkleTree(hashes);
        return Sha256Hash.wrap(tree.get(tree.size() - 1));
    }

    /**
     * Build a Merkle tree with all the blocks in `blocks` as its leaves.
     * @param hashes List of hashes belonging to all Dogecoin blocks mined within the one hour lapse
     *               corresponding to this superblock.
     * @return Merkle tree in List<> format, with its lower levels first and its root as the last element.
     */
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
                // The right hand node can be the same as the left hand,
                // in the case where we don't have enough transactions.
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
     * @return Superblock ID in Keccak wrapper format.
     * @throws IOException
     */
    private Keccak256Hash calculateHash() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializeBE(outputStream);
        byte[] data = outputStream.toByteArray();
        return Keccak256Hash.of(data);
    }


    /* ---- GETTERS ---- */

    public Sha256Hash getMerkleRoot() {
        return merkleRoot;
    }

    public BigInteger getChainWork() {
        return chainWork;
    }

    public long getLastDogeBlockTime() {
        return lastDogeBlockTime;
    }

    public long getPreviousToLastDogeBlockTime() {
        return previousToLastDogeBlockTime;
    }

    public Sha256Hash getLastDogeBlockHash() {
        return lastDogeBlockHash;
    }

    public long getLastDogeBlockBits() {
        return lastDogeBlockBits;
    }


    public Keccak256Hash getParentId() {
        return parentId;
    }

    public Keccak256Hash getSuperblockId() throws IOException {
        if (superblockId == null)
            superblockId = calculateHash();
        return superblockId;
    }

    public long getSuperblockHeight() {
        return superblockHeight;
    }

    public List<Sha256Hash> getDogeBlockHashes() {
        return dogeBlockHashes;
    }


    /* ---- STORAGE AND READING ---- */

    /**
     * Serializes Merkle root, chain work, last block hash, last block time and previous superblock hash
     * (in that order) to an output stream in little-endian format.
     * This is the information that should be used for calculating the superblock hash,
     * sending the superblock to Dogethereum Contracts and defending it in the challenges.
     * @param stream Output stream where the information will be written. Modified by the function.
     * @throws IOException if a byte operation fails.
     */
    public void serializeLE(OutputStream stream) throws IOException {
        stream.write(merkleRoot.getReversedBytes()); // 32
        stream.write(Utils.reverseBytes(SuperblockUtils.toBytes32(chainWork))); // 32
        stream.write(Utils.reverseBytes(SuperblockUtils.toBytes32(lastDogeBlockTime))); // 32
        stream.write(Utils.reverseBytes(SuperblockUtils.toBytes32(previousToLastDogeBlockTime))); // 32
        stream.write(lastDogeBlockHash.getReversedBytes()); // 32
        stream.write(Utils.reverseBytes(SuperblockUtils.toUint32(lastDogeBlockBits))); // 4
        stream.write(parentId.getReversedBytes()); // 32
    }

    public void serializeBE(OutputStream stream) throws IOException {
        stream.write(merkleRoot.getBytes());
        stream.write(SuperblockUtils.toBytes32(chainWork));
        stream.write(SuperblockUtils.toBytes32(lastDogeBlockTime));
        stream.write(SuperblockUtils.toBytes32(previousToLastDogeBlockTime));
        stream.write(lastDogeBlockHash.getBytes());
        stream.write(SuperblockUtils.toUint32(lastDogeBlockBits));
        stream.write(parentId.getBytes());
    }

    /**
     * Serializes every superblock field into an output stream in little-endian format.
     * Order: Merkle root, chain work, last block hash, last block time, previous superblock hash,
     * last block height, superblock height.
     * The last few fields should *not* be sent to Dogethereum Contracts, but they are necessary
     * for rebuilding a superblock with auxiliary information from a serialized byte array.
     * Therefore, this method should only be used for storing superblocks.
     * @param stream Output stream where the information will be written. Modified by the function.
     * @throws IOException
     */
    public void serializeForStorage(OutputStream stream) throws IOException {
        serializeLE(stream);

        Utils.uint32ToByteStreamLE(superblockHeight, stream);
        Utils.uint32ToByteStreamLE(dogeBlockHashes.size(), stream);
        serializeHashesLE(dogeBlockHashes, stream);
    }

    /**
     * Serializes a list of hashes into an output stream in little-endian format.
     * This was designed as a helper method for serializeForStorage,
     * but it might be useful for other purposes in the future,
     * so it was made public.
     * @param hashes List of Dogecoin block hashes.
     * @param stream Output stream where the information will be written. Modified by the function.
     * @throws IOException
     */
    public void serializeHashesLE(List<Sha256Hash> hashes, OutputStream stream) throws IOException {
        for (int i = 0; i < hashes.size(); i++)
            stream.write(hashes.get(i).getReversedBytes());
    }

    /**
     * Given a little-endian byte array representing a superblock,
     * deserializes its Dogecoin block hashes and returns them in a list.
     * This was designed as a helper method for parsing a superblock,
     * but it might be useful for other purposes in the future,
     * so it was made public.
     * @param payload Serialized superblock.
     * @param offset Byte where the first hash starts.
     * @param numberOfHashes How many hashes to read.
     * @return List of hashes in the same order that they were stored in.
     */
    public List<Sha256Hash> deserializeHashesLE(byte[] payload, int offset, long numberOfHashes) {
        List<Sha256Hash> hashes = new ArrayList<>();
        int cursor = offset;

        for (int i = 0; i < numberOfHashes; i++) {
            hashes.add(Sha256Hash.wrapReversed(SuperblockUtils.readBytes(payload, cursor, HASH_BYTES_LENGTH)));
            cursor += HASH_BYTES_LENGTH;
        }

        return hashes;
    }


    /* ---- OTHER METHODS ---- */

    public boolean hasDogeBlock(Sha256Hash hash) {
        for (Sha256Hash h : dogeBlockHashes) {
            if (h.equals(hash))
                return true;
        }
        return false;
    }

    public int getDogeBlockLeafIndex(Sha256Hash hash) {
        for (int i = 0; i < dogeBlockHashes.size(); i++) {
            if (dogeBlockHashes.get(i).equals(hash))
                return i;
        }
        return -1;
    }

//    }

    @Override
    public String toString() {
        return "Superblock{" +
                "merkleRoot=" + merkleRoot +
                ", chainWork=" + chainWork +
                ", lastDogeBlockTime=" + lastDogeBlockTime +
                ", previousToLastDogeBlockTime=" + previousToLastDogeBlockTime +
                ", lastDogeBlockHash=" + lastDogeBlockHash +
                ", lastDogeBlockBits=" + lastDogeBlockBits +
                ", parentId=" + parentId +
                ", superblockId=" + superblockId +
                ", superblockHeight=" + superblockHeight +
                '}';
    }
}