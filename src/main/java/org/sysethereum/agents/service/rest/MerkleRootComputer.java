package org.sysethereum.agents.service.rest;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.PartialMerkleTree;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;
import org.libdohj.params.AbstractSyscoinParams;
import org.springframework.stereotype.Service;
import org.sysethereum.agents.constants.AgentConstants;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j(topic = "MerkleRootComputer")
public class MerkleRootComputer {

    private final AgentConstants agentConstants;

    public MerkleRootComputer(AgentConstants agentConstants) {
        this.agentConstants = agentConstants;
    }

    public Sha256Hash computeMerkleRoot(List<Sha256Hash> hashes) {
        return computeMerkleRoot(agentConstants.getSyscoinParams(), hashes);
    }

    public static Sha256Hash computeMerkleRoot(AbstractSyscoinParams params, List<Sha256Hash> hashes) {
        // hash all the block syscoinBlockHashes into a Merkle tree
        byte[] includeBits = new byte[(int) Math.ceil(hashes.size() / 8.0)];
        for (int i = 0; i < hashes.size(); i++)
            Utils.setBitLE(includeBits, i);

        var hashesCopy = new ArrayList<>(hashes);
        PartialMerkleTree tree = PartialMerkleTree.buildFromLeaves(params, includeBits, hashesCopy);
        return tree.getTxnHashAndMerkleRoot(hashesCopy);
    }

}
