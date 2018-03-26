package org.dogethereum.dogesubmitter.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class OperatorKeyHandler {

    private static final Logger logger = LoggerFactory.getLogger("OperatorKeyHandler");

    private final String filePath;

    public OperatorKeyHandler(String filePath) {
        this.filePath = filePath;
    }

    public byte[] privateKey() {

        if (StringUtils.isNotBlank(this.filePath) && Paths.get(this.filePath).toFile().exists()) {
            try (FileReader fr = new FileReader(this.filePath); BufferedReader br = new BufferedReader(fr)) {
                return Hex.decode(StringUtils.trim(br.readLine()).getBytes(StandardCharsets.UTF_8));
            } catch (Exception ex) {
                logger.error("Error while reading getting operator secret");
                throw new RuntimeException("Error while reading getting operator secret");
            }
        } else {
            throw new RuntimeException("Error accessing operator key.");
        }
    }
}
