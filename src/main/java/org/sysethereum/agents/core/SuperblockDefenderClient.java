package org.sysethereum.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.AltcoinBlock;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.core.Sha256Hash;
import org.sysethereum.agents.contract.SyscoinBattleManager;
import org.sysethereum.agents.core.syscoin.*;
import org.sysethereum.agents.core.eth.EthWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

/**
 * Monitors the Ethereum blockchain for superblock-related events
 * and defends/confirms the ones submitted by the agent.
 * @author Catalina Juarros
 */

@Service
@Slf4j(topic = "SuperblockDefenderClient")
public class SuperblockDefenderClient extends SuperblockBaseClient {
    private static final Logger log = LoggerFactory.getLogger("LocalAgentConstants");
    private static long ETH_REQUIRED_CONFIRMATIONS = 5;

    public SuperblockDefenderClient() {
        super("Superblock defender client");
    }

    @Override
    protected void setupClient() {
        myAddress = ethWrapper.getGeneralPurposeAndSendSuperblocksAddress();
    }

    @Override
    public long reactToEvents(long fromBlock, long toBlock) {
        try {
            respondToMerkleRootHashesQueries(fromBlock, toBlock);
            respondToLastBlockHeaderQueries(fromBlock, toBlock);

            // Maintain data structures
            removeSemiApprovedDescendants(fromBlock, toBlock);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return fromBlock - 1;
        }
        return toBlock;
    }

    @Override
    protected void reactToElapsedTime() {
        try {
            confirmEarliestApprovableSuperblock();
            callBattleTimeouts();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /* ---- CONFIRMING/DEFENDING ---- */

    /* - Reacting to elapsed time - */

    /**
     * Finds earliest superblock that's not invalid and stored locally,
     * but not confirmed in Sysethereum Contracts, and confirms it if its timeout has passed
     * and it either received no challenges or won all battles.
     * If the superblock is indeed confirmed, its status in Sysethereum Contracts
     * is set to Approved if it received no challenges and SemiApproved otherwise.
     * @throws Exception
     */
    private void confirmEarliestApprovableSuperblock() throws Exception {
        Keccak256Hash bestSuperblockId = ethWrapper.getBestSuperblockId();
        Superblock chainHead = superblockChain.getChainHead();

        if (chainHead.getSuperblockId().equals(bestSuperblockId)) {
            // Contract and local db best superblocks are the same, do nothing.
            return;
        }


        Superblock toConfirm = superblockChain.getFirstDescendant(bestSuperblockId);
        if (toConfirm == null) {
            log.info("Best superblock from contracts, {}, not found in local database. Stopping.", bestSuperblockId);
            return;
        }
        Keccak256Hash toConfirmId = toConfirm.getSuperblockId();
        Superblock highestDescendant = ethWrapper.getHighestApprovableOrNewDescendant(toConfirm, bestSuperblockId);
        if (highestDescendant == null) {
            log.info("Highest descendent from contracts, {}, not found in local database. Stopping.", bestSuperblockId);
            return;
        }
        Keccak256Hash highestDescendantId = highestDescendant.getSuperblockId();


        // deal with your own superblock claims or if it has become unresponsive we allow someone else to check the claim or confirm it
        if (!isMine(highestDescendantId) && !unresponsiveTimeoutPassed(highestDescendantId)) return;

        if (ethWrapper.semiApprovedAndApprovable(toConfirm, highestDescendant)) {
            // The superblock is semi approved and it can be approved if it has enough confirmations
            log.info("Confirming semi-approved superblock {} with descendant {}", toConfirmId, highestDescendantId);
            ethWrapper.confirmClaim(toConfirmId, highestDescendantId, myAddress);
        }
        else if (ethWrapper.newAndTimeoutPassed(highestDescendantId) || ethWrapper.getInBattleAndSemiApprovable(highestDescendantId)) {
            // Either the superblock is unchallenged or it won all the battles;
            // it will get approved or semi-approved depending on the situation
            // (look at SyscoinClaimManager contract source code for more details)
            log.info("Confirming superblock {}", highestDescendantId);
            ethWrapper.checkClaimFinished(highestDescendantId, false);

        }

    }



    /* - Reacting to events - */

    private void respondToLastBlockHeaderQueries(long fromBlock, long toBlock)
            throws IOException, BlockStoreException, Exception {
        List<EthWrapper.QueryLastBlockHeaderEvent> queryBlockHeaderEvents =
                ethWrapper.getLastBlockHeaderQueries(fromBlock, toBlock);

        for (EthWrapper.QueryLastBlockHeaderEvent queryBlockHeader : queryBlockHeaderEvents) {
            if (isMine(queryBlockHeader) && (ethWrapper.getSessionChallengeState(queryBlockHeader.sessionId) == EthWrapper.ChallengeState.QueryLastBlockHeader)) {
                log.info("Last header requested for session {}. Responding now.", queryBlockHeader.sessionId);

                ethWrapper.respondLastBlockHeader(queryBlockHeader.sessionId, myAddress);
            }
        }
    }

    private void respondToMerkleRootHashesQueries(long fromBlock, long toBlock) throws IOException, Exception {
        List<EthWrapper.QueryMerkleRootHashesEvent> queryMerkleRootHashesEvents =
                ethWrapper.getMerkleRootHashesQueries(fromBlock, toBlock);

        for (EthWrapper.QueryMerkleRootHashesEvent queryMerkleRootHashes : queryMerkleRootHashesEvents) {
            if (isMine(queryMerkleRootHashes) && ethWrapper.getSessionChallengeState(queryMerkleRootHashes.sessionId) == EthWrapper.ChallengeState.QueryMerkleRootHashes) {
                Superblock superblock = ethWrapper.getSuperblockBySession(queryMerkleRootHashes.sessionId);
                if(superblock == null)
                    continue;
                log.info("Merkle root hashes requested for session {}, superblock {}. Responding now.",
                        queryMerkleRootHashes.sessionId, superblock.getSuperblockId());

                ethWrapper.respondMerkleRootHashes(queryMerkleRootHashes.sessionId,
                        superblock.getSyscoinBlockHashes(), myAddress);
            }
        }
    }

    private void logErrorBattleEvents(long fromBlock, long toBlock) throws IOException {
        List<EthWrapper.ErrorBattleEvent> errorBattleEvents = ethWrapper.getErrorBattleEvents(fromBlock, toBlock);

        for (EthWrapper.ErrorBattleEvent errorBattleEvent : errorBattleEvents) {
            if (sessionToSuperblockMap.containsKey(errorBattleEvent.sessionId)) {
                log.info("ErrorBattle. Session ID: {}, error: {}", errorBattleEvent.sessionId, errorBattleEvent.err);
            }
        }
    }


    /* ---- HELPER METHODS ---- */

    private boolean isMine(EthWrapper.QueryLastBlockHeaderEvent queryBlockHeader) {
        return queryBlockHeader.submitter.equals(myAddress);
    }

    private boolean isMine(EthWrapper.QueryMerkleRootHashesEvent queryMerkleRootHashes) {
        return queryMerkleRootHashes.submitter.equals(myAddress);
    }


    private boolean submittedUnresponsiveTimeoutPassed(Keccak256Hash superblockId) throws Exception {
        return ethWrapper.getNewEventTimestampDate(superblockId).before(getUnresponsiveTimeoutDate());
    }

    private Date getUnresponsiveTimeoutDate() throws Exception {
        float delay = ethWrapper.getSuperblockTimeout().floatValue()*(ethWrapper.getRandomizationCounter()/100.0f);
        int superblockTimeout = ethWrapper.getSuperblockTimeout().intValue() + (int)delay;
        return SuperblockUtils.getNSecondsAgo(superblockTimeout);
    }

    private boolean unresponsiveTimeoutPassed(Keccak256Hash superblockId) throws Exception {
        return submittedUnresponsiveTimeoutPassed(superblockId);
    }



    /* ---- OVERRIDE ABSTRACT METHODS ---- */

    @Override
    protected void setupFiles() throws IOException {
        setupBaseFiles();
    }

    @Override
    protected boolean arePendingTransactions() throws InterruptedException, IOException {
        return ethWrapper.arePendingTransactionsForSendSuperblocksAddress();
    }

    @Override
    protected boolean isEnabled() {
        return config.isSyscoinSuperblockSubmitterEnabled();
    }

    @Override
    protected String getLastEthBlockProcessedFilename() {
        return "SuperblockDefenderLatestEthBlockProcessedFile.dat";
    }

    @Override
    protected String getSessionToSuperblockMapFilename() {
        return "SuperblockDefenderSessionToSuperblockMap.dat";
    }

    @Override
    protected String getSuperblockToSessionsMapFilename() {
        return "SuperblockDefenderSuperblockToSessionsMap.dat";
    }

    @Override
    protected boolean isMine(EthWrapper.NewBattleEvent newBattleEvent) {
        return newBattleEvent.submitter.equals(myAddress);
    }

    @Override
    protected long getConfirmations() {
        return config.getAgentConstants().getDefenderConfirmations();
    }

    @Override
    protected void callBattleTimeouts() throws Exception {
        for (Keccak256Hash sessionId : sessionToSuperblockMap.keySet()) {
            if (ethWrapper.getChallengerHitTimeout(sessionId)) {
                log.info("Challenger hit timeout on session {}. Calling timeout.", sessionId);
                ethWrapper.timeout(sessionId, ethWrapper.getBattleManager());
            }
        }
    }

    /**
     * Removes superblocks from the data structure that keeps track of semi-approved superblocks.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    @Override
    protected void removeSuperblocks(long fromBlock, long toBlock, List<EthWrapper.SuperblockEvent> superblockEvents)
            throws Exception {
        boolean removeFromContract = false;
        for (EthWrapper.SuperblockEvent superblockEvent : superblockEvents) {
            if (superblockToSessionsMap.containsKey(superblockEvent.superblockId)) {
                sessionToSuperblockMap.keySet().removeAll(superblockToSessionsMap.get(superblockEvent.superblockId));
                superblockToSessionsMap.remove(superblockEvent.superblockId);
                removeFromContract = true;
            }

        }
        if (removeFromContract && config.isWithdrawFundsEnabled()) {
            ethWrapper.withdrawAllFundsExceptLimit(myAddress, false);
        }
    }

    @Override
    protected long getTimerTaskPeriod() {
        return config.getAgentConstants().getDefenderTimerTaskPeriod();
    }


    /* ---- BATTLE MAP METHODS ---- */

    /**
     * Removes semi-approved superblocks from superblock to session map.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    private void removeSemiApprovedDescendants(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.SuperblockEvent> semiApprovedSuperblockEvents =
                ethWrapper.getSemiApprovedSuperblocks(fromBlock, toBlock);

        for (EthWrapper.SuperblockEvent semiApprovedSuperblockEvent : semiApprovedSuperblockEvents) {
            if (superblockToSessionsMap.containsKey(semiApprovedSuperblockEvent.superblockId)) {
                sessionToSuperblockMap.keySet().removeAll(superblockToSessionsMap.get(semiApprovedSuperblockEvent.superblockId));
                superblockToSessionsMap.remove(semiApprovedSuperblockEvent.superblockId);
            }
        }
    }

    /**
     * Listens to NewSuperblock events to keep track of superblocks submitted by this client.
     * @param fromBlock
     * @param toBlock
     * @throws IOException
     */
    private void getNewSuperblocks(long fromBlock, long toBlock) throws IOException {
        List<EthWrapper.SuperblockEvent> newSuperblockEvents = ethWrapper.getNewSuperblocks(fromBlock, toBlock);

        for (EthWrapper.SuperblockEvent newSuperblockEvent : newSuperblockEvents) {
            if (isMine(newSuperblockEvent)) {
                superblockToSessionsMap.put(newSuperblockEvent.superblockId, new HashSet<>());
            }
        }
    }

    /**
     * Removes semi-approved superblocks from a data structure that keeps track of in battle superblocks.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    private void removeSemiApproved(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.SuperblockEvent> semiApprovedSuperblockEvents =
                ethWrapper.getSemiApprovedSuperblocks(fromBlock, toBlock);

        for (EthWrapper.SuperblockEvent semiApprovedSuperblockEvent : semiApprovedSuperblockEvents) {
            if (superblockToSessionsMap.containsKey(semiApprovedSuperblockEvent.superblockId)) {
                sessionToSuperblockMap.keySet().removeAll(superblockToSessionsMap.get(semiApprovedSuperblockEvent.superblockId));
                superblockToSessionsMap.remove(semiApprovedSuperblockEvent.superblockId);
            }
        }
    }

    // TODO: see if this should have some fault tolerance for battles that were erroneously not added to set
    // TODO: look into refactoring this and moving it to the base class
    /**
     * Filters battles where this defender submitted the superblock and got convicted
     * and deletes them from active battle set.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    @Override
    protected void deleteSubmitterConvictedBattles(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.SubmitterConvictedEvent> submitterConvictedEvents =
                ethWrapper.getSubmitterConvictedEvents(fromBlock, toBlock, ethWrapper.getBattleManagerGetter());

        for (EthWrapper.SubmitterConvictedEvent submitterConvictedEvent : submitterConvictedEvents) {
            if (submitterConvictedEvent.submitter.equals(myAddress)) {
                Superblock superblock = ethWrapper.getSuperblockBySession(submitterConvictedEvent.sessionId);
                if(superblock == null)
                    continue;
                log.info("Submitter convicted on session {}, superblock {}. Battle lost!",
                        submitterConvictedEvent.sessionId, superblock.getSuperblockId());
                sessionToSuperblockMap.remove(submitterConvictedEvent.sessionId);
                if (superblockToSessionsMap.containsKey(superblock.getSuperblockId())) {
                    superblockToSessionsMap.get(superblock.getSuperblockId()).remove(submitterConvictedEvent.sessionId);
                }
            }
        }
    }

    /**
     * Filters battles where this defender submitted the superblock and the challenger got convicted
     * and delete them from active battle set.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    @Override
    protected void deleteChallengerConvictedBattles(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.ChallengerConvictedEvent> challengerConvictedEvents =
                ethWrapper.getChallengerConvictedEvents(fromBlock, toBlock, ethWrapper.getBattleManagerGetter());

        for (EthWrapper.ChallengerConvictedEvent challengerConvictedEvent : challengerConvictedEvents) {
            Superblock superblock = ethWrapper.getSuperblockBySession(challengerConvictedEvent.sessionId);
            if(superblock == null)
                continue;
            if (sessionToSuperblockMap.containsKey(challengerConvictedEvent.sessionId)) {
                log.info("Challenger convicted on session {}, superblock {}. Battle won!",
                        challengerConvictedEvent.sessionId, superblock.getSuperblockId());
                sessionToSuperblockMap.remove(challengerConvictedEvent.sessionId);
            }
            if (superblockToSessionsMap.containsKey(superblock.getSuperblockId())) {
                superblockToSessionsMap.get(superblock.getSuperblockId()).remove(challengerConvictedEvent.sessionId);
            }
        }
    }

    @Override
    protected void restoreFiles() throws ClassNotFoundException, IOException {
        restore(latestEthBlockProcessed, latestEthBlockProcessedFile);
        restore(sessionToSuperblockMap, sessionToSuperblockMapFile);
        restore(superblockToSessionsMap, superblockToSessionsMapFile);
    }

    @Override
    protected void flushFiles() throws ClassNotFoundException, IOException {
        flush(latestEthBlockProcessed, latestEthBlockProcessedFile);
        flush(sessionToSuperblockMap, sessionToSuperblockMapFile);
        flush(superblockToSessionsMap, superblockToSessionsMapFile);
    }

}
