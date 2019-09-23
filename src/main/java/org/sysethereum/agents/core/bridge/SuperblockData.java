package org.sysethereum.agents.core.bridge;

import org.bitcoinj.core.Sha256Hash;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class SuperblockData {

    // Root of a Merkle tree comprised of Syscoin block hashes. 32 bytes.
    public final Sha256Hash merkleRoot;

    // Total chain work put into this superblock -- same as total chain work put into last block. 32 bytes.
    public final BigInteger chainWork;

    // Timestamp of last mined Syscoin block in the superblock. 32 bytes to comply with Solidity version.
    public final long lastSyscoinBlockTime;

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
     * @param chainWork Last Syscoin block's accumulated ChainWork.
     * @param lastSyscoinBlockTime Last Syscoin block's timestamp.
     * @param lastSyscoinBlockBits Difficulty bits of the last block in the superblock bits used to verify accumulatedWork through difficulty calculation
     * @param parentId Previous superblock's SHA-256 hash.
     * @param superblockHeight Height of this superblock within superblock chain.
     */
    public SuperblockData(
            Sha256Hash merkleRoot,
            List<Sha256Hash> syscoinBlockHashes,
            BigInteger chainWork,
            long lastSyscoinBlockTime,
            long lastSyscoinBlockBits,
            Sha256Hash lastSyscoinBlockHash,
            Keccak256Hash parentId,
            long superblockHeight
    ) {
        this.merkleRoot = merkleRoot;
        this.chainWork = chainWork;
        this.lastSyscoinBlockTime = lastSyscoinBlockTime;
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
     * @param chainWork Last Syscoin block's accumulated ChainWork.
     * @param lastSyscoinBlockTime Last Syscoin block's timestamp.
     * @param lastSyscoinBlockBits Difficulty bits of the last block in the superblock bits used to verify accumulatedWork through difficulty calculation
     * @param parentId Previous superblock's SHA-256 hash.
     * @param superblockHeight Height of this superblock within superblock chain.
     */
    public SuperblockData(
            Sha256Hash merkleRoot,
            List<Sha256Hash> syscoinBlockHashes,
            BigInteger chainWork,
            long lastSyscoinBlockTime,
            long lastSyscoinBlockBits,
            Keccak256Hash parentId,
            long superblockHeight
    ) {
        this(merkleRoot,
                syscoinBlockHashes,
                chainWork,
                lastSyscoinBlockTime,
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
                ", chainWork=" + chainWork +
                ", lastSyscoinBlockTime=" + lastSyscoinBlockTime +
                ", lastSyscoinBlockHash=" + lastSyscoinBlockHash +
                ", lastSyscoinBlockBits=" + lastSyscoinBlockBits +
                ", parentId=" + parentId +
                ", superblockHeight=" + superblockHeight +
                '}';
    }
}
