package org.dogethereum.dogesubmitter.core.dogecoin;

import org.bitcoinj.core.AltcoinBlock;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;
import org.libdohj.core.ScryptHash;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Constructs a superblock from a sequence of block hashes
 * Just a very rough prototype for now! This might not even compile
 */

// TODO: check if data is big endian or little endian for documentation

public class Superblock {
    private Sha256Hash merkleRoot;
    private BigInteger chainWork;
    private Sha256Hash lastBlockHash;
    private long lastBlockTime;
    private Sha256Hash prevSuperblockHash;
    
    /**
     * Construct a Superblock object from a list of Dogecoin blocks,
     * the previous superblock's hash and the accumulated chain work from its last block.
     * Callers should ensure that `work` is indeed the chain work corresponding
     * to the last element of `blocks`.
     * @param blocks List of all Dogecoin blocks mined within the last hour.
     * @param superblockHash Previous superblock's SHA-256 hash.
     * @param work Last Dogecoin block's accumulated chainwork.
     */
    public Superblock(List<AltcoinBlock> blocks, Sha256Hash superblockHash, BigInteger work) {
        // hash all the block hashes into a Merkle tree
        merkleRoot = calculateMerkleRoot(blocks);
        chainWork = work;
        lastBlockHash = blocks.get(blocks.size() - 1).getHash();
        lastBlockTime = blocks.get(blocks.size() - 1).getTimeSeconds(); // maybe this should be a Date object, check later
        prevSuperblockHash = superblockHash;
    }

    /**
     * Calculate the Merkle root hash of a tree containing all the blocks in `blocks`.
     * @param blocks List of all Dogecoin blocks mined within the last hour.
     * @return Root of a Merkle tree with all these blocks as its leaves.
     */
    private Sha256Hash calculateMerkleRoot(List<AltcoinBlock> blocks) {
        // important: look at buildMerkleTree() from bitcoinj to see how this is done for transactions
        // that code is probably not reusable but it serve as a guideline
        // there's something called hashTwice() which would be useful for calculating parent hashes
        // I still don't know how or if it's possible to concatenate two sha256 hashes
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
        // add all the block hashes
        for (AltcoinBlock b : blocks) {
            tree.add(b.getHash().getBytes());
        }

        int levelOffset = 0;
        // hashes the current level; levelSize = 1 means it's reached the root and there's nothing else to hash
        for (int levelSize = blocks.size(); levelSize > 1; levelSize = (levelSize + 1) / 2) {
            // hashes each pair of nodes
            for (int left = 0; left < levelSize; left += 2) {
                int right = Math.min(left + 1, levelSize - 1); // in case left needs to be hashed with itself
                byte[] leftBytes = Utils.reverseBytes(tree.get(levelOffset + left));
                byte[] rightBytes = Utils.reverseBytes(tree.get(levelOffset + right));
                tree.add(Utils.reverseBytes(Sha256Hash.hashTwice(leftBytes, 0, 32, rightBytes, 0, 32)));
            }
            
            levelOffset += levelSize;
        }

        return tree;
    }

    /* ---- GETTERS ---- */

    public Sha256Hash getMerkleRoot() {
        return merkleRoot;
    }

    public ScryptHash getchainWork() {
        // TODO: Note from Oscar to Cata: Returning null because method does not compile
        // why this method returns a ScryptHash? Use camel case
        //return chainWork;
        return null;
    }

    public Sha256Hash getLastBlockHash() {
        return lastBlockHash;
    }

    public long getLastBlockTime() {
        return lastBlockTime;
    }

    public Sha256Hash getPrevSuperblockHash() {
        return prevSuperblockHash;
    }
}