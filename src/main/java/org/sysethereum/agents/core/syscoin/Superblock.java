package org.sysethereum.agents.core.syscoin;

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

    // Root of a Merkle tree comprised of Syscoin block hashes. 32 bytes.
    private Sha256Hash merkleRoot;

    // Total chain work put into this superblock -- same as total chain work put into last block. 32 bytes.
    private BigInteger chainWork;

    // Timestamp of last mined Syscoin block in the superblock. 32 bytes to comply with Solidity version.
    private long lastSyscoinBlockTime;

    // Timestamp of of the block where the last difficulty adjustment occured. 32 bytes to comply with Solidity version.
    private long previousSyscoinBlockTime;

    // SHA-256 hash of last mined Syscoin block in the superblock. 32 bytes.
    private Sha256Hash lastSyscoinBlockHash;

    // Bits (difficulty) of last difficulty adjustment. 32 bytes.
    private long previousSyscoinBlockBits;

    // SHA3-256 hash of previous superblock. 32 bytes.
    private Keccak256Hash parentId;

    private long blockHeight;
    /* ---- EXTRA FIELDS ---- */

    private Keccak256Hash superblockId; // SHA3-256 hash of superblock data
    private long superblockHeight;
    private List<Sha256Hash> syscoinBlockHashes;


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
    private static final int SYSCOIN_BLOCK_HASHES_PAYLOAD_OFFSET = NUMBER_OF_HASHES_PAYLOAD_OFFSET + UINT32_LENGTH;


    /* ---- CONSTRUCTION METHODS ---- */

    /**
     * Constructs a Superblock object from a list of Syscoin block hashes.
     * @param params Syscoin network parameters
     * @param syscoinBlockHashes List of hashes belonging to all Syscoin blocks
     *                        mined within the one hour lapse corresponding to this superblock.
     * @param chainWork Last Syscoin block's accumulated chainwork.
     * @param lastSyscoinBlockTime Last Syscoin block's timestamp.
     * @param previousSyscoinBlockTime Syscoin block's timestamp of when last difficulty adjustment occured.
     * @param previousSyscoinBlockBits The previous difficulty bits
     * @param parentId Previous superblock's SHA-256 hash.
     * @param superblockHeight Height of this superblock within superblock chain.
     * @param blockHeight Height of the last block in the superblock.
     */
    public Superblock(NetworkParameters params, List<Sha256Hash> syscoinBlockHashes, BigInteger chainWork,
                      long lastSyscoinBlockTime, long previousSyscoinBlockTime, long previousSyscoinBlockBits,
                      Keccak256Hash parentId, long superblockHeight, long blockHeight) {
        // hash all the block syscoinBlockHashes into a Merkle tree
        byte[] includeBits = new byte[(int) Math.ceil(syscoinBlockHashes.size() / 8.0)];
        for (int i = 0; i < syscoinBlockHashes.size(); i++)
            Utils.setBitLE(includeBits, i);
        PartialMerkleTree syscoinBlockHashesFullMerkleTree = PartialMerkleTree.buildFromLeaves(
                params, includeBits, syscoinBlockHashes);

        this.merkleRoot = syscoinBlockHashesFullMerkleTree.getTxnHashAndMerkleRoot(syscoinBlockHashes);
        this.chainWork = chainWork;
        this.lastSyscoinBlockTime = lastSyscoinBlockTime;
        this.previousSyscoinBlockTime = previousSyscoinBlockTime;
        this.lastSyscoinBlockHash = syscoinBlockHashes.get(syscoinBlockHashes.size() - 1);
        this.previousSyscoinBlockBits = previousSyscoinBlockBits;
        this.parentId = parentId;
        this.blockHeight = blockHeight;
        // set helper fields
        this.superblockHeight = superblockHeight;
        this.syscoinBlockHashes = new ArrayList<>(syscoinBlockHashes);
    }

    /**
     * Constructs a Superblock object from an already calculated Merkle root.
     * @param merkleRoot Merkle root, already calculated from a list of Syscoin block hashes.
     * @param chainWork Last Syscoin block's accumulated chainwork.
     * @param lastSyscoinBlockTime Last Syscoin block's timestamp.
     * @param previousSyscoinBlockTime Block time when last difficulty adjustment occured.
     * @param previousSyscoinBlockBits Previous difficulty bits.
     * @param parentId Previous superblock's SHA-256 hash.
     * @param superblockHeight Height of this superblock within superblock chain.
     * @param blockHeight Height of the last block in the superblock.
     */
    public Superblock(Sha256Hash merkleRoot, BigInteger chainWork, long lastSyscoinBlockTime,
                      long previousSyscoinBlockTime, Sha256Hash lastSyscoinBlockHash, long previousSyscoinBlockBits,
                      Keccak256Hash parentId, long superblockHeight, long blockHeight) {
        this.merkleRoot = merkleRoot;
        this.chainWork = chainWork;
        this.lastSyscoinBlockTime = lastSyscoinBlockTime;
        this.previousSyscoinBlockTime = previousSyscoinBlockTime;
        this.lastSyscoinBlockHash = lastSyscoinBlockHash;
        this.previousSyscoinBlockBits = previousSyscoinBlockBits;
        this.parentId = parentId;
        this.blockHeight = blockHeight;

        // set helper fields
        this.superblockHeight = superblockHeight;
        this.syscoinBlockHashes = new ArrayList<>();
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
        this.lastSyscoinBlockTime = Utils.readUint32(payload, LAST_BLOCK_TIME_PAYLOAD_OFFSET);
        this.previousSyscoinBlockTime = Utils.readUint32(payload, PREVIOUS_TO_LAST_BLOCK_TIME_PAYLOAD_OFFSET);
        this.lastSyscoinBlockHash = Sha256Hash.wrapReversed(SuperblockUtils.readBytes(
                payload, LAST_BLOCK_HASH_PAYLOAD_OFFSET, HASH_BYTES_LENGTH));
        this.previousSyscoinBlockBits = Utils.readUint32(payload, LAST_BLOCK_BITS_PAYLOAD_OFFSET);
        this.parentId = Keccak256Hash.wrapReversed(
                SuperblockUtils.readBytes(payload, PARENT_ID_PAYLOAD_OFFSET, HASH_BYTES_LENGTH));
        this.blockHeight = Utils.readUint32(payload, BLOCK_HEIGHT_PAYLOAD_OFFSET);

        // helper fields
        this.superblockHeight = Utils.readUint32(payload, SUPERBLOCK_HEIGHT_PAYLOAD_OFFSET);
        long numberOfSyscoinBlockHashes = Utils.readUint32(payload, NUMBER_OF_HASHES_PAYLOAD_OFFSET);
        this.syscoinBlockHashes = deserializeHashesLE(payload, SYSCOIN_BLOCK_HASHES_PAYLOAD_OFFSET, numberOfSyscoinBlockHashes);
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
     * Accesses last Syscoin block time attribute.
     * @return Superblock last Syscoin block time.
     */
    public long getLastSyscoinBlockTime() {
        return lastSyscoinBlockTime;
    }

    /**
     * Accesses time when difficulty adjustment last occured
     * @return Superblock previous to last Syscoin block time.
     */
    public long getpreviousSyscoinBlockTime() {
        return previousSyscoinBlockTime;
    }

    /**
     * Accesses last Syscoin block hash attribute.
     * @return Superblock last Syscoin block hash.
     */
    public Sha256Hash getLastSyscoinBlockHash() {
        return lastSyscoinBlockHash;
    }

    /**
     * Accesses last previous difficulty bits (the previous adjustment).
     * @return Superblock last Syscoin block bits.
     */
    public long getpreviousSyscoinBlockBits() {
        return previousSyscoinBlockBits;
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
     * Accesses Syscoin block hashes attribute.
     * @return Superblock Syscoin block hashes.
     */
    public List<Sha256Hash> getSyscoinBlockHashes() {
        return syscoinBlockHashes;
    }


    /* ---- STORAGE AND READING ---- */

    /**
     * Serializes Merkle root, chain work, last block hash, last block time and previous superblock hash
     * (in that order) to an output stream in little-endian format.
     * This is the information that should be used for calculating the superblock hash,
     * sending the superblock to Sysethereum Contracts and defending it in the challenges.
     * @param stream Output stream where the information will be written. Modified by the function.
     * @throws IOException if a byte operation fails.
     */
    public void serializeLE(OutputStream stream) throws IOException {
        stream.write(merkleRoot.getReversedBytes()); // 32
        stream.write(Utils.reverseBytes(SuperblockUtils.toBytes32(chainWork))); // 32
        stream.write(Utils.reverseBytes(SuperblockUtils.toBytes32(lastSyscoinBlockTime))); // 32
        stream.write(Utils.reverseBytes(SuperblockUtils.toBytes32(previousSyscoinBlockTime))); // 32
        stream.write(lastSyscoinBlockHash.getReversedBytes()); // 32
        stream.write(Utils.reverseBytes(SuperblockUtils.toUint32(previousSyscoinBlockBits))); // 4
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
        stream.write(SuperblockUtils.toBytes32(lastSyscoinBlockTime));
        stream.write(SuperblockUtils.toBytes32(previousSyscoinBlockTime));
        stream.write(lastSyscoinBlockHash.getBytes());
        stream.write(SuperblockUtils.toUint32(previousSyscoinBlockBits));
        stream.write(parentId.getBytes());
        stream.write(SuperblockUtils.toUint32(blockHeight));
    }

    /**
     * Serializes every superblock field into an output stream in little-endian format.
     * Order: Merkle root, chain work, last block hash, last block time, previous superblock hash,
     * last block height, superblock height.
     * The last few fields should *not* be sent to Sysethereum Contracts, but they are necessary
     * for rebuilding a superblock with auxiliary information from a serialized byte array.
     * Therefore, this method should only be used for storing superblocks.
     * @param stream Output stream where the information will be written. Modified by the function.
     * @throws IOException
     */
    public void serializeForStorage(OutputStream stream) throws IOException {
        serializeLE(stream);

        Utils.uint32ToByteStreamLE(superblockHeight, stream);
        Utils.uint32ToByteStreamLE(syscoinBlockHashes.size(), stream);
        serializeHashesLE(syscoinBlockHashes, stream);
    }

    /**
     * Serializes a list of hashes into an output stream in little-endian format.
     * This was designed as a helper method for serializeForStorage,
     * but it might be useful for other purposes in the future,
     * so it was made public.
     * @param hashes List of Syscoin block hashes.
     * @param stream Output stream where the information will be written. Modified by the function.
     * @throws IOException
     */
    public void serializeHashesLE(List<Sha256Hash> hashes, OutputStream stream) throws IOException {
        for (int i = 0; i < hashes.size(); i++)
            stream.write(hashes.get(i).getReversedBytes());
    }

    /**
     * Given a little-endian byte array representing a superblock,
     * deserializes its Syscoin block hashes and returns them in a list.
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
     * Checks whether a given Syscoin block hash is part of the superblock.
     * @param hash Syscoin block hash to check.
     * @return True if the block is in the superblock, false otherwise.
     */
    public boolean hasSyscoinBlock(Sha256Hash hash) {
        for (Sha256Hash h : syscoinBlockHashes) {
            if (h.equals(hash))
                return true;
        }
        return false;
    }

    /**
     * Returns index of a given Syscoin block hash in the superblock's list of hashes.
     * @param hash Syscoin block hash to find.
     * @return Position of hash within the list if it's part of the superblock, -1 otherwise.
     */
    public int getSyscoinBlockLeafIndex(Sha256Hash hash) {
        return syscoinBlockHashes.indexOf(hash);
    }

    @Override
    public String toString() {
        return "Superblock{" +
                "merkleRoot=" + merkleRoot +
                ", chainWork=" + chainWork +
                ", lastSyscoinBlockTime=" + lastSyscoinBlockTime +
                ", lastSyscoinBlockHash=" + lastSyscoinBlockHash +
                ", previousSyscoinBlockTime=" + previousSyscoinBlockTime +
                ", previousSyscoinBlockBits=" + previousSyscoinBlockBits +
                ", parentId=" + parentId +
                ", superblockId=" + superblockId +
                ", superblockHeight=" + superblockHeight +
                ", blockHeight=" + blockHeight +
                '}';
    }
}