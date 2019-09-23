package org.sysethereum.agents.service.rest;

import org.bitcoinj.core.Sha256Hash;
import org.junit.jupiter.api.Test;
import org.libdohj.params.SyscoinRegTestParams;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MerkleRootComputerTest {

    @Test
    void computeMerkleRoot() {
        SyscoinRegTestParams params = SyscoinRegTestParams.get();

        var hashes = List.of(
            Sha256Hash.wrap("dd2a5016c7fe45349542664b08a5c6a52ed9e572ea2fb610ec0e1f86beb3ad68"),
            Sha256Hash.wrap("42530f9f92f2440d66b96e610d07b5256566fe47af2fd6e01cd9e1cd9b85c01e"),
            Sha256Hash.wrap("a07ccb5543fdc30329f26ed166b59b29255589d4f0badf2a5c297f7ecea8fc6f")
        );

        Sha256Hash underTest = MerkleRootComputer.computeMerkleRoot(params, hashes);

        assertEquals("d270262ac0df2125364f9defe9a5fb1731406150028385b2f6a961bc6c56a9a6", underTest.toString());
    }
}