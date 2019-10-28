package org.sysethereum.agents.core;

import org.junit.jupiter.api.Test;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;
import org.sysethereum.agents.service.PersistentFileStore;

import java.io.*;
import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.*;

class PersistentFileStoreTest {

    @Test
    void serializationAndDeserialization() throws IOException, ClassNotFoundException {

        var semiApprovedSet = new HashSet<Keccak256Hash>();
        semiApprovedSet.add(Keccak256Hash.ZERO_HASH);

        File file = File.createTempFile("sysagents", ".dat");

        var underTest = new PersistentFileStore();

        underTest.flush(semiApprovedSet, file);

        var semiApprovedSet2 = new HashSet<Keccak256Hash>();
        semiApprovedSet2 = underTest.restore(semiApprovedSet2, file);

        assertEquals(semiApprovedSet.size(), semiApprovedSet2.size());
        assertTrue(semiApprovedSet2.contains(Keccak256Hash.ZERO_HASH));
    }

}