package org.dogethereum.dogesubmitter.core;


import lombok.EqualsAndHashCode;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PartialMerkleTree;
import org.bitcoinj.core.Sha256Hash;
import org.libdohj.params.AbstractDogecoinParams;
import org.libdohj.params.DogecoinMainNetParams;
import org.libdohj.params.DogecoinRegTestParams;
import org.libdohj.params.DogecoinTestNet3Params;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@EqualsAndHashCode
/**
 * Proof of inclusion of a tx in a Block
 */
public class Proof implements Serializable {


    private static final long serialVersionUID = 7608116934274420667L;

    // The hash of block where the tx is included.
    private Sha256Hash blockHash;
    // The tree that has the tx as a leaf and its root hash is in the block header
    private PartialMerkleTree partialMerkleTree;

    public Proof() {
    }


    public Proof(Sha256Hash blockHash, PartialMerkleTree partialMerkleTree) {
        this.blockHash = blockHash;
        this.partialMerkleTree = partialMerkleTree;
    }

    public Sha256Hash getBlockHash() {
        return blockHash;
    }

    public PartialMerkleTree getPartialMerkleTree() {
        return partialMerkleTree;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        //byte[] blockHashBytes = this.blockHash.getBytes();
        //out.writeLong(blockHashBytes.length);
        out.write(this.blockHash.getBytes());
        out.writeUTF(partialMerkleTree.getParams().getId());
        byte[] partialMerkleTreeBytes =  this.partialMerkleTree.bitcoinSerialize();
        out.writeInt(partialMerkleTreeBytes.length);
        out.write(partialMerkleTreeBytes);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        byte[] blockHashBytes = new byte[32];
        in.readFully(blockHashBytes);
        this.blockHash = Sha256Hash.wrap(blockHashBytes);
        NetworkParameters params = fromID(in.readUTF());
        byte[] partialMerkleTreeBytes = new byte[in.readInt()];
        in.readFully(partialMerkleTreeBytes);
        this.partialMerkleTree = new PartialMerkleTree(params, partialMerkleTreeBytes, 0);
    }

    /** Returns the network parameters for the given string ID or NULL if not recognized. */
    private AbstractDogecoinParams fromID(String id) {
        if (id.equals(AbstractDogecoinParams.ID_DOGE_MAINNET)) {
            return DogecoinMainNetParams.get();
        } else if (id.equals(AbstractDogecoinParams.ID_DOGE_TESTNET)) {
            return DogecoinTestNet3Params.get();
        } else if (id.equals(AbstractDogecoinParams.ID_DOGE_REGTEST)) {
            return DogecoinRegTestParams.get();
        } else {
            throw new IllegalArgumentException(id);
        }
    }

}
