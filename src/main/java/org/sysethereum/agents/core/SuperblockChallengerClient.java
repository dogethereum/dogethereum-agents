package org.sysethereum.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Sha256Hash;
import org.sysethereum.agents.core.eth.EthWrapper;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;
import org.sysethereum.agents.core.syscoin.Superblock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

/**
 * Monitors the Ethereum blockchain for superblock-related events
 * and challenges invalid submissions.
 * @author Catalina Juarros
 * @author Ismael Bejarano
 */

@Service
@Slf4j(topic = "SuperblockChallengerClient")
public class SuperblockChallengerClient extends SuperblockBaseClient {
    private static final Logger log = LoggerFactory.getLogger("LocalAgentConstants");
    private HashSet<Keccak256Hash> semiApprovedSet;
    private File semiApprovedSetFile;

    public SuperblockChallengerClient() {
        super("Superblock challenger client");
    }

    @Override
    protected void setupClient() {
        myAddress = ethWrapper.getSyscoinSuperblockChallengerAddress();
    }

    @Override
    public long reactToEvents(long fromBlock, long toBlock) {
        try {
            validateNewSuperblocks(fromBlock, toBlock);
            respondToNewBattles(fromBlock, toBlock);
            respondToMerkleRootHashesEventResponses(fromBlock, toBlock);
            respondToBlockHeaderEventResponses(fromBlock, toBlock);

            // Maintain data structures
            getSemiApproved(fromBlock, toBlock);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return fromBlock - 1;
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
            Superblock mainChainSuperblock = superblockChain.getSuperblockByHeight(semiApprovedHeight);
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
            // decided is set to true inside of checkclaimfinished and thus only allows it to call oncex
            if (ethWrapper.getClaimInvalid(superblockId) && ethWrapper.getClaimExists(superblockId) && !ethWrapper.getClaimDecided(superblockId)) {
                log.info("Superblock {} lost a battle. Invalidating.", superblockId);
                ethWrapper.checkClaimFinished(superblockId, true);
                sessionToSuperblockMap.keySet().removeAll(superblockToSessionsMap.get(superblockId));
                superblockToSessionsMap.remove(superblockId);

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
        if(newSuperblockEvents.size() > 0){
            ethWrapper.setRandomizationCounter();
        }
        List<Keccak256Hash> toChallenge = new ArrayList<>();
        for (EthWrapper.SuperblockEvent newSuperblock : newSuperblockEvents) {
            log.info("NewSuperblock {}. Validating...", newSuperblock.superblockId);

            Superblock superblock = superblockChain.getSuperblock(newSuperblock.superblockId);
            if (superblock == null) {
                BigInteger height = ethWrapper.getSuperblockHeight(newSuperblock.superblockId);
                Superblock localSuperblock = superblockChain.getSuperblockByHeight(height.longValue());
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
                log.info("Superblock height: {}... superblock present in our superblock chain", superblock.getSuperblockHeight());
            }
        }

        for (Keccak256Hash superblockId : toChallenge) {
            ethWrapper.challengeSuperblock(superblockId, myAddress);
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
            if (isMine(newBattleEvent) && ethWrapper.getClaimExists(newBattleEvent.superblockId) && !ethWrapper.getClaimDecided(newBattleEvent.superblockId)) {
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
            if (isMine(defenderResponse) && ethWrapper.getClaimExists(defenderResponse.superblockId) && !ethWrapper.getClaimDecided(defenderResponse.superblockId)) {
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
            if (isMine(defenderResponse) && ethWrapper.getClaimExists(defenderResponse.superblockId) && !ethWrapper.getClaimDecided(defenderResponse.superblockId)) {
                reactToBlockHeaderResponse(defenderResponse);
            }
        }
    }

    /**
     * Queries header of the first Syscoin block hash in a certain superblock that the challenger is battling.
     * If it was empty, just verifies it.
     * @param defenderResponse Merkle root hashes response from the defender.
     * @throws Exception
     */
    private void startBlockHeaderQueries(EthWrapper.RespondMerkleRootHashesEvent defenderResponse) throws Exception {
        Keccak256Hash superblockId = defenderResponse.superblockId;
        List<Sha256Hash> syscoinBlockHashes = defenderResponse.blockHashes;
        log.info("Starting block header queries for superblock {}", superblockId);

        if (!syscoinBlockHashes.isEmpty()) {
            log.info("Querying first block header for superblock {}", superblockId);
            ethWrapper.queryBlockHeader(superblockId, defenderResponse.sessionId, syscoinBlockHashes.get(0),
                    myAddress);
        } else {
            log.info("Merkle root hashes response for superblock {} is empty. Verifying it now.", superblockId);
            ethWrapper.verifySuperblock(defenderResponse.sessionId, ethWrapper.getBattleManagerForChallenges());
        }
    }

    /**
     * Queries the header for the next hash in the superblock's list of Syscoin hashes if there is one,
     * ends the battle by verifying the superblock if Syscoin block hash was the last one.
     * @param defenderResponse Syscoin block hash response from defender.
     * @throws Exception
     */
    private void reactToBlockHeaderResponse(EthWrapper.RespondBlockHeaderEvent defenderResponse) throws Exception {
        Sha256Hash syscoinBlockHash = Sha256Hash.wrapReversed(Sha256Hash.hashTwice(defenderResponse.blockHeader));
        queryNextBlockHeaderOrVerifySuperblock(defenderResponse.sessionId, defenderResponse.superblockId,
                syscoinBlockHash);

    }


    /**
     * Queries the next block header or end battle verifying the superblock.
     * @param sessionId Battle's session ID
     * @param superblockId Superblock ID
     * @param syscoinBlockHash Last Syscoin block hash requested
     * @throws Exception
     */
    private void queryNextBlockHeaderOrVerifySuperblock(Keccak256Hash sessionId, Keccak256Hash superblockId,
                                                        Sha256Hash syscoinBlockHash) throws Exception {
        List<Sha256Hash> sessionSyscoinBlockHashes = ethWrapper.getSyscoinBlockHashes(sessionId);
        Sha256Hash nextSyscoinBlockHash = getNextHashToQuery(syscoinBlockHash, sessionSyscoinBlockHashes);

        if (nextSyscoinBlockHash != null) {
            // not last hash
            log.info("Querying block header {}", nextSyscoinBlockHash);
            ethWrapper.queryBlockHeader(superblockId, sessionId, nextSyscoinBlockHash, myAddress);
        } else {
            // last hash; end battle
            log.info("All block hashes for superblock {} have been received. Verifying it now.", superblockId);
            ethWrapper.verifySuperblock(sessionId, ethWrapper.getBattleManagerForChallenges());
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


    private boolean challengedByMe(EthWrapper.SuperblockEvent superblockEvent) throws Exception {
        return ethWrapper.getClaimChallengers(superblockEvent.superblockId).contains(myAddress);
    }

    /**
     * Gets the next Syscoin block hash to be requested in a battle session.
     * If the hash provided is either the last one in the list or not in the list at all,
     * this method returns null, because either of those conditions implies that the battle should end.
     * @param syscoinBlockHash Hash of the last block in the session provided by the defender.
     * @param allSyscoinBlockHashes List of Syscoin block hashes corresponding to the same battle session.
     * @return Hash of next Syscoin block hash to be requested if there is one,
     * null otherwise.
     */
    private Sha256Hash getNextHashToQuery(Sha256Hash syscoinBlockHash, List<Sha256Hash> allSyscoinBlockHashes) {
        int idx = allSyscoinBlockHashes.indexOf(syscoinBlockHash) + 1;
        if (idx < allSyscoinBlockHashes.size() && idx > 0) {
            return allSyscoinBlockHashes.get(idx);
        } else {
            return null;
        }
    }


    /* ---- OVERRIDE ABSTRACT METHODS ---- */

    @Override
    protected void setupFiles() throws IOException {
        setupBaseFiles();
        setupSemiApprovedSet();
    }

    @Override
    protected boolean arePendingTransactions() throws IOException {
        return ethWrapper.arePendingTransactionsForChallengerAddress();
    }

    @Override
    protected boolean isEnabled() {
        return config.isSyscoinBlockChallengerEnabled();
    }

    @Override
    protected String getLastEthBlockProcessedFilename() {
        return "SuperblockChallengerLatestEthBlockProcessedFile.dat";
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
        boolean withdrawFlag = false;
        for (EthWrapper.SuperblockEvent superblockEvent : superblockEvents) {
            Keccak256Hash superblockId = superblockEvent.superblockId;

            if (superblockToSessionsMap.containsKey(superblockId)) {
                sessionToSuperblockMap.keySet().removeAll(superblockToSessionsMap.get(superblockId));
                superblockToSessionsMap.remove(superblockId);
                withdrawFlag = true;
            }

            if (semiApprovedSet.contains(superblockId)) {
                semiApprovedSet.remove(superblockId);
                withdrawFlag = true;
            }

        }
        if (withdrawFlag && config.isWithdrawFundsEnabled()) {
            ethWrapper.withdrawAllFundsExceptLimit(myAddress, true);
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
                ethWrapper.getSubmitterConvictedEvents(fromBlock, toBlock, ethWrapper.getBattleManagerForChallengesGetter());

        for (EthWrapper.SubmitterConvictedEvent submitterConvictedEvent : submitterConvictedEvents) {
            if (sessionToSuperblockMap.containsKey(submitterConvictedEvent.sessionId)) {
                log.info("Submitter convicted on session {}, superblock {}. Battle won!",
                        submitterConvictedEvent.sessionId, submitterConvictedEvent.superblockId);
                sessionToSuperblockMap.remove(submitterConvictedEvent.sessionId);
            }
            if (superblockToSessionsMap.containsKey(submitterConvictedEvent.superblockId)) {
                superblockToSessionsMap.get(submitterConvictedEvent.superblockId).remove(submitterConvictedEvent.sessionId);
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
                ethWrapper.getChallengerConvictedEvents(fromBlock, toBlock, ethWrapper.getBattleManagerForChallengesGetter());

        for (EthWrapper.ChallengerConvictedEvent challengerConvictedEvent : challengerConvictedEvents) {
            if (challengerConvictedEvent.challenger.equals(myAddress)) {
                log.info("Challenger convicted on session {}, superblock {}. Battle lost!",
                        challengerConvictedEvent.sessionId, challengerConvictedEvent.superblockId);
                sessionToSuperblockMap.remove(challengerConvictedEvent.sessionId);
                if (superblockToSessionsMap.containsKey(challengerConvictedEvent.superblockId)) {
                    superblockToSessionsMap.get(challengerConvictedEvent.superblockId).remove(challengerConvictedEvent.sessionId);
                }
            }
        }
    }

    @Override
    protected void restoreFiles() throws ClassNotFoundException, IOException {
        restore(latestEthBlockProcessed, latestEthBlockProcessedFile);
        restore(sessionToSuperblockMap, sessionToSuperblockMapFile);
        restore(semiApprovedSet, semiApprovedSetFile);
    }

    @Override
    protected void flushFiles() throws ClassNotFoundException, IOException {
        flush(latestEthBlockProcessed, latestEthBlockProcessedFile);
        flush(sessionToSuperblockMap, sessionToSuperblockMapFile);
        flush(semiApprovedSet, semiApprovedSetFile);
    }


    /* ---- STORAGE ---- */

    private void setupSemiApprovedSet() {
        this.semiApprovedSet = new HashSet<>();
        this.semiApprovedSetFile = new File(dataDirectory.getAbsolutePath() + "/SemiApprovedSet.dat");
    }

}
