package org.sysethereum.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.sysethereum.agents.constants.AgentConstants;
import org.sysethereum.agents.constants.SystemProperties;
import org.sysethereum.agents.core.bridge.ClaimContractApi;
import org.sysethereum.agents.core.bridge.SuperblockContractApi;
import org.sysethereum.agents.core.syscoin.*;
import org.sysethereum.agents.core.eth.EthWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sysethereum.agents.service.ChallengeEmailNotifier;
import org.sysethereum.agents.service.ChallengeReport;

import javax.annotation.Nullable;
import javax.annotation.PreDestroy;
import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Base class to monitor the Ethereum blockchain for superblock-related events
 * @author Catalina Juarros
 * @author Ismael Bejarano
 */
@Slf4j(topic = "SuperblockBaseClient")
public abstract class SuperblockBaseClient extends PersistentFileStore {
    private static final Logger logger = LoggerFactory.getLogger("SuperblockBaseClient");

    protected final AgentConstants agentConstants;
    protected final SyscoinWrapper syscoinWrapper;
    protected final EthWrapper ethWrapper;
    protected final SuperblockContractApi superblockContractApi;
    protected final ClaimContractApi claimContractApi;
    protected final SuperblockChain superblockChain;
    protected final SystemProperties config;
    protected final String clientName;
    protected String myAddress;
    protected long latestEthBlockProcessed;
    protected final File latestEthBlockProcessedFile;

    // Data is duplicated for performance using it.

    // key: session id, value: superblock id
    protected HashMap<Keccak256Hash, Keccak256Hash> sessionToSuperblockMap;

    // key: superblock id, value: set of session ids
    protected HashMap<Keccak256Hash, HashSet<Keccak256Hash>> superblockToSessionsMap;

    protected final File sessionToSuperblockMapFile;
    protected final File superblockToSessionsMapFile;
    private final Timer timer;
    private final ChallengeEmailNotifier challengeEmailNotifier;

    public SuperblockBaseClient(
            String clientName,
            SystemProperties systemProperties,
            AgentConstants agentConstants,
            SyscoinWrapper syscoinWrapper,
            EthWrapper ethWrapper,
            SuperblockContractApi superblockContractApi,
            ClaimContractApi claimContractApi,
            SuperblockChain superblockChain,
            ChallengeEmailNotifier challengeEmailNotifier
    ) {
        super(systemProperties.dataDirectory());

        this.clientName = clientName;
        this.config = systemProperties;
        this.agentConstants = agentConstants;
        this.syscoinWrapper = syscoinWrapper;
        this.ethWrapper = ethWrapper;
        this.superblockContractApi = superblockContractApi;
        this.claimContractApi = claimContractApi;
        this.superblockChain = superblockChain;
        this.timer = new Timer(clientName, true);
        this.challengeEmailNotifier = challengeEmailNotifier;

        this.latestEthBlockProcessedFile = new File(dataDirectory.getAbsolutePath() + "/" + getLastEthBlockProcessedFilename());
        this.sessionToSuperblockMapFile = new File(dataDirectory.getAbsolutePath() + "/" + getSessionToSuperblockMapFilename());
        this.superblockToSessionsMapFile = new File(dataDirectory.getAbsolutePath() + "/" + getSuperblockToSessionsMapFilename());

        this.latestEthBlockProcessed = agentConstants.getEthInitialCheckpoint();
        this.sessionToSuperblockMap = new HashMap<>();
        this.superblockToSessionsMap = new HashMap<>();
    }

    public boolean setup() throws ClassNotFoundException, IOException {
        if (isEnabled()) {
            restoreFiles();
            try {
                timer.scheduleAtFixedRate(new SuperblocksBaseClientTimerTask(), 0, getTimerTaskPeriod());
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    @PreDestroy
    public void cleanUp() throws ClassNotFoundException, IOException {
        if (isEnabled()) {
            logger.info("cleanUp[{}]: Starting...", clientName);

            timer.cancel();
            timer.purge();
            logger.info("cleanUp: Timer was canceled.");

            flushFiles();

            logger.info("cleanUp[{}]: finished.", clientName);
        }
    }

    private class SuperblocksBaseClientTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (!ethWrapper.isEthNodeSyncing()) {
                    restoreFiles();

                    if (arePendingTransactions()) {
                        logger.debug("Skipping because there are pending transaction for the sender address.");
                        return;
                    }

                    ethWrapper.updateContractFacadesGasPrice();

                    reactToElapsedTime();

                    long fromBlock = latestEthBlockProcessed + 1;
                    long toBlock = ethWrapper.getEthBlockCount() - getConfirmations() + 1;

                    // Ignore execution if nothing to process
                    if (fromBlock > toBlock) return;

                    // Maintain data structures and react to events
                    ChallengeReport report = getNewBattles(fromBlock, toBlock);

                    if (report != null) {
                        CompletableFuture.supplyAsync(() -> challengeEmailNotifier.sendIfEnabled(report));
                    }

                    removeApproved(fromBlock, toBlock);
                    removeInvalid(fromBlock, toBlock);
                    deleteFinishedBattles(fromBlock, toBlock);
                    latestEthBlockProcessed = reactToEvents(fromBlock, toBlock);

                    flushFiles();
                } else {
                    logger.warn("SuperblocksBaseClientTimerTask skipped because the eth node is syncing blocks");
                }
            } catch (Exception e) {
                logger.error("SuperblocksBaseClientTimerTask: Exception: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * Listens to NewBattle events to keep track of new battles that this client is taking part in.
     * @param fromBlock
     * @param toBlock
     * @throws IOException
     * @return null when no events were found otherwise a ChallengeReport
     */
    @Nullable
    protected ChallengeReport getNewBattles(long fromBlock, long toBlock) throws IOException {
        var challenged = new ArrayList<Keccak256Hash>();
        boolean isAtLeastOneMine = false;

        List<EthWrapper.NewBattleEvent> events = ethWrapper.getNewBattleEvents(fromBlock, toBlock);
        for (EthWrapper.NewBattleEvent event : events) {
            if (isMine(event)) {
                isAtLeastOneMine = true;
                sessionToSuperblockMap.put(event.sessionId, event.superblockHash);
                addToSuperblockToSessionsMap(event.sessionId, event.superblockHash);
            }

            challenged.add(event.superblockHash);
        }

        return events.size() == 0 ? null : new ChallengeReport(isAtLeastOneMine, challenged);
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

    protected abstract boolean arePendingTransactions() throws InterruptedException, IOException;

    protected abstract long reactToEvents(long fromBlock, long toBlock);

    protected abstract boolean isEnabled();

    protected abstract String getLastEthBlockProcessedFilename();

    protected abstract String getSessionToSuperblockMapFilename();

    protected abstract String getSuperblockToSessionsMapFilename();

    protected abstract void reactToElapsedTime();

    protected abstract boolean isMine(EthWrapper.NewBattleEvent newBattleEvent);

    protected abstract long getConfirmations();


    protected abstract long getTimerTaskPeriod(); // in seconds

    protected abstract void deleteSubmitterConvictedBattles(long fromBlock, long toBlock) throws Exception;

    protected abstract void deleteChallengerConvictedBattles(long fromBlock, long toBlock) throws Exception;

    protected abstract void removeSuperblocks(long fromBlock, long toBlock,
                                              List<SuperblockContractApi.SuperblockEvent> superblockEvents) throws Exception;

    protected abstract void restoreFiles() throws ClassNotFoundException, IOException;

    protected abstract void flushFiles() throws ClassNotFoundException, IOException;


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
        List<SuperblockContractApi.SuperblockEvent> approvedSuperblockEvents =
                superblockContractApi.getApprovedSuperblocks(fromBlock, toBlock);
        removeSuperblocks(fromBlock, toBlock, approvedSuperblockEvents);
    }

    /**
     * Removes invalidated superblocks from data structures that keep track of semi-approved and in battle superblocks.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    protected void removeInvalid(long fromBlock, long toBlock) throws Exception {
        List<SuperblockContractApi.SuperblockEvent> invalidSuperblockEvents = superblockContractApi.getInvalidSuperblocks(fromBlock, toBlock);
        removeSuperblocks(fromBlock, toBlock, invalidSuperblockEvents);
    }

    /* ----- HELPER METHODS ----- */

    protected boolean isMine(Keccak256Hash superblockId) throws Exception {
        return claimContractApi.getClaimSubmitter(superblockId).equals(myAddress);
    }

}
