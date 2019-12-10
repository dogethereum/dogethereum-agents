package org.sysethereum.agents.core.eth;

import java.util.List;

public class SuperblockSPVProof {
    public final int index;
    public final List<String> merklePath;
    public final String superBlock;

    public SuperblockSPVProof(int indexIn, List<String> merklePathIn, String superBlockIn) {
        this.index = indexIn;
        this.merklePath = merklePathIn;
        this.superBlock = superBlockIn;
    }
}
