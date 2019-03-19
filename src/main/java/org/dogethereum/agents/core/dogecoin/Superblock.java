package org.dogethereum.agents.core.dogecoin;

import org.bitcoinj.core.*;


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

public class Superblock {

    /* ---- INFO FIELDS ---- */

    // Root of a Merkle tree comprised of Dogecoin block hashes. 32 bytes.
    private Sha256Hash merkleRoot;

    // Total chain work put into this superblock -- same as total chain work put into last block. 32 bytes.
    private BigInteger chainWork;

    // Timestamp of last mined Dogecoin block in the superblock. 32 bytes to comply with Solidity version.
    private long lastDogeBlockTime;

    // Timestamp of previous to last mined Dogecoin block in the superblock. 32 bytes to comply with Solidity version.
    private long previousToLastDogeBlockTime;

    // SHA-256 hash of last mined Dogecoin block in the superblock. 32 bytes.
    private Sha256Hash lastDogeBlockHash;

    // Bits (difficulty) of last mined Dogecoin block in the superblock. 32 bytes.
    private long lastDogeBlockBits;

    // SHA3-256 hash of previous superblock. 32 bytes.
    private Keccak256Hash parentId;

    private long blockHeight;
    /* ---- EXTRA FIELDS ---- */

    private Keccak256Hash superblockId; // SHA3-256 hash of superblock data
    private long superblockHeight;
    private List<Sha256Hash> dogeBlockHashes;


    /* ---- CONSTANTS ---- */

    public static final int HASH_BYTES_LENGTH = 32;
    public static final int BIG_INTEGER_LENGTH = 32;
    public static final int UINT32_LENGTH = 4;

    // Offsets for deserialising a Superblock object
    private static final int MERKLE_ROOT_PAYLOAD_OFFSET = 0;
    private static final int CHAIN_WORK_PAYLOAD_OFFSET = MERKLE_ROOT_PAYLOAD_OFFSET + HASH_BYTES_LENGTH;
    private static final int LAST_BLOCK_TIME_PAYLOAD_OFFSET = CHAIN_WORK_PAYLOAD_OFFSET + BIG_INTEGER_LENGTH;
    private static final int PREVIOUS_TO_LAST_BLOCK_TIME_PAYLOAD_OFFSET =
            LAST_BLOCK_TIME_PAYLOAD_OFFSET + BIG_INTEGER_LENGTH;
    private static final int LAST_BLOCK_HASH_PAYLOAD_OFFSET =
            PREVIOUS_TO_LAST_BLOCK_TIME_PAYLOAD_OFFSET + BIG_INTEGER_LENGTH;
    private static final int LAST_BLOCK_BITS_PAYLOAD_OFFSET = LAST_BLOCK_HASH_PAYLOAD_OFFSET + HASH_BYTES_LENGTH;
    private static final int PARENT_ID_PAYLOAD_OFFSET = LAST_BLOCK_BITS_PAYLOAD_OFFSET + UINT32_LENGTH;
    private static final int BLOCK_HEIGHT_PAYLOAD_OFFSET = PARENT_ID_PAYLOAD_OFFSET + HASH_BYTES_LENGTH;

    private static final int SUPERBLOCK_HEIGHT_PAYLOAD_OFFSET = BLOCK_HEIGHT_PAYLOAD_OFFSET + UINT32_LENGTH;
    private static final int NUMBER_OF_HASHES_PAYLOAD_OFFSET = SUPERBLOCK_HEIGHT_PAYLOAD_OFFSET + UINT32_LENGTH;
    private static final int DOGE_BLOCK_HASHES_PAYLOAD_OFFSET = NUMBER_OF_HASHES_PAYLOAD_OFFSET + UINT32_LENGTH;


    /* ---- CONSTRUCTION METHODS ---- */

    /**
     * Constructs a Superblock object from a list of Dogecoin block hashes.
     * @param params Doge network parameters
     * @param dogeBlockHashes List of hashes belonging to all Dogecoin blocks
     *                        mined within the one hour lapse corresponding to this superblock.
     * @param chainWork Last Dogecoin block's accumulated chainwork.
     * @param lastDogeBlockTime Last Dogecoin block's timestamp.
     * @param previousToLastDogeBlockTime Previous to last Dogecoin block's timestamp.
     * @param lastDogeBlockBits Last Dogecoin block's difficulty.
     * @param parentId Previous superblock's SHA-256 hash.
     * @param superblockHeight Height of this superblock within superblock chain.
     * @param blockHeight Height of the last block in the superblock.
     */
    public Superblock(NetworkParameters params, List<Sha256Hash> dogeBlockHashes, BigInteger chainWork,
                      long lastDogeBlockTime, long previousToLastDogeBlockTime, long lastDogeBlockBits,
                      Keccak256Hash parentId, long superblockHeight, long blockHeight) {
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
        this.blockHeight = blockHeight;
        // set helper fields
        this.superblockHeight = superblockHeight;
        this.dogeBlockHashes = new ArrayList<>(dogeBlockHashes);
    }

    /**
     * Constructs a Superblock object from an already calculated Merkle root.
     * @param merkleRoot Merkle root, already calculated from a list of Doge block hashes.
     * @param chainWork Last Dogecoin block's accumulated chainwork.
     * @param lastDogeBlockTime Last Dogecoin block's timestamp.
     * @param previousToLastDogeBlockTime Previous to last Dogecoin block's timestamp.
     * @param lastDogeBlockBits Last Dogecoin block's difficulty.
     * @param parentId Previous superblock's SHA-256 hash.
     * @param superblockHeight Height of this superblock within superblock chain.
     * @param blockHeight Height of the last block in the superblock.
     */
    public Superblock(Sha256Hash merkleRoot, BigInteger chainWork, long lastDogeBlockTime,
                      long previousToLastDogeBlockTime, Sha256Hash lastDogeBlockHash, long lastDogeBlockBits,
                      Keccak256Hash parentId, long superblockHeight, long blockHeight) {
        this.merkleRoot = merkleRoot;
        this.chainWork = chainWork;
        this.lastDogeBlockTime = lastDogeBlockTime;
        this.previousToLastDogeBlockTime = previousToLastDogeBlockTime;
        this.lastDogeBlockHash = lastDogeBlockHash;
        this.lastDogeBlockBits = lastDogeBlockBits;
        this.parentId = parentId;
        this.blockHeight = blockHeight;

        // set helper fields
        this.superblockHeight = superblockHeight;
        this.dogeBlockHashes = new ArrayList<>();
    }

    /**
     * Constructs a Superblock object from an array representing a serialized superblock.
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
        this.blockHeight = Utils.readUint32(payload, BLOCK_HEIGHT_PAYLOAD_OFFSET);

        // helper fields
        this.superblockHeight = Utils.readUint32(payload, SUPERBLOCK_HEIGHT_PAYLOAD_OFFSET);
        long numberOfDogeBlockHashes = Utils.readUint32(payload, NUMBER_OF_HASHES_PAYLOAD_OFFSET);
        this.dogeBlockHashes = deserializeHashesLE(payload, DOGE_BLOCK_HASHES_PAYLOAD_OFFSET, numberOfDogeBlockHashes);
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

    /**
     * Accesses Merkle root attribute.
     * @return Superblock Merkle root.
     */
    public Sha256Hash getMerkleRoot() {
        return merkleRoot;
    }

    /**
     * Accesses chain work attribute.
     * @return Superblock Merkle root.
     */
    public BigInteger getChainWork() {
        return chainWork;
    }

    /**
     * Accesses last Doge block time attribute.
     * @return Superblock last Doge block time.
     */
    public long getLastDogeBlockTime() {
        return lastDogeBlockTime;
    }

    /**
     * Accesses previous to last Doge block time attribute.
     * @return Superblock previous to last Doge block time.
     */
    public long getPreviousToLastDogeBlockTime() {
        return previousToLastDogeBlockTime;
    }

    /**
     * Accesses last Doge block hash attribute.
     * @return Superblock last Doge block hash.
     */
    public Sha256Hash getLastDogeBlockHash() {
        return lastDogeBlockHash;
    }

    /**
     * Accesses last Doge block bits attribute.
     * @return Superblock last Doge block bits.
     */
    public long getLastDogeBlockBits() {
        return lastDogeBlockBits;
    }

    /**
     * Accesses parent hash attribute.
     * @return Superblock parent hash.
     */
    public Keccak256Hash getParentId() {
        return parentId;
    }

    /**
     * Accesses last block height.
     * @return Superblock block height.
     */
    public long getBlockHeight() {
        return blockHeight;
    }

    /**
     * Accesses superblock hash attribute if already calculated, calculates it otherwise.
     * @return Superblock hash.
     */
    public Keccak256Hash getSuperblockId() throws IOException {
        if (superblockId == null)
            superblockId = calculateHash();
        return superblockId;
    }

    /**
     * Accesses height attribute.
     * @return Superblock height within superblock chain.
     */
    public long getSuperblockHeight() {
        return superblockHeight;
    }

    /**
     * Accesses Doge block hashes attribute.
     * @return Superblock Doge block hashes.
     */
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
        stream.write(Utils.reverseBytes(SuperblockUtils.toUint32(blockHeight))); // 4
    }

    /**
     * Serializes Merkle root, chain work, last block hash, last block time and previous superblock hash
     * (in that order) to an output stream in big-endian format.
     * @param stream Output stream where the information will be written. Modified by the function.
     * @throws IOException if a byte operation fails.
     */
    public void serializeBE(OutputStream stream) throws IOException {
        stream.write(merkleRoot.getBytes());
        stream.write(SuperblockUtils.toBytes32(chainWork));
        stream.write(SuperblockUtils.toBytes32(lastDogeBlockTime));
        stream.write(SuperblockUtils.toBytes32(previousToLastDogeBlockTime));
        stream.write(lastDogeBlockHash.getBytes());
        stream.write(SuperblockUtils.toUint32(lastDogeBlockBits));
        stream.write(parentId.getBytes());
        stream.write(SuperblockUtils.toUint32(blockHeight));
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

    /**
     * Checks whether a given Doge block hash is part of the superblock.
     * @param hash Doge block hash to check.
     * @return True if the block is in the superblock, false otherwise.
     */
    public boolean hasDogeBlock(Sha256Hash hash) {
        for (Sha256Hash h : dogeBlockHashes) {
            if (h.equals(hash))
                return true;
        }
        return false;
    }

    /**
     * Returns index of a given Doge block hash in the superblock's list of hashes.
     * @param hash Doge block hash to find.
     * @return Position of hash within the list if it's part of the superblock, -1 otherwise.
     */
    public int getDogeBlockLeafIndex(Sha256Hash hash) {
        return dogeBlockHashes.indexOf(hash);
    }

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
                ", blockHeight=" + blockHeight +
                '}';
    }
}