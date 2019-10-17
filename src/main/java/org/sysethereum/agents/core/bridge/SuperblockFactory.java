package org.sysethereum.agents.core.bridge;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Sha256Hash;
import org.springframework.stereotype.Service;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;

import java.util.List;

@Service
@Slf4j(topic = "SuperblockFactory")
public class SuperblockFactory {

    private final SuperblockHashComputer hashComputer;
    private final SuperblockSerializationHelper serializationHelper;

    public SuperblockFactory(
            SuperblockHashComputer hashComputer,
            SuperblockSerializationHelper serializationHelper
    ) {
        this.hashComputer = hashComputer;
        this.serializationHelper = serializationHelper;
    }

    public Superblock make(
            Sha256Hash merkleRoot,
            List<Sha256Hash> syscoinBlockHashes,
            long lastSyscoinBlockTime,
            long lastSyscoinBlockTimeMTP,
            long lastSyscoinBlockBits,
            Keccak256Hash prevHash,
            long height
    ) {
        Sha256Hash lastSyscoinBlockHash = syscoinBlockHashes.get(syscoinBlockHashes.size() - 1);

        SuperblockData data = new SuperblockData(
                merkleRoot,
                syscoinBlockHashes,
                lastSyscoinBlockTime,
                lastSyscoinBlockTimeMTP,
                lastSyscoinBlockBits,
                lastSyscoinBlockHash,
                prevHash,
                height
        );

        return fromData(data);
    }

    /**
     * Constructs a Superblock object from an array representing a serialized superblock.
     *
     * @param payload Serialized superblock.
     */
    public Superblock fromBytes(byte[] payload) {
        SuperblockData data = serializationHelper.fromBytes(payload);
        return fromData(data);
    }

    public Superblock fromData(SuperblockData data) {
        Keccak256Hash superblockHash = hashComputer.calculateHash(data);
        return new Superblock(data, superblockHash);
    }

}
