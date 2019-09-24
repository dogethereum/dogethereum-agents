package org.sysethereum.agents.core.bridge;

import org.bitcoinj.core.Sha256Hash;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;

import java.math.BigInteger;
import java.util.List;


/**
 * Constructs a superblock from a sequence of block hashes.
 *
 * @author Catalina Juarros
 */
public class Superblock {

    private final Keccak256Hash superblockId;
    public final SuperblockData data;

    /**
     * Constructs a Superblock object from a list of Syscoin block hashes.
     */
    public Superblock(SuperblockData data, Keccak256Hash superblockId) {
        this.data = data;
        this.superblockId = superblockId;
    }

    /**
     * Accesses superblock hash attribute if already calculated, calculates it otherwise.
     * @return Superblock hash.
     */
    public Keccak256Hash getSuperblockId() {
        return superblockId;
    }

    /**
     * Accesses Merkle root attribute.
     * @return Superblock Merkle root.
     */
    public Sha256Hash getMerkleRoot() {
        return data.merkleRoot;
    }

    /**
     * Accesses chain work attribute.
     * @return Superblock Merkle root.
     */
    public BigInteger getChainWork() {
        return data.chainWork;
    }

    /**
     * Accesses last Syscoin block time attribute.
     * @return Superblock last Syscoin block time.
     */
    public long getLastSyscoinBlockTime() {
        return data.lastSyscoinBlockTime;
    }

    /**
     * Accesses last Syscoin block median time attribute.
     * @return Superblock last Syscoin block median time.
     */
    public long getLastSyscoinBlockMedianTime() {
        return data.lastSyscoinBlockTimeMTP;
    }

    /**
     * Accesses last Syscoin block hash attribute.
     * @return Superblock last Syscoin block hash.
     */
    public Sha256Hash getLastSyscoinBlockHash() {
        return data.lastSyscoinBlockHash;
    }

    /**
     * Accesses last block difficulty bits
     * @return Superblock last Syscoin block bits.
     */
    public long getlastSyscoinBlockBits() {
        return data.lastSyscoinBlockBits;
    }

    /**
     * Accesses parent hash attribute.
     * @return Superblock parent hash.
     */
    public Keccak256Hash getParentId() {
        return data.parentId;
    }

    /**
     * Accesses height attribute.
     * @return Superblock height within superblock chain.
     */
    public long getSuperblockHeight() {
        return data.superblockHeight;
    }

    /**
     * Accesses Syscoin block hashes attribute.
     * @return Superblock Syscoin block hashes.
     */
    public List<Sha256Hash> getSyscoinBlockHashes() {
        return data.syscoinBlockHashes;
    }

    /* ---- OTHER METHODS ---- */

    /**
     * Checks whether a given Syscoin block hash is part of the superblock.
     * @param hash Syscoin block hash to check.
     * @return True if the block is in the superblock, false otherwise.
     */
    public boolean hasSyscoinBlock(Sha256Hash hash) {
        return data.hasSyscoinBlock(hash);
    }

    /**
     * Returns index of a given Syscoin block hash in the superblock's list of hashes.
     * @param hash Syscoin block hash to find.
     * @return Position of hash within the list if it's part of the superblock, -1 otherwise.
     */
    public int getSyscoinBlockLeafIndex(Sha256Hash hash) {
        return data.syscoinBlockHashes.indexOf(hash);
    }

    @Override
    public String toString() {
        return "Superblock{" +
                "superblockId=" + superblockId +
                ", data=" + data +
                '}';
    }
}