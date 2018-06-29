package org.dogethereum.dogesubmitter.core;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.dogethereum.dogesubmitter.constants.AgentConstants;
import org.dogethereum.dogesubmitter.constants.SystemProperties;
import org.dogethereum.dogesubmitter.contract.DogeSuperblocks;
import org.dogethereum.dogesubmitter.core.dogecoin.DogecoinWrapper;
import org.dogethereum.dogesubmitter.core.dogecoin.SuperblockChain;
import org.dogethereum.dogesubmitter.core.eth.EthWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.*;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

    private void setNewSuperblocks(long fromBlock, long toBlock) throws IOException {
        List<EthWrapper.SuperblockEvent> newSuperblockEvents =
                ethWrapper.getNewSuperblocks(fromBlock, toBlock);

        for (EthWrapper.SuperblockEvent newSuperblock : newSuperblockEvents) {
            if (isMine(newSuperblock)) { // todo: thing with 'who' field
                log.info("Updating info for new superblock {}.", Sha256Hash.wrap(newSuperblock.superblockId));
            }
        }
    }

    private void confirmSuperblocks() {}

    private void respondToBlockHeaderQueries(long fromBlock, long toBlock) throws IOException {
        List<EthWrapper.QueryEvent> queryBlockHeaderEvents =
                ethWrapper.getBlockHeaderQueries(fromBlock, toBlock);

        for (EthWrapper.QueryEvent queryBlockHeader : queryBlockHeaderEvents) {
            if (isMine(queryBlockHeader)) {
                log.info("Header requested for superblock {}. Responding now.",
                        Sha256Hash.wrap(queryBlockHeader.sessionId));
            }

            // Call respondBlockHeader
        }
    }

    // TODO: figure out what 'who' field should be compared to
    private boolean isMine(EthWrapper.SuperblockEvent superblockEvent) {
        return true;
    }

    private boolean isMine(EthWrapper.QueryEvent queryEvent) {
        return true;
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