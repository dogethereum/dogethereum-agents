package org.sysethereum.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.sysethereum.agents.constants.AgentConstants;
import org.sysethereum.agents.constants.AgentRole;
import org.sysethereum.agents.constants.EthAddresses;
import org.sysethereum.agents.constants.SystemProperties;
import org.sysethereum.agents.contract.SyscoinBattleManagerExtended;
import org.sysethereum.agents.core.bridge.BattleContractApi;
import org.sysethereum.agents.core.bridge.ClaimContractApi;
import org.sysethereum.agents.core.bridge.SuperblockContractApi;
import org.sysethereum.agents.core.eth.EthWrapper;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;
import org.sysethereum.agents.core.bridge.Superblock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.sysethereum.agents.core.syscoin.SuperblockChain;
import org.sysethereum.agents.service.ChallengeEmailNotifier;
import org.sysethereum.agents.util.RandomizationCounter;

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
    private static final Logger logger = LoggerFactory.getLogger("SuperblockChallengerClient");

    private final RandomizationCounter randomizationCounter;
    private final SyscoinBattleManagerExtended battleManagerForChallenges;
    @NotNull
    private final SystemProperties config;
    private final SuperblockChain superblockChain;
    private final SuperblockContractApi superblockContractApi;
    private final ClaimContractApi claimContractApi;
    private final BattleContractApi battleContractApi;
    private final SyscoinBattleManagerExtended battleManagerForChallengesGetter;

    private HashSet<Keccak256Hash> semiApprovedSet;
    private File semiApprovedSetFile;

    public SuperblockChallengerClient(
            SystemProperties config,
            AgentConstants agentConstants,
            EthWrapper ethWrapper,
            SuperblockChain superblockChain,
            EthAddresses ethAddresses,
            SuperblockContractApi superblockContractApi,
            ClaimContractApi claimContractApi,
            BattleContractApi battleContractApi,
            SyscoinBattleManagerExtended battleManagerForChallenges,
            SyscoinBattleManagerExtended battleManagerForChallengesGetter,
            ChallengeEmailNotifier challengeEmailNotifier
    ) {
        super(AgentRole.CHALLENGER, agentConstants, ethWrapper, superblockContractApi,
                claimContractApi, challengeEmailNotifier, config.dataDirectory());

        this.config = config;
        this.superblockChain = superblockChain;
        this.superblockContractApi = superblockContractApi;
        this.claimContractApi = claimContractApi;
        this.battleContractApi = battleContractApi;
        this.battleManagerForChallengesGetter = battleManagerForChallengesGetter;

        this.randomizationCounter = new RandomizationCounter();
        this.battleManagerForChallenges = battleManagerForChallenges;
        this.myAddress = ethAddresses.syscoinSuperblockChallengerAddress;

        this.semiApprovedSet = new HashSet<>();
        this.semiApprovedSetFile = new File(dataDirectory.getAbsolutePath() + "/SemiApprovedSet.dat");
    }

    @Override
    public long reactToEvents(long fromBlock, long toBlock) {
        try {
            validateNewSuperblocks(fromBlock, toBlock);
            respondToNewBattles(fromBlock, toBlock);

            // Maintain data structures
            getSemiApproved(fromBlock, toBlock);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
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
            logger.error(e.getMessage(), e);
        }
    }


    /* ---- CHALLENGING ---- */

    /* - Reacting to elapsed time */

    private void invalidateNonMainChainSuperblocks() throws Exception {
        for (Keccak256Hash superblockId : semiApprovedSet) {
            long semiApprovedHeight = superblockContractApi.getHeight(superblockId).longValue();
            Superblock mainChainSuperblock = superblockChain.getByHeight(semiApprovedHeight);
            if (mainChainSuperblock != null) {
                long confirmations = claimContractApi.getSuperblockConfirmations();
                if (!mainChainSuperblock.getSuperblockId().equals(superblockId) &&
                        superblockContractApi.getChainHeight().longValue() >= semiApprovedHeight + confirmations) {
                    logger.info("Semi-approved superblock {} not found in main chain. Invalidating.", superblockId);
                    ethWrapper.rejectClaim(superblockId);
                }
            }
        }
    }

    private void invalidateLoserSuperblocks() throws Exception {
        for (Keccak256Hash superblockId : superblockToSessionsMap.keySet()) {
            // decided is set to true inside of checkclaimfinished and thus only allows it to call once
            if (claimContractApi.getClaimInvalid(superblockId) && claimContractApi.getClaimExists(superblockId) && !claimContractApi.getClaimDecided(superblockId)) {
                logger.info("Superblock {} lost a battle. Invalidating.", superblockId);
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
        List<SuperblockContractApi.SuperblockEvent> newSuperblockEvents = superblockContractApi.getNewSuperblocks(fromBlock, toBlock);
        if(newSuperblockEvents.size() > 0){
            randomizationCounter.updateRandomValue();
        }
        List<Keccak256Hash> toChallenge = new ArrayList<>();
        for (SuperblockContractApi.SuperblockEvent newSuperblock : newSuperblockEvents) {
            logger.info("NewSuperblock {}. Validating...", newSuperblock.superblockId);

            Superblock superblock = superblockChain.getSuperblock(newSuperblock.superblockId);
            if (superblock == null) {
                BigInteger height = superblockContractApi.getHeight(newSuperblock.superblockId);
                Superblock localSuperblock = superblockChain.getByHeight(height.longValue());
                if (localSuperblock == null) {
                    // local superblockchain should not be out of sync because there is 2 hour descrepency between saving and sending
                    // this could mean our local syscoin node is out of sync (out of our control) in which case we have no choice but to challenge
                    // we have to assume if your local syscoin node is forked or not synced and we cannot detect difference between bad and good SB in that case we must challenge
                    logger.info("Superblock {} not present in our superblock chain", newSuperblock.superblockId);
                    toChallenge.add(newSuperblock.superblockId);
                } else {
                    logger.info("Superblock {} at height {} is replaced by {} in our superblock chain",
                            newSuperblock.superblockId,
                            height,
                            localSuperblock.getSuperblockId());
                    toChallenge.add(newSuperblock.superblockId);
                }
            } else {
                logger.info("Superblock height: {}... superblock present in our superblock chain", superblock.getSuperblockHeight());
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
     * Saves all new battle events that the challenger is taking part in.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    private void respondToNewBattles(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.NewBattleEvent> newBattleEvents = ethWrapper.getNewBattleEvents(fromBlock, toBlock);

        for (EthWrapper.NewBattleEvent newBattleEvent : newBattleEvents) {
            if (isMine(newBattleEvent) && battleContractApi.getSessionChallengeState(newBattleEvent.sessionId) == EthWrapper.ChallengeState.Challenged) {
                sessionToSuperblockMap.put(newBattleEvent.sessionId, newBattleEvent.superblockHash);
                addToSuperblockToSessionsMap(newBattleEvent.sessionId, newBattleEvent.superblockHash);
            }
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
        List<SuperblockContractApi.SuperblockEvent> semiApprovedSuperblockEvents =
                superblockContractApi.getSemiApprovedSuperblocks(fromBlock, toBlock);
        for (SuperblockContractApi.SuperblockEvent superblockEvent : semiApprovedSuperblockEvents) {
            if (challengedByMe(superblockEvent))
                semiApprovedSet.add(superblockEvent.superblockId);
        }
    }


    /* ---- HELPER METHODS ---- */


    private boolean challengedByMe(SuperblockContractApi.SuperblockEvent superblockEvent) throws Exception {
        return claimContractApi.getClaimChallenger(superblockEvent.superblockId).getValue().equals(myAddress);
    }

    /* ---- OVERRIDE ABSTRACT METHODS ---- */

    @Override
    protected boolean arePendingTransactions() throws InterruptedException, IOException {
        return ethWrapper.arePendingTransactionsForChallengerAddress();
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

    protected void callBattleTimeouts() throws Exception {
        for (Keccak256Hash sessionId : sessionToSuperblockMap.keySet()) {
            if (battleContractApi.getSubmitterHitTimeout(sessionId)) {
                logger.info("Submitter hit timeout on session {}. Calling timeout.", sessionId);
                ethWrapper.timeout(sessionId, battleManagerForChallenges);
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
    protected void removeSuperblocks(long fromBlock, long toBlock, List<SuperblockContractApi.SuperblockEvent> superblockEvents)
            throws Exception {
        boolean removeFromContract = false;
        for (SuperblockContractApi.SuperblockEvent superblockEvent : superblockEvents) {
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
            claimContractApi.withdrawAllFundsExceptLimit(myAddress, true);
        }
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
                ethWrapper.getSubmitterConvictedEvents(fromBlock, toBlock, battleManagerForChallengesGetter);

        for (EthWrapper.SubmitterConvictedEvent submitterConvictedEvent : submitterConvictedEvents) {
            if (sessionToSuperblockMap.containsKey(submitterConvictedEvent.sessionId)) {
                logger.info("Submitter convicted on session {}, superblock {}. Battle won!",
                        submitterConvictedEvent.sessionId, submitterConvictedEvent.superblockHash);
                sessionToSuperblockMap.remove(submitterConvictedEvent.sessionId);
            }
            if (superblockToSessionsMap.containsKey(submitterConvictedEvent.superblockHash)) {
                superblockToSessionsMap.get(submitterConvictedEvent.superblockHash).remove(submitterConvictedEvent.sessionId);
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
                ethWrapper.getChallengerConvictedEvents(fromBlock, toBlock, battleManagerForChallengesGetter);

        for (EthWrapper.ChallengerConvictedEvent challengerConvictedEvent : challengerConvictedEvents) {
            if (challengerConvictedEvent.challenger.equals(myAddress)) {
                logger.info("Challenger convicted on session {}, superblock {}. Battle lost!",
                        challengerConvictedEvent.sessionId, challengerConvictedEvent.superblockHash);
                sessionToSuperblockMap.remove(challengerConvictedEvent.sessionId);
                if (superblockToSessionsMap.containsKey(challengerConvictedEvent.superblockHash)) {
                    superblockToSessionsMap.get(challengerConvictedEvent.superblockHash).remove(challengerConvictedEvent.sessionId);
                }
            }
        }
    }

    @Override
    protected void restoreFiles() throws ClassNotFoundException, IOException {
        latestEthBlockProcessed = restore(latestEthBlockProcessed, latestEthBlockProcessedFile);
        sessionToSuperblockMap = restore(sessionToSuperblockMap, sessionToSuperblockMapFile);

        semiApprovedSet = restore(semiApprovedSet, semiApprovedSetFile);
    }

    @Override
    protected void flushFiles() throws IOException {
        flush(latestEthBlockProcessed, latestEthBlockProcessedFile);
        flush(sessionToSuperblockMap, sessionToSuperblockMapFile);
        flush(semiApprovedSet, semiApprovedSetFile);
    }

}
