package org.sysethereum.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.StoredBlock;
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
            respondToLastBlockHeaderEventResponses(fromBlock, toBlock);

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
            // decided is set to true inside of checkclaimfinished and thus only allows it to call once
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
        // check for pending if we have superblocks to challenge
        if(toChallenge.size() > 0) {
            Thread.sleep(500); // in case the transaction takes some time to complete
            if (ethWrapper.arePendingTransactionsForChallengerAddress()) {
                throw new Exception("Skipping challenging superblocks, there are pending transaction for the challenger address.");
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
            if (isMine(newBattleEvent) && ethWrapper.getSessionChallengeState(newBattleEvent.sessionId) == EthWrapper.ChallengeState.Challenged) {
                ethWrapper.queryMerkleRootHashes(newBattleEvent.sessionId, myAddress);
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
            if (isMine(defenderResponse) && ethWrapper.getSessionChallengeState(defenderResponse.sessionId) == EthWrapper.ChallengeState.RespondMerkleRootHashes) {
                startLastBlockHeaderQueries(defenderResponse);
            }
        }
    }

    /**
     * For all block header event responses corresponding to battles that the challenger is taking part in,
     * end the battle if challenge state is PendingVerification and last block status is verified
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    private void respondToLastBlockHeaderEventResponses(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.RespondLastBlockHeaderEvent> defenderResponses =
                ethWrapper.getRespondLastBlockHeaderEvents(fromBlock, toBlock);

        for (EthWrapper.RespondLastBlockHeaderEvent defenderResponse : defenderResponses) {
            if (isMine(defenderResponse)){
                if(ethWrapper.getSessionChallengeState(defenderResponse.sessionId) == EthWrapper.ChallengeState.PendingVerification) {
                    ethWrapper.verifySuperblock(defenderResponse.sessionId, ethWrapper.getBattleManagerForChallenges());
                }
            }
        }
    }
    int findInvalidInterimBlockIndex(Keccak256Hash sessionId) throws Exception{
        List<Sha256Hash> hashesFromContract = ethWrapper.getBlockHashesBySession(sessionId);
        Keccak256Hash superblockId = ethWrapper.getSuperblockIdBySession(sessionId);
        BigInteger height = ethWrapper.getSuperblockHeight(superblockId);
        // find local superblock based on height of superblock being challenged
        Superblock superblock = superblockChain.getSuperblockByHeight(height.longValue());
        if(superblock == null)
            throw new Exception("Superblock {} not found in local chain at height {} " + superblockId + height.longValue());



        List<Sha256Hash> localHashes = superblock.getSyscoinBlockHashes();
        if(localHashes.size() != superblockChain.SUPERBLOCK_DURATION)
            throw new Exception("Local superblock must have 60 hashes, we found: " + localHashes.size());
        if(hashesFromContract.size() != superblockChain.SUPERBLOCK_DURATION)
            throw new Exception("Stored superblock must have 60 hashes, we found: " + hashesFromContract.size());

        StoredBlock firstBlock = syscoinWrapper.getBlock(hashesFromContract.get(0));
        // if we don't have the block representing the first hash of the superblock then it must be a bad block to us, so we should ask submitter to prove 0th block
        if(firstBlock == null) {
            return 0;
        }
        // check first block prev hash matches last superblock last block hash
        Sha256Hash lastBlockHash = ethWrapper.getSuperblockLastHash(superblock.getParentId());
        // if prev hash doesn't match last superblock last block hash then enforce submitter to respond with 0th header to verify it is linked to previous superblock
        if(firstBlock.getHeader().getPrevBlockHash() != lastBlockHash){
            return 0;
        }
        // start from position 57 and walk back to 0, checking to ensure if a hash is different then request the header of the proceeding index (i+1) to check the prevBlock field of the header matches the previous hash (i position)
        for (int i = hashesFromContract.size()-3; i >= 0; i--) {
            // we check hash in i position and if not matching want to request the i+1 header which will give prevBlock which should match hash in i position otherwise chain is broken
            if(!hashesFromContract.get(i).equals(localHashes.get(i))) {
                return i+1;
            }
        }
        // if all matches then just return -1 meaning we don't have to check interim block for this challenge
        return -1;
    }
    /**
     * Queries last header for the session that the challenger is battling.
     * If it was empty, just verifies it.
     * @param defenderResponse Merkle root hashes response from the defender.
     * @throws Exception
     */
    private void startLastBlockHeaderQueries(EthWrapper.RespondMerkleRootHashesEvent defenderResponse) throws Exception {
        log.info("Starting last block header query for session {}", defenderResponse.sessionId);
        ethWrapper.queryLastBlockHeader(defenderResponse.sessionId, findInvalidInterimBlockIndex(defenderResponse.sessionId));

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

    private boolean isMine(EthWrapper.RespondLastBlockHeaderEvent respondBlockHeaderEvent) {
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
    protected boolean arePendingTransactions() throws InterruptedException, IOException {
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
        boolean removeFromContract = false;
        for (EthWrapper.SuperblockEvent superblockEvent : superblockEvents) {
            Keccak256Hash superblockId = superblockEvent.superblockId;

            if (superblockToSessionsMap.containsKey(superblockId)) {
                sessionToSuperblockMap.keySet().removeAll(superblockToSessionsMap.get(superblockId));
                superblockToSessionsMap.remove(superblockId);
                removeFromContract = true;
            }

            if (semiApprovedSet.contains(superblockId)) {
                semiApprovedSet.remove(superblockId);
                removeFromContract = true;
            }

        }
        if (removeFromContract && config.isWithdrawFundsEnabled()) {
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
            Superblock superblock = ethWrapper.getSuperblockBySession(submitterConvictedEvent.sessionId);
            if(superblock == null)
                continue;
            if (sessionToSuperblockMap.containsKey(submitterConvictedEvent.sessionId)) {
                log.info("Submitter convicted on session {}, superblock {}. Battle won!",
                        submitterConvictedEvent.sessionId, superblock.getSuperblockId());
                sessionToSuperblockMap.remove(submitterConvictedEvent.sessionId);
            }
            if (superblockToSessionsMap.containsKey(superblock.getSuperblockId())) {
                superblockToSessionsMap.get(superblock.getSuperblockId()).remove(submitterConvictedEvent.sessionId);
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
                Superblock superblock = ethWrapper.getSuperblockBySession(challengerConvictedEvent.sessionId);
                if(superblock == null)
                    continue;
                log.info("Challenger convicted on session {}, superblock {}. Battle lost!",
                        challengerConvictedEvent.sessionId, superblock.getSuperblockId());
                sessionToSuperblockMap.remove(challengerConvictedEvent.sessionId);
                if (superblockToSessionsMap.containsKey(superblock.getSuperblockId())) {
                    superblockToSessionsMap.get(superblock.getSuperblockId()).remove(challengerConvictedEvent.sessionId);
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
