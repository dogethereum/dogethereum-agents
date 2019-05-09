/*
 * Copyright (C) 2017 RSK Labs Ltd.
 * Copyright (C) 2018 Coinfabrik and Oscar Guindzberg.
 * Copyright (C) 2019 Jagdeep Sidhu
 */
package org.sysethereum.agents.core.syscoin;


import lombok.EqualsAndHashCode;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PartialMerkleTree;
import org.bitcoinj.core.Sha256Hash;
import org.libdohj.params.AbstractSyscoinParams;
import org.libdohj.params.SyscoinMainNetParams;
import org.libdohj.params.SyscoinTestNet3Params;
import org.libdohj.params.SyscoinRegTestParams;


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

    // The hash of the block where the tx is included.
    private Sha256Hash blockHash;
    // The tree that has the tx as a leaf and whose root hash is in the Syscoin block header.
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
    private AbstractSyscoinParams fromID(String id) {
        if (id.equals(AbstractSyscoinParams.ID_SYSCOIN_MAINNET)) {
            return SyscoinMainNetParams.get();
        } else if (id.equals(AbstractSyscoinParams.ID_SYSCOIN_TESTNET)) {
            return SyscoinTestNet3Params.get();
        } else if (id.equals(AbstractSyscoinParams.ID_SYSCOIN_REGTEST)) {
            return SyscoinRegTestParams.get();
        } else {
            throw new IllegalArgumentException(id);
        }
    }

}
