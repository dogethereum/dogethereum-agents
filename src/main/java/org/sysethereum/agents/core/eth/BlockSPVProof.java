package org.sysethereum.agents.core.eth;

import java.util.List;

public class BlockSPVProof {
    public final int index;
    public final List<String> merklePath;
    public final String block;
    public final String blockhash;
    public final String tx;

    public BlockSPVProof(int indexIn, List<String> merklePathIn, String block, String blockhash, String tx) {
        this.index = indexIn;
        this.merklePath = merklePathIn;
        this.block = block;
        this.blockhash = blockhash;
        this.tx = tx;
    }
}
