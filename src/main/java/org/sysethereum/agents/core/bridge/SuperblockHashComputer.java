package org.sysethereum.agents.core.bridge;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Slf4j(topic = "SuperblockHashComputer")
public class SuperblockHashComputer {

    private final SuperblockSerializationHelper helper;

    public SuperblockHashComputer(
            SuperblockSerializationHelper helper
    ) {
        this.helper = helper;
    }

    /**
     * Calculates Keccak-256 hash of superblock data.
     * @return Superblock ID in Keccak wrapper format.
     */
    public Keccak256Hash calculateHash(SuperblockData sbd) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            helper.serializeBE(sbd, outputStream);
            byte[] data = outputStream.toByteArray();
            return Keccak256Hash.of(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
