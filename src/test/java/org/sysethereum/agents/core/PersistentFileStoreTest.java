package org.sysethereum.agents.core;

import org.junit.Test;
import org.sysethereum.agents.core.PersistentFileStore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class PersistentFileStoreTest {

    @Test
    public void testLoadSaveLoad() throws IOException {
        File db = Files.createTempFile("store", ".db").toFile();
        db.deleteOnExit();
        PersistentFileStore store = new PersistentFileStore() {

            @Override
            void setupFiles() throws IOException {
                dataDirectory = Files.createTempDirectory("store").toFile();
            }
        };
        store.setupFiles();
        store.flush("some value", db);
        String restored = store.restore(String.class, db);
        assertEquals("some value", restored);
    }
}
