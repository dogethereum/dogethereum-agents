package org.dogethereum.agents.api;

import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import lombok.experimental.StandardException;
import org.dogethereum.agents.core.dogecoin.Keccak256Hash;
import org.dogethereum.agents.core.dogecoin.Superblock;
import org.dogethereum.agents.core.dogecoin.Superblockchain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class SuperblocksService {
    @Autowired
    Superblockchain superblockchain;

    @JsonRpcErrors({
            @JsonRpcError(
                    exception=SuperblockServiceException.class,
                    code=-1
            )
    })
    public Superblock getSuperblock(@JsonRpcParam(value="superblockId") final String id) throws SuperblockServiceException {
        if (!id.startsWith("0x") || id.length() != 66) {
            throw new SuperblockServiceException("Expected a hex encoded 32 byte superblock ID.");
        }

        Superblock superblock;
        try {
            superblock = superblockchain.getSuperblock(
                    Keccak256Hash.wrap(id.substring((2)))
            );
        } catch (IOException error) {
            throw new SuperblockServiceException("Failed obtaining the superblock.", error);
        }
        return superblock;
    }

    @StandardException
    static public class SuperblockServiceException extends Exception {}
}
