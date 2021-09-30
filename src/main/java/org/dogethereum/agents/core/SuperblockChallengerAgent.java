package org.dogethereum.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Sha256Hash;
import org.dogethereum.agents.core.eth.EthWrapper;
import org.dogethereum.agents.core.dogecoin.Keccak256Hash;
import org.dogethereum.agents.core.dogecoin.Superblock;
import org.libdohj.core.Utils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

/**
 * Monitors the Ethereum blockchain for superblock-related events
 * and challenges invalid submissions.
 * @author Catalina Juarros
 * @author Ismael Bejarano
 * @author Oscar Guindzberg
 */

@Service
@Slf4j(topic = "SuperblockChallengerAgent")
public class SuperblockChallengerAgent extends SuperblockBattleBaseAgent {

    private HashSet<Keccak256Hash> semiApprovedSet;
    private File semiApprovedSetFile;

    public SuperblockChallengerAgent() {
        super("SuperblockChallengerAgent");
    }

    @Override
    protected void setupAgent() {
        myAddress = ethWrapper.getDogeSuperblockChallengerAddress();
    }

    @Override
    public long reactToEvents(long fromBlock, long toBlock) {
        try {
//            challengeEverything(fromBlock, toBlock);
            validateNewSuperblocks(fromBlock, toBlock);
            respondToNewBattles(fromBlock, toBlock);
            respondToMerkleRootHashesEventResponses(fromBlock, toBlock);
            respondToBlockHeaderEventResponses(fromBlock, toBlock);
            respondToResolveScryptHashValidation(fromBlock, toBlock);

            // Maintain data structures
            getSemiApproved(fromBlock, toBlock);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return latestEthBlockProcessed;
        }
        return toBlock;
    }

    @Override
    protected void reactToElapsedTime() {
        try {
            callBattleTimeouts();
            invalidateLoserSuperblocks();
            invalidateNonMainChainSuperblocks();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /* ---- CHALLENGING ---- */

    /* - Reacting to elapsed time */

    private void invalidateNonMainChainSuperblocks() throws Exception {
        for (Keccak256Hash superblockId : semiApprovedSet) {
            long semiApprovedHeight = ethWrapper.getSuperblockHeight(superblockId).longValue();
            Superblock mainChainSuperblock = superblockchain.getSuperblockByHeight(semiApprovedHeight);
            if (mainChainSuperblock != null) {
                long confirmations = ethWrapper.getSuperblockConfirmations();
                if (!mainChainSuperblock.getSuperblockId().equals(superblockId) &&
                        ethWrapper.getChainHeight().longValue() >= semiApprovedHeight + confirmations) {
                    log.info("Semi-approved superblock {} not found in main chain. Invalidating.", superblockId);
                    ethWrapper.rejectClaim(superblockId, myAddress);
                }
            }
        }
    }

    private void invalidateLoserSuperblocks() throws Exception {
        for (Keccak256Hash superblockId : superblockToSessionsMap.keySet()) {
            if (ethWrapper.getClaimInvalid(superblockId)) {
                log.info("Superblock {} lost a battle. Invalidating.", superblockId);
                ethWrapper.checkClaimFinished(superblockId, myAddress, true);
            }
        }
    }


    /* - Reacting to events */

    /**
     * Starts challenges for all new superblocks that aren't in the challenger's local chain.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    private void validateNewSuperblocks(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.SuperblockEvent> newSuperblockEvents = ethWrapper.getNewSuperblocks(fromBlock, toBlock);

        List<Keccak256Hash> toChallenge = new ArrayList<>();
        for (EthWrapper.SuperblockEvent newSuperblock : newSuperblockEvents) {
            log.info("NewSuperblock {}. Validating...", newSuperblock.superblockId);

            Superblock superblock = superblockchain.getSuperblock(newSuperblock.superblockId);
            if (superblock == null) {
                BigInteger height = ethWrapper.getSuperblockHeight(newSuperblock.superblockId);
                Superblock localSuperblock = superblockchain.getSuperblockByHeight(height.longValue());
                if (localSuperblock == null) {
                    //FIXME: Local superbockchain might be out of sync
                    log.info("Superblock {} not present in our superblock chain", newSuperblock.superblockId);
                } else {
                    log.info("Superblock {} at height {} is replaced by {} in our superblock chain",
                            newSuperblock.superblockId,
                            height,
                            localSuperblock.getSuperblockId());
                    toChallenge.add(newSuperblock.superblockId);
                }
            } else {
                log.info("... superblock present in our superblock chain");
            }
        }

        for (Keccak256Hash superblockId : toChallenge) {
            ethWrapper.challengeSuperblock(superblockId, myAddress);
        }
    }

    // For testing only. To be eventually deleted.
    private void challengeEverything(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.SuperblockEvent> newSuperblockEvents = ethWrapper.getNewSuperblocks(fromBlock, toBlock);
        for (EthWrapper.SuperblockEvent superblockEvent : newSuperblockEvents) {
            ethWrapper.challengeSuperblock(superblockEvent.superblockId, myAddress);
        }
    }

    /**
     * Queries Merkle root hashes for all new battle events that the challenger is taking part in.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    private void respondToNewBattles(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.NewBattleEvent> newBattleEvents = ethWrapper.getNewBattleEvents(fromBlock, toBlock);

        for (EthWrapper.NewBattleEvent newBattleEvent : newBattleEvents) {
            if (isMine(newBattleEvent)) {
                ethWrapper.queryMerkleRootHashes(newBattleEvent.superblockId, newBattleEvent.sessionId, myAddress);
                sessionToSuperblockMap.put(newBattleEvent.sessionId, newBattleEvent.superblockId);
                addToSuperblockToSessionsMap(newBattleEvent.sessionId, newBattleEvent.superblockId);
            }
        }
    }

    /**
     * Queries first block header for battles that the challenger is taking part in.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    private void respondToMerkleRootHashesEventResponses(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.RespondMerkleRootHashesEvent> defenderResponses =
                ethWrapper.getRespondMerkleRootHashesEvents(fromBlock, toBlock);

        for (EthWrapper.RespondMerkleRootHashesEvent defenderResponse : defenderResponses) {
            if (isMine(defenderResponse)) {
                startBlockHeaderQueries(defenderResponse);
            }
        }
    }

    /**
     * For all block header event responses corresponding to battles that the challenger is taking part in,
     * queries the next block header if there are more to go; otherwise, end the battle.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    private void respondToBlockHeaderEventResponses(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.RespondBlockHeaderEvent> defenderResponses =
                ethWrapper.getRespondBlockHeaderEvents(fromBlock, toBlock);

        for (EthWrapper.RespondBlockHeaderEvent defenderResponse : defenderResponses) {
            if (isMine(defenderResponse)) {
                reactToBlockHeaderResponse(defenderResponse);
            }
        }
    }

    /**
     * Queries header of the first Doge block hash in a certain superblock that the challenger is battling.
     * If it was empty, just verifies it.
     * @param defenderResponse Merkle root hashes response from the defender.
     * @throws Exception
     */
    private void startBlockHeaderQueries(EthWrapper.RespondMerkleRootHashesEvent defenderResponse) throws Exception {
        Keccak256Hash superblockId = defenderResponse.superblockId;
        List<Sha256Hash> dogeBlockHashes = defenderResponse.blockHashes;
        log.info("Starting block header queries for superblock {}", superblockId);

        if (!dogeBlockHashes.isEmpty()) {
            log.info("Querying first block header for superblock {}", superblockId);
            ethWrapper.queryBlockHeader(superblockId, defenderResponse.sessionId, dogeBlockHashes.get(0),
                    myAddress);
        } else {
            log.info("Merkle root hashes response for superblock {} is empty. Verifying it now.", superblockId);
            ethWrapper.verifySuperblock(defenderResponse.sessionId, ethWrapper.getBattleManagerForChallenges());
        }
    }

    /**
     * Queries the header for the next hash in the superblock's list of Doge hashes if there is one,
     * ends the battle by verifying the superblock if Doge block hash was the last one.
     * @param defenderResponse Doge block hash response from defender.
     * @throws Exception
     */
    private void reactToBlockHeaderResponse(EthWrapper.RespondBlockHeaderEvent defenderResponse) throws Exception {
        Sha256Hash dogeBlockHash = Sha256Hash.wrapReversed(Sha256Hash.hashTwice(defenderResponse.blockHeader));
        Sha256Hash proposedBlockScryptHash = Sha256Hash.wrap(defenderResponse.blockScryptHash);
        Sha256Hash scryptBlockHash = Sha256Hash.wrap(Utils.scryptDigest(defenderResponse.powBlockHeader));

        if (!verifyScryptHashAndSendValidationRequest(defenderResponse.sessionId, defenderResponse.superblockId,
                dogeBlockHash, proposedBlockScryptHash, scryptBlockHash)) {
            queryNextBlockHeaderOrVerifySuperblock(defenderResponse.sessionId, defenderResponse.superblockId,
                    dogeBlockHash);
        }
    }

    /**
     * Queries the next block header or end battle verifying the superblock.
     * @param sessionId Battle's session ID
     * @param superblockId Superblock ID
     * @param dogeBlockHash Doge block hash
     * @param proposedBlockScryptHash Proposes scrypt hash
     * @param scryptBlockHash Calculated scrypt hash
     */
    private boolean verifyScryptHashAndSendValidationRequest(Keccak256Hash sessionId, Keccak256Hash superblockId,
                                                             Sha256Hash dogeBlockHash, Sha256Hash proposedBlockScryptHash,
                                                             Sha256Hash scryptBlockHash) throws Exception {
        if (!proposedBlockScryptHash.equals(scryptBlockHash)) {
            log.info("Sending request to validate scrypt hash session {}, superblock {}, scrypt hash {}, block {}",
                    sessionId, superblockId, proposedBlockScryptHash, dogeBlockHash);
            ethWrapper.requestScryptHashValidation(superblockId, sessionId, dogeBlockHash, myAddress);
            return true;
        }
        return false;
    }

    /**
     * Queries the next block header or end battle verifying the superblock.
     * @param sessionId Battle's session ID
     * @param superblockId Superblock ID
     * @param dogeBlockHash Last Doge block hash requested
     * @throws Exception
     */
    private void queryNextBlockHeaderOrVerifySuperblock(Keccak256Hash sessionId, Keccak256Hash superblockId,
                                                        Sha256Hash dogeBlockHash) throws Exception {
        List<Sha256Hash> sessionDogeBlockHashes = ethWrapper.getDogeBlockHashes(sessionId);
        Sha256Hash nextDogeBlockHash = getNextHashToQuery(dogeBlockHash, sessionDogeBlockHashes);

        if (nextDogeBlockHash != null) {
            // not last hash
            log.info("Querying block header {}", nextDogeBlockHash);
            ethWrapper.queryBlockHeader(superblockId, sessionId, nextDogeBlockHash, myAddress);
        } else {
            // last hash; end battle
            log.info("All block hashes for superblock {} have been received. Verifying it now.", superblockId);
            ethWrapper.verifySuperblock(sessionId, ethWrapper.getBattleManagerForChallenges());
        }
    }

    /**
     * For scrypt hash validation events corresponding to battles that the challenger is taking part in,
     * queries the next block header or finish the battle.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    private void respondToResolveScryptHashValidation(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.ResolvedScryptHashValidationEvent> defenderResponses =
                ethWrapper.getResolvedScryptHashValidation(fromBlock, toBlock);

        for (EthWrapper.ResolvedScryptHashValidationEvent defenderResponse : defenderResponses) {
            if (isMine(defenderResponse)) {
                reactToResolveScryptHashValidation(defenderResponse);
            }
        }
    }

    /**
     * When scrypt hash validation is complete, queries the next block header or ends the battle.
     * @param defenderResponse Doge block hash response from defender.
     * @throws Exception
     */
    private void reactToResolveScryptHashValidation(EthWrapper.ResolvedScryptHashValidationEvent defenderResponse)
            throws Exception {
        if (defenderResponse.valid) {
            log.info("Scrypt hash validation succeeded session {}, superblock {}, block {}, scrypt {}",
                    defenderResponse.sessionId, defenderResponse.superblockId, defenderResponse.blockSha256Hash,
                    defenderResponse.blockScryptHash);

            queryNextBlockHeaderOrVerifySuperblock(defenderResponse.sessionId, defenderResponse.superblockId,
                    defenderResponse.blockSha256Hash);
        } else {
            // Scrypt hash
            log.info("Scrypt hash verification failed! session {}, superblock {}", defenderResponse.sessionId,
                    defenderResponse.superblockId);
            ethWrapper.verifySuperblock(defenderResponse.sessionId, ethWrapper.getBattleManagerForChallenges());
        }
    }

    /**
     * Adds new semi-approved superblocks to a data structure that keeps track of them
     * so that they can be invalidated if they turn out not to be in the main chain.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    private void getSemiApproved(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.SuperblockEvent> semiApprovedSuperblockEvents =
                ethWrapper.getSemiApprovedSuperblocks(fromBlock, toBlock);
        for (EthWrapper.SuperblockEvent superblockEvent : semiApprovedSuperblockEvents) {
            if (challengedByMe(superblockEvent))
                semiApprovedSet.add(superblockEvent.superblockId);
        }
    }


    /* ---- HELPER METHODS ---- */

    private boolean isMine(EthWrapper.RespondMerkleRootHashesEvent respondMerkleRootHashesEvent) {
        return respondMerkleRootHashesEvent.challenger.equals(myAddress);
    }

    private boolean isMine(EthWrapper.RespondBlockHeaderEvent respondBlockHeaderEvent) {
        return respondBlockHeaderEvent.challenger.equals(myAddress);
    }

    private boolean isMine(EthWrapper.ResolvedScryptHashValidationEvent resolvedScryptHashValidationEvent) {
        return resolvedScryptHashValidationEvent.challenger.equals(myAddress);
    }

    private boolean challengedByMe(EthWrapper.SuperblockEvent superblockEvent) throws Exception {
        return ethWrapper.getClaimChallengers(superblockEvent.superblockId).contains(myAddress);
    }

    /**
     * Gets the next Doge block hash to be requested in a battle session.
     * If the hash provided is either the last one in the list or not in the list at all,
     * this method returns null, because either of those conditions implies that the battle should end.
     * @param dogeBlockHash Hash of the last block in the session provided by the defender.
     * @param allDogeBlockHashes List of Doge block hashes corresponding to the same battle session.
     * @return Hash of next Doge block hash to be requested if there is one,
     * null otherwise.
     */
    private Sha256Hash getNextHashToQuery(Sha256Hash dogeBlockHash, List<Sha256Hash> allDogeBlockHashes) {
        int idx = allDogeBlockHashes.indexOf(dogeBlockHash) + 1;
        if (idx < allDogeBlockHashes.size() && idx > 0) {
            return allDogeBlockHashes.get(idx);
        } else {
            return null;
        }
    }


    /* ---- OVERRIDE ABSTRACT METHODS ---- */

    @Override
    protected boolean arePendingTransactions() throws IOException {
        return ethWrapper.arePendingTransactionsForChallengerAddress();
    }

    @Override
    protected boolean isEnabled() {
        return config.isDogeBlockChallengerEnabled();
    }

    @Override
    protected String getLatestEthBlockProcessedFilename() {
        return "SuperblockChallengerLatestEthBlockProcessed.dat";
    }

    @Override
    protected String getSessionToSuperblockMapFilename() {
        return "SuperblockChallengerSessionToSuperblockMap.dat";
    }

    @Override
    protected String getSuperblockToSessionsMapFilename() {
        return "SuperblockChallengerSuperblockToSessionsMap.dat";
    }

    @Override
    protected boolean isMine(EthWrapper.NewBattleEvent newBattleEvent) {
        return newBattleEvent.challenger.equals(myAddress);
    }

    @Override
    protected long getConfirmations() {
        return config.getAgentConstants().getChallengerConfirmations();
    }

    @Override
    protected void callBattleTimeouts() throws Exception {
        for (Keccak256Hash sessionId : sessionToSuperblockMap.keySet()) {
            if (ethWrapper.getSubmitterHitTimeout(sessionId)) {
                log.info("Submitter hit timeout on session {}. Calling timeout.", sessionId);
                ethWrapper.timeout(sessionId, ethWrapper.getBattleManagerForChallenges());
            }
        }
    }

    /**
     * Removes superblocks from the data structures that keep track of semi-approved superblocks.
     * If fund withdrawal is enabled, also withdraws any deposits that might have been unbonded
     * or any rewards that might have resulted from the superblocks' status change.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    @Override
    protected void removeSuperblocks(long fromBlock, long toBlock, List<EthWrapper.SuperblockEvent> superblockEvents)
            throws Exception {
        for (EthWrapper.SuperblockEvent superblockEvent : superblockEvents) {
            Keccak256Hash superblockId = superblockEvent.superblockId;

            if (superblockToSessionsMap.containsKey(superblockId)) {
                superblockToSessionsMap.remove(superblockId);
            }

            if (semiApprovedSet.contains(superblockId)) {
                semiApprovedSet.remove(superblockId);
            }

            if (config.isWithdrawFundsEnabled()) {
                ethWrapper.withdrawAllFundsExceptLimit(myAddress, true);
            }
        }
    }

    @Override
    protected long getTimerTaskPeriod() {
        return config.getAgentConstants().getChallengerTimerTaskPeriod();
    }

    /**
     * Filters battles where this challenger battled the superblock and the submitter got convicted
     * and deletes them from active battle set.
     * @param fromBlock
     * @param toBlock
     * @return
     * @throws Exception
     */
    @Override
    protected void deleteSubmitterConvictedBattles(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.SubmitterConvictedEvent> submitterConvictedEvents =
                ethWrapper.getSubmitterConvictedEvents(fromBlock, toBlock, ethWrapper.getBattleManagerForChallenges());

        for (EthWrapper.SubmitterConvictedEvent submitterConvictedEvent : submitterConvictedEvents) {
            if (sessionToSuperblockMap.containsKey(submitterConvictedEvent.sessionId)) {
                log.info("Submitter convicted on session {}, superblock {}. Battle won!",
                        submitterConvictedEvent.sessionId, submitterConvictedEvent.superblockId);
                sessionToSuperblockMap.remove(submitterConvictedEvent.sessionId);
            }
        }
    }

    // TODO: see if this should have some fault tolerance for battles that were erroneously not added to set
    /**
     * Filters battles where this challenger battled the superblock and got convicted
     * and deletes them from active battle set.
     * @param fromBlock
     * @param toBlock
     * @return
     * @throws Exception
     */
    @Override
    protected void deleteChallengerConvictedBattles(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.ChallengerConvictedEvent> challengerConvictedEvents =
                ethWrapper.getChallengerConvictedEvents(fromBlock, toBlock, ethWrapper.getBattleManagerForChallenges());

        for (EthWrapper.ChallengerConvictedEvent challengerConvictedEvent : challengerConvictedEvents) {
            if (challengerConvictedEvent.challenger.equals(myAddress)) {
                log.info("Challenger convicted on session {}, superblock {}. Battle lost!",
                        challengerConvictedEvent.sessionId, challengerConvictedEvent.superblockId);
                sessionToSuperblockMap.remove(challengerConvictedEvent.sessionId);
            }
        }
    }

    @Override
    protected void setupFiles() throws IOException {
        super.setupFiles();
        this.semiApprovedSet = new HashSet<>();
        this.semiApprovedSetFile = new File(dataDirectory.getAbsolutePath() + "/SemiApprovedSet.dat");
    }

    @Override
    protected void restoreFiles() throws ClassNotFoundException, IOException {
        super.restoreFiles();
        restore(semiApprovedSet, semiApprovedSetFile);
    }

    @Override
    protected void flushFiles() throws ClassNotFoundException, IOException {
        super.flushFiles();
        flush(semiApprovedSet, semiApprovedSetFile);
    }
}
