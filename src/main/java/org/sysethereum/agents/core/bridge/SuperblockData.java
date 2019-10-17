package org.sysethereum.agents.core.bridge;

import org.bitcoinj.core.Sha256Hash;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;

import java.util.ArrayList;
import java.util.List;

public class SuperblockData {

    // Root of a Merkle tree comprised of Syscoin block hashes. 32 bytes.
    public final Sha256Hash merkleRoot;

    // Timestamp of last mined Syscoin block in the superblock. 32 bytes to comply with Solidity version.
    public final long lastSyscoinBlockTime;

    // Median timestamp of last mined Syscoin block in the superblock. 32 bytes to comply with Solidity version.
    public final long lastSyscoinBlockTimeMTP;

    // SHA-256 hash of last mined Syscoin block in the superblock. 32 bytes.
    public final Sha256Hash lastSyscoinBlockHash;

    // Bits (difficulty) of last difficulty adjustment. 32 bytes.
    public final long lastSyscoinBlockBits;

    // SHA3-256 hash of previous superblock. 32 bytes.
    public final Keccak256Hash parentId;

    /* ---- EXTRA FIELDS ---- */
    public final long superblockHeight;
    public final List<Sha256Hash> syscoinBlockHashes;

    /**
     * Constructs a Superblock object from a list of Syscoin block hashes.
     * @param merkleRoot MerkleRoot computed from syscoinBlockHashes parameter
     * @param syscoinBlockHashes List of hashes belonging to all Syscoin blocks
     *                        mined within the one hour lapse corresponding to this superblock.
     * @param lastSyscoinBlockTime Last Syscoin block's timestamp.
     * @param lastSyscoinBlockTimeMTP Last Syscoin block's median timestamp.
     * @param lastSyscoinBlockBits Difficulty bits of the last block in the superblock bits.
     * @param parentId Previous superblock's SHA-256 hash.
     * @param superblockHeight Height of this superblock within superblock chain.
     */
    public SuperblockData(
            Sha256Hash merkleRoot,
            List<Sha256Hash> syscoinBlockHashes,
            long lastSyscoinBlockTime,
            long lastSyscoinBlockTimeMTP,
            long lastSyscoinBlockBits,
            Sha256Hash lastSyscoinBlockHash,
            Keccak256Hash parentId,
            long superblockHeight
    ) {
        this.merkleRoot = merkleRoot;
        this.lastSyscoinBlockTime = lastSyscoinBlockTime;
        this.lastSyscoinBlockTimeMTP = lastSyscoinBlockTimeMTP;
        this.lastSyscoinBlockBits = lastSyscoinBlockBits;
        this.lastSyscoinBlockHash = lastSyscoinBlockHash;
        this.parentId = parentId;
        this.superblockHeight = superblockHeight;
        this.syscoinBlockHashes = new ArrayList<>(syscoinBlockHashes);
    }

    /**
     * Constructs a Superblock object from a list of Syscoin block hashes.
     * @param merkleRoot MerkleRoot computed from syscoinBlockHashes parameter
     * @param syscoinBlockHashes List of hashes belonging to all Syscoin blocks
     *                        mined within the one hour lapse corresponding to this superblock.
     * @param lastSyscoinBlockTime Last Syscoin block's timestamp.
     * @param lastSyscoinBlockTimeMTP Last Syscoin block's median timestamp.
     * @param lastSyscoinBlockBits Difficulty bits of the last block in the superblock bits.
     * @param parentId Previous superblock's SHA-256 hash.
     * @param superblockHeight Height of this superblock within superblock chain.
     */
    public SuperblockData(
            Sha256Hash merkleRoot,
            List<Sha256Hash> syscoinBlockHashes,
            long lastSyscoinBlockTime,
            long lastSyscoinBlockTimeMTP,
            long lastSyscoinBlockBits,
            Keccak256Hash parentId,
            long superblockHeight
    ) {
        this(merkleRoot,
                syscoinBlockHashes,
                lastSyscoinBlockTime,
                lastSyscoinBlockTimeMTP,
                lastSyscoinBlockBits,
                syscoinBlockHashes.get(syscoinBlockHashes.size() -1),
                parentId,
                superblockHeight
        );
    }

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

    @Override
    public String toString() {
        return "SuperblockData{" +
                "merkleRoot=" + merkleRoot +
                ", lastSyscoinBlockTime=" + lastSyscoinBlockTime +
                ", lastSyscoinBlockTimeMTP=" + lastSyscoinBlockTimeMTP +
                ", lastSyscoinBlockHash=" + lastSyscoinBlockHash +
                ", lastSyscoinBlockBits=" + lastSyscoinBlockBits +
                ", parentId=" + parentId +
                ", superblockHeight=" + superblockHeight +
                '}';
    }
}
