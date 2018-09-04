package org.dogethereum.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.store.BlockStoreException;
import org.dogethereum.agents.constants.SystemProperties;
import org.dogethereum.agents.core.dogecoin.*;
import org.dogethereum.agents.core.eth.EthWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.util.*;

/**
 * Base class to monitor the Ethereum blockchain for superblock-related events
 * @author Catalina Juarros
 * @author Ismael Bejarano
 */

@Slf4j(topic = "SuperblockDefenderClient")
public abstract class SuperblockBaseClient {

    @Autowired
    protected DogecoinWrapper dogecoinWrapper;

    @Autowired
    protected EthWrapper ethWrapper;

    @Autowired
    protected SuperblockChain superblockChain;

    protected SystemProperties config;

    protected String clientName;

    protected String myAddress;

    // These 2 structures have the same data. Data is duplicated for performance using it.
    protected long latestEthBlockProcessed;

    // key: session id, value: superblock id
    protected HashMap<Keccak256Hash, Keccak256Hash> sessionToSuperblockMap;

    // key: superblock id, value: set of session ids
    protected HashMap<Keccak256Hash, HashSet<Keccak256Hash>> superblockToSessionsMap;

    protected File dataDirectory;
    protected File latestEthBlockProcessedFile;
    protected File sessionToSuperblockMapFile;
    protected File superblockToSessionsMapFile;

    public SuperblockBaseClient(String clientName) {
        this.clientName = clientName;
        this.config = SystemProperties.CONFIG;
    }

    @PostConstruct
    public void setup() throws Exception {
        if (isEnabled()) {
            setupFiles();

            restoreLatestEthBlockProcessed();
            restoreSessionToSuperblockMap();
            restoreSuperblockToSessionsMap();

            setupClient();

            setupTimer();
        }
    }

    @PreDestroy
    public void tearDown() throws BlockStoreException, IOException {
        if (isEnabled()) {
            log.info("{} tearDown starting...", clientName);

            flushLatestEthBlockProcessed();
            flushSessionToSuperblockMap();
            flushSuperblockToSessionsMap();

            log.info("{} tearDown finished.", clientName);
        }
    }

    private void setupTimer() {
       new Timer(clientName).scheduleAtFixedRate(new SuperblocksBaseClientTimerTask(),
               getFirstExecutionDate(), getTimerTaskPeriod());
    }

    private Date getFirstExecutionDate() {
        Calendar firstExecution = Calendar.getInstance();
        return firstExecution.getTime();
    }

    private class SuperblocksBaseClientTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (!ethWrapper.isEthNodeSyncing()) {
                    restoreFiles();

                    if (arePendingTransactions()) {
                        log.debug("Skipping because there are pending transaction for the sender address.");
                        return;
                    }

                    ethWrapper.updateContractFacadesGasPrice();

                    reactToElapsedTime();

                    long fromBlock = latestEthBlockProcessed + 1;
                    long toBlock = ethWrapper.getEthBlockCount() - getConfirmations() + 1;

                    // Ignore execution if nothing to process
                    if (fromBlock > toBlock) return;

                    getNewBattles(fromBlock, toBlock); // update battle set
                    latestEthBlockProcessed = reactToEvents(fromBlock, toBlock);

                    flushFiles();
                } else {
                    log.warn("SuperblocksBaseClientTimerTask skipped because the eth node is syncing blocks");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }


    /* ---- ABSTRACT METHODS ---- */

    protected abstract boolean arePendingTransactions() throws IOException;

    protected abstract long reactToEvents(long fromBlock, long toBlock);

    protected abstract boolean isEnabled();

    protected abstract String getLastEthBlockProcessedFilename();

    protected abstract String getSessionToSuperblockMapFilename();

    protected abstract String getSuperblockToSessionsMapFilename();

    protected abstract void setupClient();

    protected abstract void reactToElapsedTime();

    protected abstract boolean isMine(EthWrapper.NewBattleEvent newBattleEvent);

    protected abstract long getConfirmations();

    protected abstract void callBattleTimeouts() throws Exception;

    protected abstract long getTimerTaskPeriod(); // in seconds

    protected abstract void deleteSubmitterConvictedBattles(long fromBlock, long toBlock) throws Exception;

    protected abstract void deleteChallengerConvictedBattles(long fromBlock, long toBlock) throws Exception;

    protected abstract void removeInvalid(long fromBlock, long toBlock) throws Exception;

    protected abstract void restoreFiles() throws Exception;

    protected abstract void flushFiles() throws Exception;


    /* ---- DATABASE METHODS ---- */

    private void setupFiles() throws IOException {
        this.latestEthBlockProcessed = config.getAgentConstants().getEthInitialCheckpoint();
        this.dataDirectory = new File(config.dataDirectory());
        this.latestEthBlockProcessedFile = new File(dataDirectory.getAbsolutePath() +
                "/" + getLastEthBlockProcessedFilename());
        this.sessionToSuperblockMap =  new HashMap<>();
        this.sessionToSuperblockMapFile = new File(dataDirectory.getAbsolutePath() + "/" +
                getSessionToSuperblockMapFilename());
        this.superblockToSessionsMap = new HashMap<>();
        this.superblockToSessionsMapFile = new File(dataDirectory.getAbsolutePath() + "/"
                + getSuperblockToSessionsMapFilename());

    }

    void restoreLatestEthBlockProcessed() throws IOException {
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

    void flushLatestEthBlockProcessed() throws IOException {
        synchronized (this) {
            if (!dataDirectory.exists()) {
                if (!dataDirectory.mkdirs()) {
                    throw new IOException("Could not create directory " + dataDirectory.getAbsolutePath());
                }
            }
            try (
                FileOutputStream latestEthBlockProcessedFileOs = new FileOutputStream(latestEthBlockProcessedFile);
                ObjectOutputStream latestEthBlockProcessedObjectOs =
                        new ObjectOutputStream(latestEthBlockProcessedFileOs);
            ) {
                latestEthBlockProcessedObjectOs.writeLong(latestEthBlockProcessed);
            }
        }
    }

    void restoreSessionToSuperblockMap() throws IOException, ClassNotFoundException {
        if (sessionToSuperblockMapFile.exists()) {
            synchronized (this) {
                try (
                    FileInputStream sessionToSuperblockMapFileIs = new FileInputStream(sessionToSuperblockMapFile);
                    ObjectInputStream sessionToSuperblockMapObjectIs =
                            new ObjectInputStream(sessionToSuperblockMapFileIs);
                ) {
                    sessionToSuperblockMap =
                            (HashMap<Keccak256Hash, Keccak256Hash>) sessionToSuperblockMapObjectIs.readObject();
                }
            }
        }
    }

    void flushSessionToSuperblockMap() throws IOException {
        if (!dataDirectory.exists()) {
            if (!dataDirectory.mkdirs()) {
                throw new IOException("Could not create directory " + dataDirectory.getAbsolutePath());
            }
        }
        try (
            FileOutputStream sessionToSuperblockMapFileOs = new FileOutputStream(sessionToSuperblockMapFile);
            ObjectOutputStream sessionToSuperblockMapObjectOs = new ObjectOutputStream(sessionToSuperblockMapFileOs);
        ) {
            sessionToSuperblockMapObjectOs.writeObject(sessionToSuperblockMap);
        }
    }

    void restoreSuperblockToSessionsMap() throws IOException, ClassNotFoundException {
        if (superblockToSessionsMapFile.exists()) {
            synchronized (this) {
                try (
                        FileInputStream superblockToSessionsMapFileIs =
                                new FileInputStream(superblockToSessionsMapFile);
                        ObjectInputStream superblockToSessionsMapObjectIs =
                                new ObjectInputStream(superblockToSessionsMapFileIs);
                ) {
                    superblockToSessionsMap = (HashMap<Keccak256Hash, HashSet<Keccak256Hash>>)
                            superblockToSessionsMapObjectIs.readObject();
                }
            }
        }
    }

    void flushSuperblockToSessionsMap() throws IOException {
        if (!dataDirectory.exists()) {
            if (!dataDirectory.mkdirs()) {
                throw new IOException("Could not create directory " + dataDirectory.getAbsolutePath());
            }
        }
        try (
                FileOutputStream superblockToSessionsMapFileOs = new FileOutputStream(superblockToSessionsMapFile);
                ObjectOutputStream superblockToSessionsMapObjectOs =
                        new ObjectOutputStream(superblockToSessionsMapFileOs);
        ) {
            superblockToSessionsMapObjectOs.writeObject(superblockToSessionsMap);
        }
    }


    /* ---- BATTLE MAP METHODS ---- */

    /**
     * Listen to NewBattle events to keep track of new battles that this client is taking part in.
     * @param fromBlock
     * @param toBlock
     * @throws IOException
     */
    private void getNewBattles(long fromBlock, long toBlock) throws IOException {
        List<EthWrapper.NewBattleEvent> newBattleEvents = ethWrapper.getNewBattleEvents(fromBlock, toBlock);
        for (EthWrapper.NewBattleEvent newBattleEvent : newBattleEvents) {
            if (isMine(newBattleEvent)) {
                Keccak256Hash sessionId = newBattleEvent.sessionId;
                Keccak256Hash superblockId = newBattleEvent.superblockId;
                sessionToSuperblockMap.put(sessionId, superblockId);

                if (superblockToSessionsMap.containsKey(superblockId)) {
                    superblockToSessionsMap.get(superblockId).add(sessionId);
                } else {
                    HashSet<Keccak256Hash> newSuperblockBattles = new HashSet<>();
                    newSuperblockBattles.add(sessionId);
                    superblockToSessionsMap.put(superblockId, newSuperblockBattles);
                }
            }
        }
    }

    /**
     * Remove semi-approved superblocks from a data structure that keeps track of in battle superblocks.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    void removeSemiApproved(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.SuperblockEvent> semiApprovedSuperblockEvents =
                ethWrapper.getSemiApprovedSuperblocks(fromBlock, toBlock);

        for (EthWrapper.SuperblockEvent semiApprovedSuperblockEvent : semiApprovedSuperblockEvents) {
            if (superblockToSessionsMap.containsKey(semiApprovedSuperblockEvent.superblockId)) {
                superblockToSessionsMap.remove(semiApprovedSuperblockEvent.superblockId);
            }
        }
    }

    /**
     * Listen to SubmitterConvicted and ChallengerConvicted events to remove battles that have already ended.
     * @param fromBlock
     * @param toBlock
     * @throws IOException
     */
    void deleteFinishedBattles(long fromBlock, long toBlock) throws Exception {
        deleteSubmitterConvictedBattles(fromBlock, toBlock);
        deleteChallengerConvictedBattles(fromBlock, toBlock);
    }


    /* ----- HELPER METHODS ----- */

    boolean isMine(EthWrapper.SuperblockEvent superblockEvent) {
        return superblockEvent.who.equals(myAddress);
    }

    boolean isMine(Keccak256Hash superblockId) throws Exception {
        return ethWrapper.getClaimSubmitter(superblockId).equals(myAddress);
    }

    // TODO: see if this should be deleted now that data structures are maintained by responding to events
    /**
     *
     * @param superblockId
     */
    void deleteSuperblockSessions(Keccak256Hash superblockId) {
        if (superblockToSessionsMap.containsKey(superblockId)) {
            HashSet<Keccak256Hash> superblockBattles = superblockToSessionsMap.get(superblockId);

            for (Keccak256Hash sessionId : superblockBattles) {
                sessionToSuperblockMap.remove(sessionId);
            }

            superblockToSessionsMap.remove(superblockId);
        }
    }
}
