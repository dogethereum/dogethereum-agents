package org.dogethereum.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.AltcoinBlock;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.store.BlockStoreException;
import org.dogethereum.agents.core.dogecoin.*;
import org.dogethereum.agents.core.eth.EthWrapper;
import org.spongycastle.util.encoders.Hex;
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
            respondToBlockHeaderQueries(fromBlock, toBlock);
            respondToMerkleRootHashesQueries(fromBlock, toBlock);
            logErrorBattleEvents(fromBlock, toBlock);
            deleteFinishedBattles(fromBlock, toBlock);
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

            if (newAndTimeoutPassed(toConfirm) || inBattleAndSemiApprovable(toConfirm)) {
                log.info("Confirming superblock {}", toConfirmId);
                ethWrapper.checkClaimFinished(toConfirmId);
            } else if (semiApprovedAndApprovable(toConfirm)) {
                Keccak256Hash descendantId = superblockChain.getFirstDescendant(toConfirmId).getSuperblockId();
                log.info("Confirming semi-approved superblock {}", toConfirmId);
                ethWrapper.confirmClaim(toConfirmId, descendantId);
            }
        }
    }


    /* - Reacting to events - */

    // TODO: document
    private void respondToBlockHeaderQueries(long fromBlock, long toBlock)
            throws IOException, BlockStoreException, Exception {
        List<EthWrapper.QueryBlockHeaderEvent> queryBlockHeaderEvents =
                ethWrapper.getBlockHeaderQueries(fromBlock, toBlock);

        for (EthWrapper.QueryBlockHeaderEvent queryBlockHeader : queryBlockHeaderEvents) {
            if (isMine(queryBlockHeader)) {
                log.info("Header requested for Doge block {}, session {}. Responding now.",
                        queryBlockHeader.dogeBlockHash, queryBlockHeader.sessionId);
                List<Sha256Hash> allDogeBlockHashes =
                        superblockChain.getSuperblock(queryBlockHeader.superblockId).getDogeBlockHashes();
                log.debug("Superblock hashes: {}", allDogeBlockHashes);

                StoredBlock dogeBlock = dogecoinWrapper.getBlock(queryBlockHeader.dogeBlockHash);
                ethWrapper.respondBlockHeader(queryBlockHeader.superblockId, queryBlockHeader.sessionId,
                        (AltcoinBlock) dogeBlock.getHeader());
            }
        }
    }

    private void respondToMerkleRootHashesQueries(long fromBlock, long toBlock) throws IOException, Exception {
        List<EthWrapper.QueryMerkleRootHashesEvent> queryMerkleRootHashesEvents =
                ethWrapper.getMerkleRootHashesQueries(fromBlock, toBlock);

        for (EthWrapper.QueryMerkleRootHashesEvent queryMerkleRootHashes : queryMerkleRootHashesEvents) {
            if (isMine(queryMerkleRootHashes)) {
                log.info("Merkle root hashes requested for session {}. Responding now.",
                        queryMerkleRootHashes.sessionId);

                Superblock superblock = superblockChain.getSuperblock(queryMerkleRootHashes.superblockId);
                ethWrapper.respondMerkleRootHashes(queryMerkleRootHashes.superblockId, queryMerkleRootHashes.sessionId,
                        superblock.getDogeBlockHashes());
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
            Superblock descendant = superblockChain.getFirstDescendant(semiApprovedSuperblockEvent.superblockId);
            if (descendant != null) {
                log.info("Found superblock {}, descendant of semi-approved {}. Sending it now.",
                        descendant.getSuperblockId(), semiApprovedSuperblockEvent.superblockId);
                ethWrapper.sendStoreSuperblock(descendant);
            }
        }
    }

    private void logErrorBattleEvents(long fromBlock, long toBlock) throws IOException {
        List<EthWrapper.ErrorBattleEvent> errorBattleEvents = ethWrapper.getErrorBattleEvents(fromBlock, toBlock);

        for (EthWrapper.ErrorBattleEvent errorBattleEvent : errorBattleEvents) {
            if (battleSet.contains(errorBattleEvent.sessionId)) {
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
            return false;
        } else {
            Keccak256Hash descendantId = descendant.getSuperblockId();
            return (ethWrapper.isSuperblockSemiApproved(descendantId) &&
                    ethWrapper.isSuperblockSemiApproved(superblockId));
        }
    }



    /* ---- OVERRIDE ABSTRACT METHODS ---- */

    @Override
    protected boolean isEnabled() {
        return config.isDogeBlockSubmitterEnabled();
    }

    @Override
    protected String getLastEthBlockProcessedFilename() {
        return "SuperblockDefenderLatestEthBlockProcessedFile.dat";
    }

    @Override
    protected String getBattleSetFilename() {
        return "SuperblockDefenderBattleSet.dat";
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
        for (Keccak256Hash sessionId : battleSet) {
            if (ethWrapper.getChallengerHitTimeout(sessionId)) {
                log.info("Challenger hit timeout on session {}. Calling timeout.", sessionId);
                ethWrapper.timeout(sessionId, ethWrapper.getClaimManager());
            }
        }
    }

    @Override
    protected long getTimerTaskPeriod() {
        return config.getAgentConstants().getDefenderTimerTaskPeriod();
    }

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

        if (!submitterConvictedEvents.isEmpty()) {
            log.debug("Battles before deletion: {}", battleSet);
        }

        for (EthWrapper.SubmitterConvictedEvent submitterConvictedEvent : submitterConvictedEvents) {
            if (submitterConvictedEvent.submitter.equals(myAddress)) {
                log.info("Submitter convicted on session {}, superblock {}. Battle lost!",
                        submitterConvictedEvent.sessionId, submitterConvictedEvent.superblockId);
                battleSet.remove(submitterConvictedEvent.sessionId);
                // TODO: see if this should have some fault tolerance for battles that were erroneously not added to set
            }
        }

        if (!submitterConvictedEvents.isEmpty()) {
            log.debug("Battles after deletion: {}", battleSet);
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

        if (!challengerConvictedEvents.isEmpty()) {
            log.debug("Battles before deletion: {}", battleSet);
        }

        for (EthWrapper.ChallengerConvictedEvent challengerConvictedEvent : challengerConvictedEvents) {
            if (battleSet.contains(challengerConvictedEvent.sessionId)) {
                log.info("Challenger convicted on session {}, superblock {}. Battle won!",
                        challengerConvictedEvent.sessionId, challengerConvictedEvent.superblockId);
                battleSet.remove(challengerConvictedEvent.sessionId);
            }
        }

        if (!challengerConvictedEvents.isEmpty()) {
            log.debug("Battles after deletion: {}", battleSet);
        }
    }
}
