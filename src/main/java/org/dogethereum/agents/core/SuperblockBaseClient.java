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
    protected HashMap<Keccak256Hash, Keccak256Hash> battleMap;
    // key: superblock id, value: list of session id
    protected HashMap<Keccak256Hash, HashSet<Keccak256Hash>> superblockBattleMap;

    protected File dataDirectory;
    protected File latestEthBlockProcessedFile;
    protected File battleMapFile;
    protected File superblockBattleMapFile;

    public SuperblockBaseClient(String clientName) {
        this.clientName = clientName;
        this.config = SystemProperties.CONFIG;
    }

    @PostConstruct
    public void setup() throws Exception {
        if (isEnabled()) {
            setupFiles();

            restoreLatestEthBlockProcessed();
            restoreBattleMap();
            restoreSuperblockBattleMap();

            setupClient();

            setupTimer();
        }
    }

    @PreDestroy
    public void tearDown() throws BlockStoreException, IOException {
        if (isEnabled()) {
            log.info("{} tearDown starting...", clientName);

            flushLatestEthBlockProcessed();
            flushBattleMap();
            flushSuperblockBattleMap();

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
//                log.debug("/////////Running");
                if (!ethWrapper.isEthNodeSyncing()) {
                    if (arePendingTransactions()) {
                        log.debug("Skipping there are pending transaction for the sender address.");
                        return;
                    }

                    ethWrapper.updateContractFacadesGasPrice();

                    reactToElapsedTime();

                    long fromBlock = latestEthBlockProcessed + 1;
                    long toBlock = ethWrapper.getEthBlockCount() - getConfirmations() + 1;
                    log.debug("fromBlock: {}, toBlock: {}", fromBlock, toBlock);

                    // Ignore execution if nothing to process
                    if (fromBlock > toBlock) {
                        log.info("Nothing to process");
                        return;
                    }

                    getNewBattles(fromBlock, toBlock); // update battle set
                    latestEthBlockProcessed = reactToEvents(fromBlock, toBlock);

                    flushLatestEthBlockProcessed();
                    flushBattleMap();
                    flushSuperblockBattleMap();

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

    protected abstract String getBattleMapFilename();

    protected abstract String getSuperblockBattleMapFilename();

    protected abstract void setupClient();

    protected abstract void reactToElapsedTime();

    protected abstract boolean isMine(EthWrapper.NewBattleEvent newBattleEvent);

    protected abstract long getConfirmations();

    protected abstract void callBattleTimeouts() throws Exception;

    protected abstract long getTimerTaskPeriod(); // in seconds

    protected abstract void deleteSubmitterConvictedBattles(long fromBlock, long toBlock) throws Exception;

    protected abstract void deleteChallengerConvictedBattles(long fromBlock, long toBlock) throws Exception;


    /* ---- DATABASE METHODS ---- */

    private void setupFiles() throws IOException {
        this.latestEthBlockProcessed = config.getAgentConstants().getEthInitialCheckpoint();
        this.dataDirectory = new File(config.dataDirectory());
        this.latestEthBlockProcessedFile = new File(dataDirectory.getAbsolutePath() +
                "/" + getLastEthBlockProcessedFilename());
        this.battleMap =  new HashMap<>();
        this.battleMapFile = new File(dataDirectory.getAbsolutePath() + "/" + getBattleMapFilename());
        this.superblockBattleMap = new HashMap<>();
        this.superblockBattleMapFile = new File(dataDirectory.getAbsolutePath() + "/" + getSuperblockBattleMapFilename());

    }

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
        synchronized (this) {
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

    private void restoreBattleMap() throws IOException, ClassNotFoundException {
        if (battleMapFile.exists()) {
            synchronized (this) {
                try (
                    FileInputStream battleMapFileIs = new FileInputStream(battleMapFile);
                    ObjectInputStream battleMapObjectIs = new ObjectInputStream(battleMapFileIs);
                ) {
                    battleMap = (HashMap<Keccak256Hash, Keccak256Hash>) battleMapObjectIs.readObject();
                }
            }
        }
    }

    private void flushBattleMap() throws IOException {
        if (!dataDirectory.exists()) {
            if (!dataDirectory.mkdirs()) {
                throw new IOException("Could not create directory " + dataDirectory.getAbsolutePath());
            }
        }
        try (
            FileOutputStream battleMapFileOs = new FileOutputStream(battleMapFile);
            ObjectOutputStream battleMapObjectOs = new ObjectOutputStream(battleMapFileOs);
        ) {
            battleMapObjectOs.writeObject(battleMap);
        }
    }


    private void restoreSuperblockBattleMap() throws IOException, ClassNotFoundException {
        if (superblockBattleMapFile.exists()) {
            synchronized (this) {
                try (
                        FileInputStream superblockBattleMapFileIs = new FileInputStream(superblockBattleMapFile);
                        ObjectInputStream superblockBattleMapObjectIs = new ObjectInputStream(superblockBattleMapFileIs);
                ) {
                    superblockBattleMap = (HashMap<Keccak256Hash, HashSet<Keccak256Hash>>)
                            superblockBattleMapObjectIs.readObject();
                }
            }
        }
    }

    private void flushSuperblockBattleMap() throws IOException {
        if (!dataDirectory.exists()) {
            if (!dataDirectory.mkdirs()) {
                throw new IOException("Could not create directory " + dataDirectory.getAbsolutePath());
            }
        }
        try (
                FileOutputStream superblockBattleMapFileOs = new FileOutputStream(superblockBattleMapFile);
                ObjectOutputStream superblockBattleMapObjectOs = new ObjectOutputStream(superblockBattleMapFileOs);
        ) {
            superblockBattleMapObjectOs.writeObject(superblockBattleMap);
        }
    }


    /* ---- BATTLE SET METHODS ---- */

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
                battleMap.put(sessionId, superblockId);

                if (superblockBattleMap.containsKey(superblockId)) {
                    superblockBattleMap.get(superblockId).add(sessionId);
                } else {
                    HashSet<Keccak256Hash> newSuperblockBattles = new HashSet<>();
                    newSuperblockBattles.add(sessionId);
                    superblockBattleMap.put(superblockId, newSuperblockBattles);
                }
            }
        }
    }

    /**
     * Listen to SubmitterConvicted and ChallengerConvicted events to remove battles that have already ended.
     * @param fromBlock
     * @param toBlock
     * @throws IOException
     */
    protected void deleteFinishedBattles(long fromBlock, long toBlock) throws Exception {
        deleteSubmitterConvictedBattles(fromBlock, toBlock);
        deleteChallengerConvictedBattles(fromBlock, toBlock);
    }


    /* ----- HELPER METHODS ----- */

    boolean isMine(EthWrapper.SuperblockEvent superblockEvent) {
        return superblockEvent.who.equals(myAddress);
    }

    void deleteSuperblockBattles(Keccak256Hash superblockId) {
        if (superblockBattleMap.containsKey(superblockId)) {
            HashSet<Keccak256Hash> superblockBattles = superblockBattleMap.get(superblockId);

            for (Keccak256Hash sessionId : superblockBattles) {
                battleMap.remove(sessionId);
            }

            superblockBattleMap.remove(superblockId);
        }
    }

    protected void logErrorClaimEvents(long fromBlock, long toBlock) throws IOException {
        List<EthWrapper.ErrorClaimEvent> errorClaimEvents = ethWrapper.getErrorClaimEvents(fromBlock, toBlock);

        for (EthWrapper.ErrorClaimEvent errorClaimEvent : errorClaimEvents) {
            log.info("ErrorClaim. Session ID: {}, error: {}", errorClaimEvent.claimId, errorClaimEvent.err);
        }
    }

    protected void logSuperblockClaimFailedEvents(long fromBlock, long toBlock) throws IOException {
        List<EthWrapper.SuperblockClaimFailedEvent> superblockClaimFailedEvents =
                ethWrapper.getSuperblockClaimFailedEvents(fromBlock, toBlock);

        for (EthWrapper.SuperblockClaimFailedEvent superblockClaimFailedEvent : superblockClaimFailedEvents) {
            log.info("SuperblockClaimFailed. Claim ID: {}", superblockClaimFailedEvent);
        }
    }
}
