package org.dogethereum.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.AltcoinBlock;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.core.Sha256Hash;
import org.dogethereum.agents.core.dogecoin.*;
import org.dogethereum.agents.core.eth.EthWrapper;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

/**
 * Monitors the Ethereum blockchain for superblock-related events
 * and defends/confirms the ones submitted by the agent.
 * @author Catalina Juarros
 * @author Oscar Guindzberg
 */

@Service
@Slf4j(topic = "SuperblockDefenderAgent")
public class SuperblockDefenderAgent extends SuperblockBattleBaseAgent {

    public SuperblockDefenderAgent() {
        super("SuperblockDefenderAgent");
    }

    @Override
    protected void setupAgent() {
        myAddress = ethWrapper.getGeneralPurposeAndSendSuperblocksAddress();
    }

    @Override
    public long reactToEvents(long fromBlock, long toBlock) {
        try {
            respondToRequestScryptHashValidation(fromBlock, toBlock);
            respondToMerkleRootHashesQueries(fromBlock, toBlock);
            respondToBlockHeaderQueries(fromBlock, toBlock);
            sendDescendantsOfSemiApproved(fromBlock, toBlock);

            // Maintain data structures
            removeSemiApprovedDescendants(fromBlock, toBlock);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return latestEthBlockProcessed;
        }
        return toBlock;
    }

    @Override
    protected void reactToElapsedTime() {
        try {
            confirmEarliestApprovableSuperblock();
            callBattleTimeouts();
            confirmAllSemiApprovable();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /* ---- CONFIRMING/DEFENDING ---- */

    /* - Reacting to elapsed time - */

    /**
     * Finds earliest superblock that's not invalid and stored locally,
     * but not confirmed in Dogethereum Contracts, and confirms it if its timeout has passed
     * and it either received no challenges or won all battles.
     * If the superblock is indeed confirmed, its status in Dogethereum Contracts
     * is set to Approved if it received no challenges and SemiApproved otherwise.
     * @throws Exception
     */
    private void confirmEarliestApprovableSuperblock() throws Exception {
        Keccak256Hash bestSuperblockId = ethWrapper.getBestSuperblockId();
        Superblock chainHead = superblockchain.getChainHead();

        if (chainHead.getSuperblockId().equals(bestSuperblockId)) {
            // Contract and local db best superblocks are the same, do nothing.
            return;
        }

        Superblock toConfirm = superblockchain.getFirstDescendant(bestSuperblockId);

        if (toConfirm == null) {
            // TODO: see if this should raise an exception, because it's a pretty bad state
            log.info("Best superblock from contracts, {}, not found in local database. Stopping.", bestSuperblockId);
        } else {
            Keccak256Hash toConfirmId = toConfirm.getSuperblockId();

            if (!isMine(toConfirmId)) return;

            if (newAndTimeoutPassed(toConfirm) || inBattleAndSemiApprovable(toConfirm)) {
                // Either the superblock is unchallenged or it won all the battles;
                // it will get approved or semi-approved depending on the situation
                // (look at SuperblockClaims contract source code for more details)
                log.info("Confirming superblock {}", toConfirmId);
                ethWrapper.checkClaimFinished(toConfirmId, myAddress, false);
            } else if (ethWrapper.isSuperblockSemiApproved(toConfirmId)) {
                Superblock descendant = getHighestSemiApprovedDescendant(toConfirmId);
                if (descendant != null && semiApprovedAndApprovable(toConfirm, descendant)) {
                    // The superblock is semi approved and it can be approved if it has enough confirmations
                    // TODO: see if this should be done by polling semi approved superblocks with enough confirmations
                    Keccak256Hash descendantId = descendant.getSuperblockId();
                    log.info("Confirming semi-approved superblock {} with descendant {}", toConfirmId, descendantId);
                    ethWrapper.confirmClaim(toConfirmId, descendantId, myAddress);
                }
            }
        }
    }

    /**
     * Confirms superblocks which weren't challenged or for which the defender has won all the battles,
     * but whose parent might not be approved.
     * @throws Exception
     */
    private void confirmAllSemiApprovable() throws Exception {
        for (Keccak256Hash superblockId : superblockToSessionsMap.keySet()) {
            Superblock superblock = superblockchain.getSuperblock(superblockId);
            if (superblock != null && (inBattleAndSemiApprovable(superblock) || newAndTimeoutPassed(superblock))) {
                log.info("Confirming semi-approvable superblock {}", superblockId);
                ethWrapper.checkClaimFinished(superblockId, myAddress, false);
            }
        }
    }


    /* - Reacting to events - */

    private void respondToBlockHeaderQueries(long fromBlock, long toBlock)
            throws IOException, BlockStoreException, Exception {
        List<EthWrapper.QueryBlockHeaderEvent> queryBlockHeaderEvents =
                ethWrapper.getBlockHeaderQueries(fromBlock, toBlock);

        for (EthWrapper.QueryBlockHeaderEvent queryBlockHeader : queryBlockHeaderEvents) {
            if (isMine(queryBlockHeader)) {
                log.info("Header requested for Doge block {}, session {}. Responding now.",
                        queryBlockHeader.dogeBlockHash, queryBlockHeader.sessionId);

                StoredBlock dogeBlock = dogecoinWrapper.getBlock(queryBlockHeader.dogeBlockHash);
                ethWrapper.respondBlockHeader(queryBlockHeader.superblockId, queryBlockHeader.sessionId,
                        (AltcoinBlock) dogeBlock.getHeader(), myAddress);
            }
        }
    }

    private void respondToMerkleRootHashesQueries(long fromBlock, long toBlock) throws IOException, Exception {
        List<EthWrapper.QueryMerkleRootHashesEvent> queryMerkleRootHashesEvents =
                ethWrapper.getMerkleRootHashesQueries(fromBlock, toBlock);

        for (EthWrapper.QueryMerkleRootHashesEvent queryMerkleRootHashes : queryMerkleRootHashesEvents) {
            if (isMine(queryMerkleRootHashes)) {
                log.info("Merkle root hashes requested for session {}, superblock {}. Responding now.",
                        queryMerkleRootHashes.sessionId, queryMerkleRootHashes.superblockId);

                Superblock superblock = superblockchain.getSuperblock(queryMerkleRootHashes.superblockId);
                ethWrapper.respondMerkleRootHashes(queryMerkleRootHashes.superblockId, queryMerkleRootHashes.sessionId,
                        superblock.getDogeBlockHashes(), myAddress);
            }
        }
    }

    /**
     * Listens to SemiApprovedSuperblock events and proposes their direct descendants to the contracts
     * if the semi-approved superblock was proposed by this defender.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    private void sendDescendantsOfSemiApproved(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.SuperblockEvent> semiApprovedSuperblockEvents =
                ethWrapper.getSemiApprovedSuperblocks(fromBlock, toBlock);

        for (EthWrapper.SuperblockEvent semiApprovedSuperblockEvent : semiApprovedSuperblockEvents) {
            Superblock descendant = superblockchain.getFirstDescendant(semiApprovedSuperblockEvent.superblockId);
            if (descendant != null) {
                log.info("Found superblock {}, descendant of semi-approved {}. Sending it now.",
                        descendant.getSuperblockId(), semiApprovedSuperblockEvent.superblockId);
                ethWrapper.sendStoreSuperblock(descendant, myAddress);
                superblockToSessionsMap.put(descendant.getSuperblockId(), new HashSet<>());
            }
        }
    }

    private void respondToRequestScryptHashValidation(long fromBlock, long toBlock) throws IOException, Exception {
        List<EthWrapper.RequestScryptHashValidationEvent> requestScryptHashValidationEvents =
                ethWrapper.getRequestScryptHashValidation(fromBlock, toBlock);

        for (EthWrapper.RequestScryptHashValidationEvent requestScryptHashValidationEvent : requestScryptHashValidationEvents) {
            if (isMine(requestScryptHashValidationEvent)) {
                Sha256Hash dogeBlockHash =
                        Sha256Hash.wrapReversed(Sha256Hash.hashTwice(requestScryptHashValidationEvent.blockHeader));
                log.info("Request scrypt hash verification for session {}, superblock {}, " +
                                "block {}, scrypt hash {}. Responding now.",
                        requestScryptHashValidationEvent.sessionId, requestScryptHashValidationEvent.superblockId,
                        dogeBlockHash, requestScryptHashValidationEvent.blockScryptHash);
                ethWrapper.checkScrypt(requestScryptHashValidationEvent.sessionId,
                        requestScryptHashValidationEvent.superblockId,
                        requestScryptHashValidationEvent.proposalId,
                        requestScryptHashValidationEvent.blockHeader,
                        requestScryptHashValidationEvent.blockScryptHash);
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

    private boolean isMine(EthWrapper.QueryBlockHeaderEvent queryBlockHeader) {
        return queryBlockHeader.submitter.equals(myAddress);
    }

    private boolean isMine(EthWrapper.QueryMerkleRootHashesEvent queryMerkleRootHashes) {
        return queryMerkleRootHashes.submitter.equals(myAddress);
    }

    private boolean isMine(EthWrapper.RequestScryptHashValidationEvent requestScryptHashValidation) {
        return requestScryptHashValidation.submitter.equals(myAddress);
    }

    private boolean submittedTimeoutPassed(Keccak256Hash superblockId) throws Exception {
        return ethWrapper.getNewEventTimestampDate(superblockId).before(getTimeoutDate());
    }

    private Date getTimeoutDate() throws Exception {
        int superblockTimeout = ethWrapper.getSuperblockTimeout().intValue();
        return SuperblockUtils.getNSecondsAgo(superblockTimeout);
    }

    private boolean challengeTimeoutPassed(Keccak256Hash superblockId) throws Exception {
        return ethWrapper.getClaimChallengeTimeoutDate(superblockId).before(getTimeoutDate());
    }

    private boolean newAndTimeoutPassed(Superblock superblock) throws Exception {
        Keccak256Hash superblockId = superblock.getSuperblockId();
        return (ethWrapper.isSuperblockNew(superblockId) && submittedTimeoutPassed(superblockId));
    }

    /**
     * Checks if a given superblock is in battle and meets the necessary and sufficient conditions
     * for being semi-approved when calling checkClaimFinished.
     * @param superblock Superblock to be confirmed.
     * @return True if the superblock can be safely semi-approved, false otherwise.
     * @throws Exception
     */
    private boolean inBattleAndSemiApprovable(Superblock superblock) throws Exception {
        Keccak256Hash superblockId = superblock.getSuperblockId();
        return ethWrapper.getInBattleAndSemiApprovable(superblockId);
    }

    /**
     * Checks if a superblock is semi-approved and has enough confirmations, i.e. semi-approved descendants.
     * To be used after finding a descendant with getHighestSemiApprovedDescendant.
     * @param superblock Superblock to be confirmed.
     * @param descendant Highest semi-approved descendant of superblock to be confirmed.
     * @return True if the superblock can be safely approved, false otherwise.
     * @throws Exception
     */
    private boolean semiApprovedAndApprovable(Superblock superblock, Superblock descendant) throws Exception {
        Keccak256Hash superblockId = superblock.getSuperblockId();
        Keccak256Hash descendantId = descendant.getSuperblockId();
        return (descendant.getSuperblockHeight() - superblock.getSuperblockHeight() >=
            ethWrapper.getSuperblockConfirmations() &&
            ethWrapper.isSuperblockSemiApproved(descendantId) &&
            ethWrapper.isSuperblockSemiApproved(superblockId));
    }

    /**
     * Helper method for confirming a semi-approved superblock.
     * Finds the highest semi-approved superblock in the main chain that comes after a given superblock.
     * @param superblockId Superblock to be confirmed.
     * @return Highest superblock in main chain that's newer than the given superblock
     *         if such a superblock exists, null otherwise (i.e. given superblock isn't in main chain
     *         or has no semi-approved descendants).
     * @throws BlockStoreException
     * @throws IOException
     * @throws Exception
     */
    private Superblock getHighestSemiApprovedDescendant(Keccak256Hash superblockId)
            throws BlockStoreException, IOException, Exception {
        Superblock highest = superblockchain.getChainHead();

        // Find highest semi-approved descendant
        while (highest != null && !ethWrapper.isSuperblockSemiApproved(highest.getSuperblockId())) {
            highest = superblockchain.getParent(highest);
            if (highest.getSuperblockId().equals(superblockId)) {
                // No semi-approved descendants found
                return null;
            }
        }

        return highest;
    }


    /* ---- OVERRIDE ABSTRACT METHODS ---- */

    @Override
    protected boolean arePendingTransactions() throws IOException {
        return ethWrapper.arePendingTransactionsForSendSuperblocksAddress();
    }

    @Override
    protected boolean isEnabled() {
        return config.isDogeSuperblockSubmitterEnabled();
    }

    @Override
    protected String getLatestEthBlockProcessedFilename() {
        return "SuperblockDefenderLatestEthBlockProcessed.dat";
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
        for (EthWrapper.SuperblockEvent superblockEvent : superblockEvents) {
            if (superblockToSessionsMap.containsKey(superblockEvent.superblockId)) {
                superblockToSessionsMap.remove(superblockEvent.superblockId);
            }

            if (config.isWithdrawFundsEnabled()) {
                ethWrapper.withdrawAllFundsExceptLimit(myAddress, false);
            }
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
                superblockToSessionsMap.remove(semiApprovedSuperblockEvent.superblockId);
            }
        }
    }

    /**
     * Listens to NewSuperblock events to keep track of superblocks submitted by this agent.
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
                ethWrapper.getSubmitterConvictedEvents(fromBlock, toBlock, ethWrapper.getBattleManager());

        for (EthWrapper.SubmitterConvictedEvent submitterConvictedEvent : submitterConvictedEvents) {
            if (submitterConvictedEvent.submitter.equals(myAddress)) {
                log.info("Submitter convicted on session {}, superblock {}. Battle lost!",
                        submitterConvictedEvent.sessionId, submitterConvictedEvent.superblockId);
                sessionToSuperblockMap.remove(submitterConvictedEvent.sessionId);
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
                ethWrapper.getChallengerConvictedEvents(fromBlock, toBlock, ethWrapper.getBattleManager());

        for (EthWrapper.ChallengerConvictedEvent challengerConvictedEvent : challengerConvictedEvents) {
            if (sessionToSuperblockMap.containsKey(challengerConvictedEvent.sessionId)) {
                log.info("Challenger convicted on session {}, superblock {}. Battle won!",
                        challengerConvictedEvent.sessionId, challengerConvictedEvent.superblockId);
                sessionToSuperblockMap.remove(challengerConvictedEvent.sessionId);
            }
        }
    }

}
