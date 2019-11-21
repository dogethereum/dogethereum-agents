package org.sysethereum.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.sysethereum.agents.constants.AgentConstants;
import org.sysethereum.agents.constants.AgentRole;
import org.sysethereum.agents.constants.SystemProperties;
import org.sysethereum.agents.core.bridge.BattleContractApi;
import org.sysethereum.agents.core.bridge.ClaimContractApi;
import org.sysethereum.agents.core.bridge.SuperblockContractApi;
import org.sysethereum.agents.core.bridge.battle.NewBattleEvent;
import org.sysethereum.agents.core.syscoin.*;
import org.sysethereum.agents.core.eth.EthWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sysethereum.agents.service.ChallengeEmailNotifier;
import org.sysethereum.agents.service.ChallengeReport;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Base class to monitor the Ethereum blockchain for superblock-related events
 * @author Catalina Juarros
 * @author Ismael Bejarano
 */
@Slf4j(topic = "SuperblockBaseClient")
public abstract class SuperblockBaseClient {
    private static final Logger logger = LoggerFactory.getLogger("SuperblockBaseClient");

    protected final AgentConstants agentConstants;
    protected final EthWrapper ethWrapper;
    protected final SuperblockContractApi superblockContractApi;
    private final BattleContractApi battleContractApi;
    protected final ClaimContractApi claimContractApi;
    protected final AgentRole agentRole;
    protected String myAddress;
    protected long latestEthBlockProcessed;
    protected final File latestEthBlockProcessedFile;


    // set of superblock id's involving this agent in challenging
    protected HashSet<Keccak256Hash> sessionToSuperblockMap;

    protected final File sessionToSuperblockMapFile;
    private final Timer timer;
    private final ChallengeEmailNotifier challengeEmailNotifier;

    public SuperblockBaseClient(
            AgentRole agentRole,
            SystemProperties config,
            AgentConstants agentConstants,
            EthWrapper ethWrapper,
            SuperblockContractApi superblockContractApi,
            BattleContractApi battleContractApi,
            ClaimContractApi claimContractApi,
            ChallengeEmailNotifier challengeEmailNotifier
    ) {
        this.agentRole = agentRole;
        this.agentConstants = agentConstants;
        this.ethWrapper = ethWrapper;
        this.superblockContractApi = superblockContractApi;
        this.battleContractApi = battleContractApi;
        this.claimContractApi = claimContractApi;
        this.timer = new Timer(agentRole.getTimerTaskName(), true);
        this.challengeEmailNotifier = challengeEmailNotifier;

        this.latestEthBlockProcessedFile = Paths.get(config.dataDirectory(), config.getLastEthBlockProcessedFilename(agentRole)).toAbsolutePath().toFile();
        this.sessionToSuperblockMapFile = Paths.get(config.dataDirectory(), config.getSessionToSuperblockMapFilename(agentRole)).toAbsolutePath().toFile();

        this.latestEthBlockProcessed = 0;
        this.sessionToSuperblockMap = new HashSet<>();
    }

    public boolean setup() throws ClassNotFoundException, IOException {
        restoreFiles();
        try {
            timer.scheduleAtFixedRate(new SuperblocksBaseClientTimerTask(), 0, agentConstants.getTimerTaskPeriod(agentRole));
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public void cleanUp() throws IOException {
        logger.info("cleanUp[{}]: Starting...", agentRole);

        timer.cancel();
        timer.purge();
        logger.info("cleanUp: Timer was canceled.");

        flushFiles();

        logger.info("cleanUp[{}]: finished.", agentRole);
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


                    long toBlock = ethWrapper.getEthBlockCount() - agentConstants.getConfirmations(agentRole) + 1;
                    long fromBlock;
                    if(latestEthBlockProcessed == 0)
                        fromBlock = toBlock - 5000;
                    else
                        fromBlock = latestEthBlockProcessed + 1;

                    // Ignore execution if nothing to process
                    if (fromBlock > toBlock) return;

                    // Maintain data structures and react to events
                    ChallengeReport report = getNewBattles(fromBlock, toBlock);

                    if (report != null) {
                        CompletableFuture.supplyAsync(() -> challengeEmailNotifier.sendIfEnabled(report));
                    }

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

        List<NewBattleEvent> events = battleContractApi.getNewBattleEvents(fromBlock, toBlock);
        for (NewBattleEvent event : events) {
            if (isMyBattleEvent(event)) {
                isAtLeastOneMine = true;
                sessionToSuperblockMap.add(event.superblockHash);
            }

            challenged.add(event.superblockHash);
        }

        return events.size() == 0 ? null : new ChallengeReport(isAtLeastOneMine, challenged);
    }


    protected boolean isMyBattleEvent(NewBattleEvent newBattleEvent) {
        return newBattleEvent.getAddressByRole(agentRole).equalsIgnoreCase(myAddress);
    }

    protected final boolean arePendingTransactions() throws InterruptedException, IOException {
        return agentRole == AgentRole.SUBMITTER
                ? ethWrapper.arePendingTransactionsForSendSuperblocksAddress()
                : ethWrapper.arePendingTransactionsForChallengerAddress();
    }

    /* ---- ABSTRACT METHODS ---- */

    protected abstract long reactToEvents(long fromBlock, long toBlock);

    protected abstract void reactToElapsedTime();

    protected abstract void restoreFiles() throws ClassNotFoundException, IOException;

    protected abstract void flushFiles() throws IOException;


    /* ---- BATTLE MAP METHODS ---- */



    /* ----- HELPER METHODS ----- */

    protected boolean isMine(Keccak256Hash superblockId) throws Exception {
        return claimContractApi.getClaimSubmitter(superblockId).equalsIgnoreCase(myAddress);
    }

}
