package org.sysethereum.agents.core;

import java.io.*;

/**
 * Base methods for managing file storage.
 * @author Catalina Juarros
 */
public abstract class PersistentFileStore {
    public final File dataDirectory;


    public PersistentFileStore(String dataDirectory) {
        this(new File(dataDirectory));
    }

    public PersistentFileStore(File dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    abstract void setupFiles() throws IOException;

    public <T extends Serializable> T restore(T obj, File file) throws ClassNotFoundException, IOException {
        if (file.exists()) {
            synchronized (this) {
                try(
                    FileInputStream fileInputStream = new FileInputStream(file);
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)
                ) {
                    //noinspection unchecked
                    return (T)obj.getClass().cast(objectInputStream.readObject());
                }
            }
        }

        return obj;
    }

    public void flush(Serializable obj, File file) throws IOException {
        if (!dataDirectory.exists()) {
            if (!dataDirectory.mkdirs()) {
                throw new IOException("Could not create directory " + dataDirectory.getAbsolutePath());
            }
        }
        try (
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)
        ) {
            objectOutputStream.writeObject(obj);
        }
    }
}