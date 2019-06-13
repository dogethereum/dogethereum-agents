package org.sysethereum.agents.core;

import java.io.*;

/**
 * Base methods for managing file storage.
 * @author Catalina Juarros
 */
public abstract class PersistentFileStore {
    File dataDirectory;

    abstract void setupFiles() throws IOException;

    <T> T restore(Class<T> clazz, File file) throws IOException {
        if (file.exists()) {
            synchronized (this) {
                try(
                    FileInputStream fileInputStream = new FileInputStream(file);
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                ) {
                    try {
                        return clazz.cast(objectInputStream.readObject());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return null;
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