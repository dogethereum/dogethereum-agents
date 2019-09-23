package org.sysethereum.agents.service.rest;

import com.google.common.collect.Lists;
import org.bitcoinj.core.Sha256Hash;
import org.junit.jupiter.api.Test;
import org.libdohj.params.SyscoinRegTestParams;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MerkleRootComputerTest {

    @Test
    void computeMerkleRoot() {
        SyscoinRegTestParams params = SyscoinRegTestParams.get();
        List<Sha256Hash> hashes = Lists.newArrayList(params.getGenesisBlock().getHash());

        Sha256Hash underTest = MerkleRootComputer.computeMerkleRoot(params, hashes);

        assertEquals("28a2c2d251f46fac05ade79085cbcb2ae4ec67ea24f1f1c7b40a348c00521194", underTest.toString());
    }
}