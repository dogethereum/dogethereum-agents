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
 * Base class for Superblock challenger and defender agents.
 * @author Catalina Juarros
 * @author Ismael Bejarano
 */

@Slf4j(topic = "SuperblockBattleBaseAgent")
public abstract class SuperblockBattleBaseAgent extends PersistentFileStore {

    @Autowired
    protected DogecoinWrapper dogecoinWrapper;

    @Autowired
    protected EthWrapper ethWrapper;

    @Autowired
    protected Superblockchain superblockchain;

    protected SystemProperties config;

    protected String agentName;

    protected String myAddress;

    protected long latestEthBlockProcessed;
    protected File latestEthBlockProcessedFile;


    // Data is duplicated for performance using it.

    // key: session id, value: superblock id
    protected HashMap<Keccak256Hash, Keccak256Hash> sessionToSuperblockMap;

    // key: superblock id, value: set of session ids
    protected HashMap<Keccak256Hash, HashSet<Keccak256Hash>> superblockToSessionsMap;

    protected File sessionToSuperblockMapFile;
    protected File superblockToSessionsMapFile;


    public SuperblockBattleBaseAgent(String agentName) {
        this.agentName = agentName;
        this.config = SystemProperties.CONFIG;
    }

    @PostConstruct
    public void setup() throws ClassNotFoundException, IOException {
        if (isEnabled()) {
            setupFiles();

            restoreFiles();

            setupAgent();

            setupTimer();
        }
    }

    @PreDestroy
    public void tearDown() throws BlockStoreException, ClassNotFoundException, IOException {
        if (isEnabled()) {
            log.info("{} tearDown starting...", agentName);

            flushFiles();

            log.info("{} tearDown finished.", agentName);
        }
    }

    private void setupTimer() {
       new Timer(agentName).scheduleAtFixedRate(new SuperblocksBattleBaseAgentTimerTask(),
               getFirstExecutionDate(), getTimerTaskPeriod());
    }

    private Date getFirstExecutionDate() {
        Calendar firstExecution = Calendar.getInstance();
        return firstExecution.getTime();
    }

    private class SuperblocksBattleBaseAgentTimerTask extends TimerTask {
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

                    // Maintain data structures and react to events
                    removeApproved(fromBlock, toBlock);
                    removeInvalid(fromBlock, toBlock);
                    getNewBattles(fromBlock, toBlock);
                    deleteFinishedBattles(fromBlock, toBlock);
                    latestEthBlockProcessed = reactToEvents(fromBlock, toBlock);

                    flushFiles();
                } else {
                    log.warn("SuperblocksBattleBaseAgentTimerTask skipped because the eth node is syncing blocks");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Listens to NewBattle events to keep track of new battles that this agent is taking part in.
     * @param fromBlock
     * @param toBlock
     * @throws IOException
     */
    protected void getNewBattles(long fromBlock, long toBlock) throws IOException {
        List<EthWrapper.NewBattleEvent> newBattleEvents = ethWrapper.getNewBattleEvents(fromBlock, toBlock);
        for (EthWrapper.NewBattleEvent newBattleEvent : newBattleEvents) {
            if (isMine(newBattleEvent)) {
                Keccak256Hash sessionId = newBattleEvent.sessionId;
                Keccak256Hash superblockId = newBattleEvent.superblockId;
                sessionToSuperblockMap.put(sessionId, superblockId);
                addToSuperblockToSessionsMap(sessionId, superblockId);
            }
        }
    }

    protected void addToSuperblockToSessionsMap(Keccak256Hash sessionId, Keccak256Hash superblockId) {
        if (superblockToSessionsMap.containsKey(superblockId)) {
            superblockToSessionsMap.get(superblockId).add(sessionId);
        } else {
            HashSet<Keccak256Hash> newSuperblockBattles = new HashSet<>();
            newSuperblockBattles.add(sessionId);
            superblockToSessionsMap.put(superblockId, newSuperblockBattles);
        }
    }


    /* ---- ABSTRACT METHODS ---- */

    protected abstract boolean arePendingTransactions() throws IOException;

    protected abstract long reactToEvents(long fromBlock, long toBlock);

    protected abstract boolean isEnabled();

    protected abstract String getLastEthBlockProcessedFilename();

    protected abstract String getSessionToSuperblockMapFilename();

    protected abstract String getSuperblockToSessionsMapFilename();

    protected abstract void setupAgent();

    protected abstract void reactToElapsedTime();

    protected abstract boolean isMine(EthWrapper.NewBattleEvent newBattleEvent);

    protected abstract long getConfirmations();

    protected abstract void callBattleTimeouts() throws Exception;

    protected abstract long getTimerTaskPeriod(); // in seconds

    protected abstract void deleteSubmitterConvictedBattles(long fromBlock, long toBlock) throws Exception;

    protected abstract void deleteChallengerConvictedBattles(long fromBlock, long toBlock) throws Exception;

    protected abstract void removeSuperblocks(long fromBlock, long toBlock,
                                              List<EthWrapper.SuperblockEvent> superblockEvents) throws Exception;

    protected abstract void restoreFiles() throws ClassNotFoundException, IOException;

    protected abstract void flushFiles() throws ClassNotFoundException, IOException;


    /* ---- DATABASE METHODS ---- */

    void setupBaseFiles() throws IOException {
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


    /* ---- BATTLE MAP METHODS ---- */

    /**
     * Listens to SubmitterConvicted and ChallengerConvicted events to remove battles that have already ended.
     * @param fromBlock First Ethereum block to be polled.
     * @param toBlock Last Ethereum block to be polled.
     * @throws IOException
     */
    private void deleteFinishedBattles(long fromBlock, long toBlock) throws Exception {
        deleteSubmitterConvictedBattles(fromBlock, toBlock);
        deleteChallengerConvictedBattles(fromBlock, toBlock);
    }

    /**
     * Removes approved superblocks from the data structures that keep track of semi-approved and in battle superblocks.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    protected void removeApproved(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.SuperblockEvent> approvedSuperblockEvents =
                ethWrapper.getApprovedSuperblocks(fromBlock, toBlock);
        removeSuperblocks(fromBlock, toBlock, approvedSuperblockEvents);
    }

    /**
     * Removes invalidated superblocks from data structures that keep track of semi-approved and in battle superblocks.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    protected void removeInvalid(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.SuperblockEvent> invalidSuperblockEvents = ethWrapper.getInvalidSuperblocks(fromBlock, toBlock);
        removeSuperblocks(fromBlock, toBlock, invalidSuperblockEvents);
    }


    /* ----- HELPER METHODS ----- */

    boolean isMine(EthWrapper.SuperblockEvent superblockEvent) {
        return superblockEvent.who.equals(myAddress);
    }

    boolean isMine(Keccak256Hash superblockId) throws Exception {
        return ethWrapper.getClaimSubmitter(superblockId).equals(myAddress);
    }

}
