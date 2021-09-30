package org.dogethereum.agents.core;

import org.bitcoinj.store.BlockStoreException;
import org.dogethereum.agents.constants.SystemProperties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;

/**
 * Base class for agents managing file storage.
 * @author Catalina Juarros
 * @author Oscar Guindzberg
 */
public abstract class PersistentFileStore {
    protected SystemProperties config;

    protected File dataDirectory;

    protected long latestEthBlockProcessed;
    protected File latestEthBlockProcessedFile;

    @PostConstruct
    public void setup() throws Exception{
        this.config = SystemProperties.CONFIG;
        if (isEnabled()) {
            this.dataDirectory = new File(config.dataDirectory());
            // Set latestEthBlockProcessed to eth genesis block or eth checkpoint,
            // then read the latestEthBlockProcessed from file and overwrite it.
            this.latestEthBlockProcessed = config.getAgentConstants().getEthInitialCheckpoint();
            this.latestEthBlockProcessedFile = new File(dataDirectory.getAbsolutePath() + "/" + getLatestEthBlockProcessedFilename());
            restore(latestEthBlockProcessed, latestEthBlockProcessedFile);
        }
    }


    @PreDestroy
    public void tearDown() throws BlockStoreException, ClassNotFoundException, IOException {
        if (isEnabled()) {
            flush(latestEthBlockProcessed, latestEthBlockProcessedFile);
        }
    }

    protected abstract boolean isEnabled();

    protected abstract String getLatestEthBlockProcessedFilename();


    void restore(Serializable obj, File file) throws ClassNotFoundException, IOException {
        if (file.exists()) {
            synchronized (this) {
                try(
                    FileInputStream fileInputStream = new FileInputStream(file);
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                ) {
                    obj = obj.getClass().cast(objectInputStream.readObject());
                }
            }
        }
    }

    void flush(Serializable obj, File file) throws IOException {
        if (!dataDirectory.exists()) {
            if (!dataDirectory.mkdirs()) {
                throw new IOException("Could not create directory " + dataDirectory.getAbsolutePath());
            }
        }
        try (
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        ) {
            objectOutputStream.writeObject(obj);
        }
    }
}