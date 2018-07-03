package org.dogethereum.dogesubmitter.core.dogecoin;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.store.BlockStoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Runs a SuperblockChain.
 * @author Catalina Juarros
 */

@Service
@Slf4j(topic = "SuperblockChainClient")

public class SuperblockChainClient {
    @Autowired
    private SuperblockChain superblockChain;

    @Autowired
    private DogecoinWrapper dogecoinWrapper;


    /**
     * Starts syncing.
     * @param updatePeriod How often the SuperblockChain object will poll dogecoinWrapper for new Doge blocks
     *                     and update its chain file accordingly.
     * @param executionDate
     * @throws BlockStoreException
     * @throws java.io.IOException
     */
    private void initialize(int updatePeriod, Date executionDate) throws BlockStoreException, java.io.IOException {
        new Timer("Update superblock chain").scheduleAtFixedRate(
                new UpdateSuperblocksTimerTask(), executionDate, updatePeriod);
    }

    /**
     * Builds and maintains a chain of superblocks from the whole Dogecoin blockchain.
     * Writes it to disk as specified by SuperblockLevelDBBlockStore.
     * @throws BlockStoreException
     * @throws IOException
     */
    public void updateChain() throws Exception, BlockStoreException, IOException {
        Superblock bestSuperblock = superblockChain.getChainHead();
        Sha256Hash bestSuperblockLastBlockHash = bestSuperblock.getLastDogeBlockHash();

        // get all the Dogecoin blocks that haven't yet been hashed into a superblock
        Stack<Sha256Hash> allDogeHashesToHash = getDogeBlockHashesNewerThan(bestSuperblockLastBlockHash);
        superblockChain.storeSuperblocks(allDogeHashesToHash, bestSuperblock.getSuperblockId()); // group them in superblocks
    }

    private Stack<Sha256Hash> getDogeBlockHashesNewerThan(Sha256Hash blockHash) throws BlockStoreException {
        Stack<Sha256Hash> hashes = new Stack<>();
        StoredBlock currentStoredBlock = dogecoinWrapper.getChainHead();

        while (!currentStoredBlock.getHeader().getHash().equals(blockHash)) {
            hashes.push(currentStoredBlock.getHeader().getHash());
            currentStoredBlock = dogecoinWrapper.getBlock(currentStoredBlock.getHeader().getPrevBlockHash());
        }

        return hashes;
    }

    /**
     * Task to keep superblock chain updated whenever the agent is running.
     */
    private class UpdateSuperblocksTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                log.debug("UpdateSuperblocksTimerTask");
                updateChain();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
