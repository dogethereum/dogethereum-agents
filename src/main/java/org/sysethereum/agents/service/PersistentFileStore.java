package org.sysethereum.agents.service;

import org.springframework.stereotype.Service;

import java.io.*;

/**
 * Base methods for managing file storage.
 * @author Catalina Juarros
 */
@Service
public class PersistentFileStore {

    public PersistentFileStore() {
    }

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
        File directory = file.getParentFile();

        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("Could not create directory " + directory.getAbsolutePath());
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