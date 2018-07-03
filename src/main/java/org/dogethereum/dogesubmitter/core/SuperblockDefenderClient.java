package org.dogethereum.dogesubmitter.core;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.dogethereum.dogesubmitter.constants.AgentConstants;
import org.dogethereum.dogesubmitter.constants.SystemProperties;
import org.dogethereum.dogesubmitter.contract.DogeSuperblocks;
import org.dogethereum.dogesubmitter.core.dogecoin.*;
import org.dogethereum.dogesubmitter.core.eth.EthWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Monitors the Ethereum blockchain for superblock-related events
 * and defends/confirms the ones submitted by the agent.
 * @author Catalina Juarros
 */

@Service
@Slf4j(topic = "SuperblockDefenderClient")
public class SuperblockDefenderClient {
    @Autowired
    private DogecoinWrapper dogecoinWrapper;
    private EthWrapper ethWrapper;
    @Autowired
    private SuperblockChain superblockChain;

    private SystemProperties config;

    private static long ETH_REQUIRED_CONFIRMATIONS = 5;

    private long latestEthBlockProcessed;
    private File dataDirectory;
    private File latestEthBlockProcessedFile;

    private PeerGroup peerGroup;

    public SuperblockDefenderClient() {}

    public void setup() throws Exception {
        this.config = SystemProperties.CONFIG;
        if (config.isDogeBlockSubmitterEnabled()) {
            this.latestEthBlockProcessed = config.getAgentConstants().getEthInitialCheckpoint();
            this.dataDirectory = new File(config.dataDirectory());
            this.latestEthBlockProcessedFile = new File(dataDirectory.getAbsolutePath() +
                    "/SuperblockDefenderLatestEthBlockProcessedFile.dat");
            restoreLatestEthBlockProcessed();

            AgentConstants agentConstants = config.getAgentConstants();
            Context dogeContext = new Context(agentConstants.getDogeParams());
            final InetAddress localHost = InetAddress.getLocalHost();

            this.peerGroup = new PeerGroup(dogeContext);
            this.peerGroup.addAddress(localHost);
            this.peerGroup.setMaxConnections(1);
            this.peerGroup.start();

            new Timer("Eth to Doge client").scheduleAtFixedRate(new DefendSuperblocksTimerTask(),
                    Calendar.getInstance().getTime(), 15 * 1000);
        }
    }

    private class DefendSuperblocksTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (!ethWrapper.isEthNodeSyncing()) {
                    long fromBlock = latestEthBlockProcessed + 1;
                    long toBlock = ethWrapper.getEthBlockCount() -
                            config.getAgentConstants().getEth2DogeMinimumAcceptableConfirmations();

                    // Ignore execution if nothing to process
                    if (fromBlock > toBlock) return;

                    List<EthWrapper.SuperblockEvent> challengedSuperblockEvents =
                            ethWrapper.getChallengedSuperblocks(fromBlock, toBlock);

                    for (EthWrapper.SuperblockEvent challengedSuperblock : challengedSuperblockEvents) {
                        if (isMine(challengedSuperblock)) {
                            log.info("Superblock {} has been challenged. Defending it now.",
                                    Sha256Hash.wrap(challengedSuperblock.superblockId));
                        }
                    }

                    latestEthBlockProcessed = toBlock;
                    flushLatestEthBlockProcessed();
                } else {
                    log.warn("DefendSuperblocksTimerTask skipped because the eth node is syncing blocks");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }


    /* ---- STATUS SETTERS ---- */

    private void setNewSuperblocks(long fromBlock, long toBlock) throws IOException {
        List<EthWrapper.SuperblockEvent> newSuperblockEvents =
                ethWrapper.getNewSuperblocks(fromBlock, toBlock);

        for (EthWrapper.SuperblockEvent newSuperblock : newSuperblockEvents) {
            if (isMine(newSuperblock)) { // todo: thing with 'who' field
                log.info("Updating info for new superblock {}.", Sha256Hash.wrap(newSuperblock.superblockId));
                superblockChain.setStatus(newSuperblock.superblockId, SuperblockUtils.STATUS_NEW);
            }
        }
    }

    private void setApprovedSuperblocks(long fromBlock, long toBlock) throws IOException {
        List<EthWrapper.SuperblockEvent> approvedSuperblockEvents =
                ethWrapper.getApprovedSuperblocks(fromBlock, toBlock);

        for (EthWrapper.SuperblockEvent approvedSuperblock : approvedSuperblockEvents) {
            if (isMine(approvedSuperblock)) {
                log.info("Updating info for approved superblock {}.", Sha256Hash.wrap(approvedSuperblock.superblockId));
                superblockChain.setStatus(approvedSuperblock.superblockId, SuperblockUtils.STATUS_NEW);
            }
        }
    }

    private void setSemiApprovedSuperblocks(long fromBlock, long toBlock) throws IOException {
        List<EthWrapper.SuperblockEvent> semiApprovedSuperblockEvents =
                ethWrapper.getSemiApprovedSuperblocks(fromBlock, toBlock);

        for (EthWrapper.SuperblockEvent semiApprovedSuperblock : semiApprovedSuperblockEvents) {
            if (isMine(semiApprovedSuperblock)) {
                log.info("Updating info for approved superblock {}.",
                        Sha256Hash.wrap(semiApprovedSuperblock.superblockId));
                superblockChain.setStatus(semiApprovedSuperblock.superblockId, SuperblockUtils.STATUS_NEW);
            }
        }
    }


    /* ---- CONFIRMING/DEFENDING ---- */

    /**
     * Find earliest superblock that's unchallenged and stored locally,
     * but not confirmed in DogeRelay, and confirm it if its timeout has passed
     * and it either received no challenges or won all battles.
     * @throws Exception
     */
    private void confirmEarliestApprovableSuperblock() throws Exception {
        byte[] bestSuperblockId = ethWrapper.getBestSuperblockId();
        Superblock currentSuperblock = superblockChain.getChainHead();

        while (!Arrays.equals(currentSuperblock.getParentId(), bestSuperblockId)) {
            currentSuperblock = superblockChain.getSuperblock(currentSuperblock.getParentId());
        }

        byte[] toConfirmId = currentSuperblock.getSuperblockId();

        if (timeoutPassed(currentSuperblock) && ethWrapper.isNew(toConfirmId)) {
            log.info("Confirming superblock {}", Sha256Hash.wrap(toConfirmId));
            ethWrapper.checkClaimFinished(toConfirmId);
        }
    }

    private void respondToBlockHeaderQueries(long fromBlock, long toBlock) throws IOException {
        List<EthWrapper.QueryEvent> queryBlockHeaderEvents =
                ethWrapper.getBlockHeaderQueries(fromBlock, toBlock);

        for (EthWrapper.QueryEvent queryBlockHeader : queryBlockHeaderEvents) {
            if (isMine(queryBlockHeader)) {
                log.info("Header requested for superblock {}. Responding now.",
                        Sha256Hash.wrap(queryBlockHeader.sessionId));

                ethWrapper.respondBlockHeader(queryBlockHeader.sessionId, null);
            }

            // Call respondBlockHeader
        }
    }


    /* ---- HELPER METHODS ---- */

    // TODO: figure out what 'who' field should be compared to
    private boolean isMine(EthWrapper.SuperblockEvent superblockEvent) {
        return true;
    }

    private boolean isMine(EthWrapper.QueryEvent queryEvent) {
        return true;
    }

    private boolean timeoutPassed(Superblock superblock) throws Exception {
        return superblock.getNewEventDate().before(getTimeoutDate());
    }

    private Date getTimeoutDate() throws Exception {
        int superblockTimeout = ethWrapper.getSuperblockTimeout().intValue();
        return SuperblockUtils.getNSecondsAgo(superblockTimeout);
    }


    /* ---- DATABASE METHODS ---- */

    // TODO: see if these should be moved to another file to avoid repeated code
    private void restoreLatestEthBlockProcessed() throws IOException {
        if (latestEthBlockProcessedFile.exists()) {
            synchronized (this) {
                try (
                    FileInputStream latestEthBlockProcessedFileIs = new FileInputStream(latestEthBlockProcessedFile);
                    ObjectInputStream latestEthBlockProcessedObjectIs =
                            new ObjectInputStream(latestEthBlockProcessedFileIs);
                ) {
                    latestEthBlockProcessed = latestEthBlockProcessedObjectIs.readLong();
                }
            }
        }
    }

    private void flushLatestEthBlockProcessed() throws IOException {
        if (!dataDirectory.exists()) {
            if (!dataDirectory.mkdirs()) {
                throw new IOException("Could not create directory " + dataDirectory.getAbsolutePath());
            }
        }
        try (
            FileOutputStream latestEthBlockProcessedFileOs = new FileOutputStream(latestEthBlockProcessedFile);
            ObjectOutputStream latestEthBlockProcessedObjectOs = new ObjectOutputStream(latestEthBlockProcessedFileOs);
        ) {
            latestEthBlockProcessedObjectOs.writeLong(latestEthBlockProcessed);
        }
    }
}

// TODO: move to another class