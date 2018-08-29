package org.dogethereum.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.AltcoinBlock;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.core.Sha256Hash;
import org.dogethereum.agents.contract.DogeClaimManagerExtended;
import org.dogethereum.agents.core.dogecoin.*;
import org.dogethereum.agents.core.eth.EthWrapper;
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

    private static long ETH_REQUIRED_CONFIRMATIONS = 5;

    protected DogeClaimManagerExtended claimManager;

    public SuperblockDefenderClient() {
        super("Superblock defender client");
    }

    @Override
    protected void setupClient() {
        myAddress = ethWrapper.getGeneralPurposeAndSendSuperblocksAddress();
        claimManager = ethWrapper.getClaimManager();
    }

    @Override
    public long reactToEvents(long fromBlock, long toBlock) {
        try {
            respondToRequestScryptHashValidation(fromBlock, toBlock);
            respondToMerkleRootHashesQueries(fromBlock, toBlock);
            respondToBlockHeaderQueries(fromBlock, toBlock);
            sendDescendantsOfSemiApproved(fromBlock, toBlock);

            logErrorBattleEvents(fromBlock, toBlock);
            logApproved(fromBlock, toBlock);
            logSemiApproved(fromBlock, toBlock);
            logInvalid(fromBlock, toBlock);
            logErrorClaimEvents(fromBlock, toBlock);

            // Maintain data structures
            deleteFinishedBattles(fromBlock, toBlock);
            removeSemiApproved(fromBlock, toBlock);
            removeInvalid(fromBlock, toBlock);
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
     * Find earliest superblock that's unchallenged and stored locally,
     * but not confirmed in Dogethereum Contracts, and confirm it if its timeout has passed
     * and it either received no challenges or won all battles.
     * If the superblock is indeed confirmed, its status in Dogethereum Contracts
     * will be set to Approved if it received no challenges and SemiApproved otherwise.
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
            // TODO: see if this should raise an exception, because it's a pretty bad state
            log.info("Best superblock from contracts, {}, not found in local database. Stopping.", bestSuperblockId);
        } else {
            Keccak256Hash toConfirmId = toConfirm.getSuperblockId();

            if (!isMine(toConfirmId)) return;

            if (newAndTimeoutPassed(toConfirm) || inBattleAndSemiApprovable(toConfirm)) {
                log.info("Confirming superblock {}", toConfirmId);
                ethWrapper.checkClaimFinished(toConfirmId);
            } else if (semiApprovedAndApprovable(toConfirm)) {
                Keccak256Hash descendantId = getHighestApprovableDescendant(toConfirmId);
                if (descendantId != null) {
                    log.info("Confirming semi-approved superblock {}", toConfirmId);
                    ethWrapper.confirmClaim(toConfirmId, descendantId);
                }
            }
        }
    }

    /**
     * Confirm superblocks for which the defender has won all the battles, but whose parent might not be approved.
     * @throws Exception
     */
    private void confirmAllSemiApprovable() throws Exception {
        for (Keccak256Hash superblockId : superblockToSessionsMap.keySet()) {
            Superblock superblock = superblockChain.getSuperblock(superblockId);
            if (superblock != null && inBattleAndSemiApprovable(superblock)) {
                log.info("Confirming semi-approvable superblock {}", superblockId);
                ethWrapper.checkClaimFinished(superblockId);
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
                if (dogeBlock == null) {
                    dogeBlock = dogecoinWrapper.getFakeStoredDogeBlock(queryBlockHeader.dogeBlockHash);
                }
                ethWrapper.respondBlockHeader(queryBlockHeader.superblockId, queryBlockHeader.sessionId,
                        (AltcoinBlock) dogeBlock.getHeader(), claimManager);
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

                Superblock superblock = superblockChain.getSuperblock(queryMerkleRootHashes.superblockId);
                ethWrapper.respondMerkleRootHashes(queryMerkleRootHashes.superblockId, queryMerkleRootHashes.sessionId,
                        superblock.getDogeBlockHashes(), claimManager);
            }
        }
    }

    /**
     * Listen to SemiApprovedSuperblock events and propose their direct descendants to the contracts
     * if the semi-approved superblock was proposed by this defender.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    private void sendDescendantsOfSemiApproved(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.SuperblockEvent> semiApprovedSuperblockEvents =
                ethWrapper.getSemiApprovedSuperblocks(fromBlock, toBlock);

        for (EthWrapper.SuperblockEvent semiApprovedSuperblockEvent : semiApprovedSuperblockEvents) {
            if (isMine(semiApprovedSuperblockEvent)) {
                Superblock descendant = superblockChain.getFirstDescendant(semiApprovedSuperblockEvent.superblockId);
                if (descendant != null) {
                    log.info("Found superblock {}, descendant of semi-approved {}. Sending it now.",
                            descendant.getSuperblockId(), semiApprovedSuperblockEvent.superblockId);
                    ethWrapper.sendStoreSuperblock(descendant, claimManager);
                }
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
     * Check if a given superblock is in battle and meets the necessary and sufficient conditions
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
     * Check if a superblock is semi-approved and has a semi-approved descendant.
     * @param superblock Superblock to be confirmed.
     * @return True if the superblock can be safely approved, false otherwise.
     * @throws Exception
     */
    private boolean semiApprovedAndApprovable(Superblock superblock) throws Exception {
        Keccak256Hash superblockId = superblock.getSuperblockId();
        Superblock descendant = superblockChain.getFirstDescendant(superblockId);
        if (descendant == null) {
            log.debug("No descendant found for superblock {}", superblock.getSuperblockId());
            return false;
        } else {
            Keccak256Hash descendantId = descendant.getSuperblockId();
            return (ethWrapper.isSuperblockSemiApproved(descendantId) &&
                    ethWrapper.isSuperblockSemiApproved(superblockId));
        }
    }

    private Keccak256Hash getHighestApprovableDescendant(Keccak256Hash superblockId)
            throws BlockStoreException, IOException, Exception {
        Superblock highest = superblockChain.getChainHead();

        // Find highest semi-approved unchallenged descendant
        while (!semiApprovedAndUnchallenged(highest)) {
            highest = superblockChain.getParent(highest);
            if (highest.getSuperblockId().equals(superblockId)) {
                // No semi-approved unchallenged descendants found
                return null;
            }
        }

        return highest.getSuperblockId();

//        // Find highest semi-approved unchallenged descendant with no challenged ancestors
//        Superblock ancestor = superblockChain.getParent(highest);
//        while (ancestor != null && !ancestor.getSuperblockId().equals(superblockId) &&
//                !highest.getSuperblockId().equals(superblockId)) {
//            if (ethWrapper.isChallenged(ancestor)) {
//                highest = superblockChain.getParent(ancestor); // skip challenged superblock
//                ancestor = superblockChain.getParent(highest); // given the context, highest cannot be null
//            } else {
//                ancestor = superblockChain.getParent(ancestor);
//            }
//        }
//
//        if (highest.getSuperblockHeight() > superblockChain.getSuperblock(superblockId).getSuperblockHeight()) {
//            return highest.getSuperblockId();
//        } else {
//            // No unchallenged descendant
//            return null;
//        }
    }

    private boolean semiApprovedAndUnchallenged(Superblock superblock) throws Exception {
        Keccak256Hash superblockId = superblock.getSuperblockId();
        return (ethWrapper.isSuperblockSemiApproved(superblockId) &&
                !ethWrapper.isChallenged(superblock));
    }

    private void logApproved(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.SuperblockEvent> semiApprovedSuperblocks =
                ethWrapper.getApprovedSuperblocks(fromBlock, toBlock);

        for (EthWrapper.SuperblockEvent superblockEvent : semiApprovedSuperblocks) {
//            if (isMine(superblockEvent)) {
                log.debug("Approved: {}", superblockEvent.superblockId);
//            }
        }
    }

    private void logSemiApproved(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.SuperblockEvent> semiApprovedSuperblocks =
                ethWrapper.getSemiApprovedSuperblocks(fromBlock, toBlock);

        for (EthWrapper.SuperblockEvent superblockEvent : semiApprovedSuperblocks) {
//            if (isMine(superblockEvent)) {
                log.debug("Semi-approved: {}", superblockEvent.superblockId);
//            }
        }
    }

    private void logInvalid(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.SuperblockEvent> semiApprovedSuperblocks =
                ethWrapper.getInvalidSuperblocks(fromBlock, toBlock);

        for (EthWrapper.SuperblockEvent superblockEvent : semiApprovedSuperblocks) {
//            if (isMine(superblockEvent)) {
                log.debug("Invalid: {}", superblockEvent.superblockId);
//            }
        }
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
                ethWrapper.timeout(sessionId, ethWrapper.getClaimManager());
            }
        }
    }

    /**
     * Remove invalidated superblocks from the data structure that keeps track of in battle superblocks.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    @Override
    protected void removeInvalid(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.SuperblockEvent> invalidSuperblockEvents = ethWrapper.getInvalidSuperblocks(fromBlock, toBlock);
        for (EthWrapper.SuperblockEvent superblockEvent : invalidSuperblockEvents) {
            Keccak256Hash superblockId = superblockEvent.superblockId;
            if (superblockToSessionsMap.containsKey(superblockId)) {
                superblockToSessionsMap.remove(superblockId);
            }
        }
    }

    @Override
    protected long getTimerTaskPeriod() {
        return config.getAgentConstants().getDefenderTimerTaskPeriod();
    }

    // TODO: see if this should have some fault tolerance for battles that were erroneously not added to set
    /**
     * Filter battles where this defender submitted the superblock and got convicted
     * and delete them from active battle set.
     * @param fromBlock
     * @param toBlock
     * @return
     * @throws Exception
     */
    @Override
    protected void deleteSubmitterConvictedBattles(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.SubmitterConvictedEvent> submitterConvictedEvents =
                ethWrapper.getSubmitterConvictedEvents(fromBlock, toBlock, ethWrapper.getClaimManager());

        for (EthWrapper.SubmitterConvictedEvent submitterConvictedEvent : submitterConvictedEvents) {
            if (submitterConvictedEvent.submitter.equals(myAddress)) {
                log.info("Submitter convicted on session {}, superblock {}. Battle lost!",
                        submitterConvictedEvent.sessionId, submitterConvictedEvent.superblockId);
                sessionToSuperblockMap.remove(submitterConvictedEvent.sessionId);
            }
        }
    }

    /**
     * Filter battles where this defender submitted the superblock and the challenger got convicted
     * and delete them from active battle set.
     * @param fromBlock
     * @param toBlock
     * @return
     * @throws Exception
     */
    @Override
    protected void deleteChallengerConvictedBattles(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.ChallengerConvictedEvent> challengerConvictedEvents =
                ethWrapper.getChallengerConvictedEvents(fromBlock, toBlock, ethWrapper.getClaimManager());

        for (EthWrapper.ChallengerConvictedEvent challengerConvictedEvent : challengerConvictedEvents) {
            if (sessionToSuperblockMap.containsKey(challengerConvictedEvent.sessionId)) {
                log.info("Challenger convicted on session {}, superblock {}. Battle won!",
                        challengerConvictedEvent.sessionId, challengerConvictedEvent.superblockId);
                sessionToSuperblockMap.remove(challengerConvictedEvent.sessionId);
            }
        }
    }

    @Override
    protected void restoreFiles() throws Exception {
        restoreLatestEthBlockProcessed();
        restoreSessionToSuperblockMap();
        restoreSuperblockToSessionsMap();
    }

    @Override
    protected void flushFiles() throws Exception {
        flushLatestEthBlockProcessed();
        flushSessionToSuperblockMap();
        flushSuperblockToSessionsMap();
    }
}
