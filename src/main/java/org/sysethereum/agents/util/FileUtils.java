package org.sysethereum.agents.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class FileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger("file");

    public static boolean recursiveDelete(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            //check if the file is a directory
            if (file.isDirectory()) {
                if ((file.list()).length > 0) {
                    for(String s:file.list()){
                        //call deletion of file individually
                        recursiveDelete(fileName + System.getProperty("file.separator") + s);
                    }
                }
            }

            if (!file.setWritable(true))
                LOGGER.error("File is not writable");

            boolean result = file.delete();
            return result;
        } else {
            return false;
        }
    }

}
