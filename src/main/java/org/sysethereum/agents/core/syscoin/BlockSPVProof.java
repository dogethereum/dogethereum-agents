package org.sysethereum.agents.core.syscoin;

import java.util.List;

public class BlockSPVProof {
    public final String transaction;
    public final String blockhash;
    public final String header;
    // this one can get filled when getting merkle proof, initially set with block txids and then converted into a merkle path via fillBlockSPVProof
    public List<String> siblings;
    public final int index;

    public BlockSPVProof(String transaction, String blockhash, String header, List<String> siblings, int index) {
        this.index = index;
        this.siblings = siblings;
        this.header = header;
        this.blockhash = blockhash;
        this.transaction = transaction;
    }
}
