/*
 * Copyright (C) 2017 RSK Labs Ltd.
 * Copyright (C) 2018 Coinfabrik and Oscar Guindzberg.
 * Copyright (C) 2019 Jagdeep Sidhu
 */
package org.sysethereum.agents.core.syscoin;


import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j(topic = "SyscoinWrapper")
public class SyscoinWrapper {

    private static final Logger logger = LoggerFactory.getLogger("SyscoinWrapper");

    private final Context syscoinContext;
    private final SyscoinWalletAppKit kit;

    @Autowired
    public SyscoinWrapper(
            Context syscoinContext,
            SyscoinWalletAppKit syscoinWalletAppKit
    ) {
        this.syscoinContext = syscoinContext;
        this.kit = syscoinWalletAppKit;
    }

    public void setupAndStart() {
        // TODO: Make the syscoin peer list configurable
        // if (!peerAddresses.isEmpty()) {
        //    kit.setPeerNodes(peerAddresses.toArray(new PeerAddress[]{}));
        //}
        kit.connectToLocalHost();

        InputStream checkpoints = SyscoinWrapper.class.getResourceAsStream("/" + syscoinContext.getParams().getId() + ".checkpoints");
        if (checkpoints != null) {
            kit.setCheckpoints(checkpoints);
        }

        logger.debug("About to start WalletAppKit");

        Context.propagate(syscoinContext);
        try {
            kit.startAsync().awaitRunning();
            logger.debug("WalletAppKit is running");
        } catch (IllegalStateException e) {
            logger.error("SyscoinWrapper failed to initialize");
        }
    }

    public void stop() {
        logger.info("stop: Starting...");

        Context.propagate(syscoinContext);

        logger.debug("stop: Request WAK to stop");
        try {
            kit.stopAsync().awaitTerminated(10, TimeUnit.SECONDS);
            logger.debug("stop: WAK stopped");
        } catch (TimeoutException e) {
            logger.debug("stop: WAK not stopped in 10 seconds, kill the thread instead");
            // Kill it
            for (Thread thread : Thread.getAllStackTraces().keySet()) {
                if (thread.getName().startsWith(SyscoinWalletAppKit.class.getSimpleName())) {
                    logger.info("stop: Interrupt thread:{}, isAlive:{}, isInterrupted:{}",
                            thread.getName(), thread.isAlive(), thread.isInterrupted());
                    thread.interrupt();
                }
            }
        }

        logger.info("stop: Finished");
    }

    public StoredBlock getChainHead() {
        return kit.chain().getChainHead();
    }

    /**
     * Gets the median timestamp of the last 11 blocks
     */
    public long getMedianTimestamp(StoredBlock storedBlock) throws BlockStoreException {
        Context.propagate(syscoinContext);
        long[] timestamps = new long[11];
        int unused = 9;
        timestamps[10] = storedBlock.getHeader().getTimeSeconds();
        while (unused >= 0 && (storedBlock = storedBlock.getPrev(kit.store())) != null)
            timestamps[unused--] = storedBlock.getHeader().getTimeSeconds();

        Arrays.sort(timestamps, unused + 1, 11);
        return timestamps[unused + (11 - unused) / 2];
    }

    public StoredBlock getBlock(Sha256Hash hash) throws BlockStoreException {
        return kit.store().get(hash);
    }

    @Nullable
    public StoredBlock getStoredBlockAtHeight(int height) throws BlockStoreException {
        Context.propagate(syscoinContext);
        BlockStore blockStore = kit.store();
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

    public Stack<Sha256Hash> getNewerHashesThan(Sha256Hash blockHash) throws BlockStoreException {
        Context.propagate(syscoinContext);
        var hashes = new Stack<Sha256Hash>();
        StoredBlock cur = getChainHead();

        while (cur != null && !cur.getHeader().getHash().equals(blockHash)) {
            hashes.push(cur.getHeader().getHash());
            cur = getBlock(cur.getHeader().getPrevBlockHash());
        }

        return hashes;
    }

}
