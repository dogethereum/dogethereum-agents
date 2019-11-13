package org.sysethereum.agents.service.rest;

import org.bitcoinj.core.Sha256Hash;
import org.junit.jupiter.api.Test;
import org.libdohj.params.SyscoinRegTestParams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MerkleRootComputerTest {

    private static Sha256Hash h1 = Sha256Hash.wrap("ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb");
    private static Sha256Hash h2 = Sha256Hash.wrap("3e23e8160039594a33894f6564e1b1348bbd7a0088d42c4acb73eeaed59c009d");
    private static Sha256Hash h3 = Sha256Hash.wrap("2e7d2c03a9507ae265ecf5b5356885a53393a2029d241394997265a1a25aefc6");

    private static final String EXPECTED_MERKLE_ROOT = "a9b813617813531c879eb4a8888315d5710a74b34a74f543dd5da3242297cdc0";

    @Test
    void computeMerkleRoot_lib() {
        SyscoinRegTestParams params = SyscoinRegTestParams.get();
        var hashes = List.of(h1, h2, h3);
        Sha256Hash underTest = MerkleRootComputer.computeMerkleRoot(params, hashes);
        assertEquals(EXPECTED_MERKLE_ROOT, underTest.toString());
    }

    @Test
    void computeMerkleRoot_manual() {
        Sha256Hash L1 = Sha256Hash.wrapReversed(Sha256Hash.hashTwice(combine(h1.getReversedBytes(), h2.getReversedBytes())));
        Sha256Hash R1 = Sha256Hash.wrapReversed(Sha256Hash.hashTwice(combine(h3.getReversedBytes(), h3.getReversedBytes())));

        Sha256Hash M = Sha256Hash.wrapReversed(Sha256Hash.hashTwice(combine(L1.getReversedBytes(), R1.getReversedBytes())));
        assertEquals(EXPECTED_MERKLE_ROOT, M.toString());
    }

    private byte[] combine(byte[] a, byte[] b) {
        var os = new ByteArrayOutputStream();

        try {
            os.write(a);
            os.write(b);
        } catch (IOException e) {
            throw new RuntimeException("!");
        }

        return os.toByteArray();
    }
}