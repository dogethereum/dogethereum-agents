package org.sysethereum.agents.util;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "AgentUtils")
public class AgentUtils {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger("AgentUtils");

    public StoredBlock getStoredBlockAtHeight(BlockStore blockStore, int height) throws BlockStoreException {
        StoredBlock storedBlock = blockStore.getChainHead();
        int headHeight = storedBlock.getHeight();
        if (height > headHeight) {
            return null;
        }
        for (int i = 0; i < (headHeight - height); i++) {
            if (storedBlock == null) {
                return null;
            }

            Sha256Hash prevBlockHash = storedBlock.getHeader().getPrevBlockHash();
            storedBlock = blockStore.get(prevBlockHash);
        }
        if (storedBlock != null) {
            if (storedBlock.getHeight() != height) {
                throw new IllegalStateException("Block height is " + storedBlock.getHeight() + " but should be " + headHeight);
            }
            return storedBlock;
        } else {
            return null;
        }
    }

}
