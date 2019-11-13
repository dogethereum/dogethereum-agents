package org.sysethereum.agents.core.bridge;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;
import org.springframework.stereotype.Service;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;
import org.sysethereum.agents.core.syscoin.SuperblockUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j(topic = "SuperblockSerializationHelper")
public class SuperblockSerializationHelper {

    public static final int HASH_BYTES_LENGTH = 32;
    public static final int BIG_INTEGER_LENGTH = 32;
    public static final int UINT32_LENGTH = 4;

    // Offsets for deserializing a Superblock object
    private static final int MERKLE_ROOT_PAYLOAD_OFFSET = 0;
    private static final int LAST_BLOCK_TIME_PAYLOAD_OFFSET = MERKLE_ROOT_PAYLOAD_OFFSET + BIG_INTEGER_LENGTH;
    private static final int LAST_BLOCK_TIME_MTP_PAYLOAD_OFFSET = LAST_BLOCK_TIME_PAYLOAD_OFFSET + BIG_INTEGER_LENGTH;
    private static final int LAST_BLOCK_HASH_PAYLOAD_OFFSET =
            LAST_BLOCK_TIME_MTP_PAYLOAD_OFFSET + BIG_INTEGER_LENGTH;

    private static final int LAST_BLOCK_BITS_PAYLOAD_OFFSET = LAST_BLOCK_HASH_PAYLOAD_OFFSET + HASH_BYTES_LENGTH;


    private static final int PARENT_ID_PAYLOAD_OFFSET = LAST_BLOCK_BITS_PAYLOAD_OFFSET + UINT32_LENGTH;

    private static final int SUPERBLOCK_HEIGHT_PAYLOAD_OFFSET = PARENT_ID_PAYLOAD_OFFSET + HASH_BYTES_LENGTH;
    private static final int NUMBER_OF_HASHES_PAYLOAD_OFFSET = SUPERBLOCK_HEIGHT_PAYLOAD_OFFSET + UINT32_LENGTH;
    private static final int SYSCOIN_BLOCK_HASHES_PAYLOAD_OFFSET = NUMBER_OF_HASHES_PAYLOAD_OFFSET + UINT32_LENGTH;

    /**
     * Constructs a Superblock object from an array representing a serialized superblock.
     * @param payload Serialized superblock.
     */
    public SuperblockData fromBytes(byte[] payload) {
        Sha256Hash merkleRoot = Sha256Hash.wrapReversed(SuperblockUtils.readBytes(
                payload, MERKLE_ROOT_PAYLOAD_OFFSET, HASH_BYTES_LENGTH));
        long lastSyscoinBlockTime = new BigInteger(Utils.reverseBytes(SuperblockUtils.readBytes(
                payload, LAST_BLOCK_TIME_PAYLOAD_OFFSET, BIG_INTEGER_LENGTH))).longValue();
        long lastSyscoinBlockTimeMTP = new BigInteger(Utils.reverseBytes(SuperblockUtils.readBytes(
                payload, LAST_BLOCK_TIME_MTP_PAYLOAD_OFFSET, BIG_INTEGER_LENGTH))).longValue();
        Sha256Hash lastSyscoinBlockHash = Sha256Hash.wrapReversed(SuperblockUtils.readBytes(
                payload, LAST_BLOCK_HASH_PAYLOAD_OFFSET, HASH_BYTES_LENGTH));
        long lastSyscoinBlockBits = Utils.readUint32(payload, LAST_BLOCK_BITS_PAYLOAD_OFFSET);
        Keccak256Hash parentId = Keccak256Hash.wrapReversed(
                SuperblockUtils.readBytes(payload, PARENT_ID_PAYLOAD_OFFSET, HASH_BYTES_LENGTH));

        // helper fields
        long height = Utils.readUint32(payload, SUPERBLOCK_HEIGHT_PAYLOAD_OFFSET);
        long numberOfSyscoinBlockHashes = Utils.readUint32(payload, NUMBER_OF_HASHES_PAYLOAD_OFFSET);
        List<Sha256Hash> syscoinBlockHashes = deserializeHashesLE(payload, SYSCOIN_BLOCK_HASHES_PAYLOAD_OFFSET, numberOfSyscoinBlockHashes);

        return new SuperblockData(merkleRoot, syscoinBlockHashes, lastSyscoinBlockTime, lastSyscoinBlockTimeMTP, lastSyscoinBlockBits, lastSyscoinBlockHash, parentId, height);
    }

    /**
     * Serializes Merkle root, chain work, last block hash, last block time/mtp and previous superblock hash
     * (in that order) to an output stream in little-endian format.
     * This is the information that should be used for calculating the superblock hash,
     * sending the superblock to Sysethereum Contracts and defending it in the challenges.
     *
     * @param sbd
     * @param stream Output stream where the information will be written. Modified by the function.
     * @throws IOException if a byte operation fails.
     */
    public void serializeLE(SuperblockData sbd, OutputStream stream) throws IOException {
        stream.write(sbd.merkleRoot.getReversedBytes()); // 32
        stream.write(Utils.reverseBytes(SuperblockUtils.toBytes32(sbd.lastSyscoinBlockTime))); // 32
        stream.write(Utils.reverseBytes(SuperblockUtils.toBytes32(sbd.lastSyscoinBlockTimeMTP))); // 32
        stream.write(sbd.lastSyscoinBlockHash.getReversedBytes()); // 32
        stream.write(Utils.reverseBytes(SuperblockUtils.toUint32(sbd.lastSyscoinBlockBits))); // 4
        stream.write(sbd.parentId.getReversedBytes()); // 32
    }

    /**
     * Serializes Merkle root, chain work, last block hash, last block time and previous superblock hash
     * (in that order) to an output stream in big-endian format.
     * @param stream Output stream where the information will be written. Modified by the function.
     * @throws IOException if a byte operation fails.
     */
    public void serializeBE(SuperblockData sb, OutputStream stream) throws IOException {
        stream.write(sb.merkleRoot.getBytes());
        stream.write(SuperblockUtils.toBytes32(sb.lastSyscoinBlockTime));
        stream.write(SuperblockUtils.toBytes32(sb.lastSyscoinBlockTimeMTP));
        stream.write(sb.lastSyscoinBlockHash.getBytes());
        stream.write(SuperblockUtils.toUint32(sb.lastSyscoinBlockBits));
        stream.write(sb.parentId.getBytes());
    }

    public ByteArrayOutputStream serializeForStorage(SuperblockData sb) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        serializeForStorage(sb, stream);
        return stream;
    }

    /**
     * Serializes every superblock field into an output stream in little-endian format.
     * Order: Merkle root, chain work, last block hash, last block time/mtp, previous superblock hash,
     * last block height, superblock height.
     * The last few fields should *not* be sent to Sysethereum Contracts, but they are necessary
     * for rebuilding a superblock with auxiliary information from a serialized byte array.
     * Therefore, this method should only be used for storing superblocks.
     *
     * @param sbd
     * @param stream Output stream where the information will be written. Modified by the function.
     * @throws IOException
     */
    public void serializeForStorage(SuperblockData sbd, OutputStream stream) {
        try {
            serializeLE(sbd, stream);

            Utils.uint32ToByteStreamLE(sbd.superblockHeight, stream);
            Utils.uint32ToByteStreamLE(sbd.syscoinBlockHashes.size(), stream);
            serializeHashesLE(sbd.syscoinBlockHashes, stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
    private void serializeHashesLE(List<Sha256Hash> hashes, OutputStream stream) throws IOException {
        for (Sha256Hash hash : hashes) stream.write(hash.getReversedBytes());
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

}
