package org.sysethereum.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.sysethereum.agents.constants.AgentConstants;
import org.sysethereum.agents.constants.AgentRole;
import org.sysethereum.agents.constants.EthAddresses;
import org.sysethereum.agents.constants.SystemProperties;
import org.sysethereum.agents.core.bridge.BattleContractApi;
import org.sysethereum.agents.core.bridge.ClaimContractApi;
import org.sysethereum.agents.core.bridge.Superblock;
import org.sysethereum.agents.core.bridge.SuperblockContractApi;
import org.sysethereum.agents.core.bridge.battle.ChallengerConvictedEvent;
import org.sysethereum.agents.core.bridge.battle.SubmitterConvictedEvent;
import org.sysethereum.agents.core.syscoin.*;
import org.sysethereum.agents.core.eth.EthWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.sysethereum.agents.service.ChallengeEmailNotifier;
import org.sysethereum.agents.service.PersistentFileStore;
import org.sysethereum.agents.util.RandomizationCounter;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

/**
 * Monitors the Ethereum blockchain for superblock-related events
 * and defends/confirms the ones submitted by the agent.
 * @author Catalina Juarros
 */
@Service
@Slf4j(topic = "SuperblockDefenderClient")
public class SuperblockDefenderClient extends SuperblockBaseClient {
    private static final Logger logger = LoggerFactory.getLogger("SuperblockDefenderClient");

    private final SystemProperties config;
    private final PersistentFileStore persistentFileStore;
    private final BattleContractApi battleContractApi;
    private final SuperblockChain localSuperblockChain;
    private final RandomizationCounter randomizationCounter;
    private final BigInteger superblockTimeout;

    public SuperblockDefenderClient(
            SystemProperties config,
            AgentConstants agentConstants,
            PersistentFileStore persistentFileStore,
            EthWrapper ethWrapper,
            SuperblockContractApi superblockContractApi,
            BattleContractApi battleContractApi,
            ClaimContractApi claimContractApi,
            SuperblockChain superblockChain,
            RandomizationCounter randomizationCounter,
            BigInteger superblockTimeout,
            EthAddresses ethAddresses,
            ChallengeEmailNotifier challengeEmailNotifier
    ) {
        super(AgentRole.SUBMITTER, config, agentConstants, ethWrapper, superblockContractApi, battleContractApi, claimContractApi, challengeEmailNotifier);

        this.config = config;
        this.persistentFileStore = persistentFileStore;
        this.battleContractApi = battleContractApi;
        this.localSuperblockChain = superblockChain;
        this.randomizationCounter = randomizationCounter;
        this.superblockTimeout = superblockTimeout;
        this.myAddress = ethAddresses.generalPurposeAndSendSuperblocksAddress;
    }

    @Override
    public long reactToEvents(long fromBlock, long toBlock) {
        try {
            respondToNewBattles(fromBlock, toBlock);

            // Maintain data structures
            removeSemiApprovedDescendants(fromBlock, toBlock);

            respondToHeaders(fromBlock, toBlock);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return fromBlock - 1;
        }
        return toBlock;
    }

    @Override
    protected void reactToElapsedTime() {
        try {
            confirmEarliestApprovableSuperblock();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    /* ---- CONFIRMING/DEFENDING ---- */

    /* - Reacting to elapsed time - */
    /**
     * Responds to ongoing battle by responding with 16 headers 3 times and 12 headers the last time for a total of 60 headers over 4 transactions/blocks
     * @throws Exception
     */
    void respondToHeaders(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.RespondHeadersEvent> respondHeaderEvents =
                ethWrapper.getNewRespondHeadersEvents(fromBlock, toBlock);

        for (EthWrapper.RespondHeadersEvent respondHeaderEvent : respondHeaderEvents) {
            if (isMine(respondHeaderEvent) && (battleContractApi.getSessionChallengeState(respondHeaderEvent.sessionId) == EthWrapper.ChallengeState.Challenged)) {
                // only respond if the event is the one you are looking for (it matches the number of hashes the contract thinks is the latest)
                if (respondHeaderEvent.merkleHashCount == battleContractApi.getNumMerkleHashesBySession(respondHeaderEvent.sessionId)) {
                    logger.info("Header response detected for superblock {} session {}. Merkle hash count: {}. Responding with next set now.", respondHeaderEvent.superblockHash, respondHeaderEvent.sessionId, respondHeaderEvent.merkleHashCount);
                    ethWrapper.respondBlockHeaders(respondHeaderEvent.sessionId, respondHeaderEvent.superblockHash, respondHeaderEvent.merkleHashCount);
                }
            }
        }
    }
    /**
     * Finds earliest superblock that's not invalid and stored locally,
     * but not confirmed in Sysethereum Contracts, and confirms it if its timeout has passed
     * and it either received no challenges or won all battles.
     * If the superblock is indeed confirmed, its status in Sysethereum Contracts
     * is set to Approved if it received no challenges and SemiApproved otherwise.
     * @throws Exception
     */
    private void confirmEarliestApprovableSuperblock() throws Exception {
        Keccak256Hash bestSuperblockId = superblockContractApi.getBestSuperblockId();
        Superblock chainHead = localSuperblockChain.getChainHead();

        if (chainHead.getHash().equals(bestSuperblockId)) {
            // Contract and local db best superblocks are the same, do nothing.
            return;
        }


        Superblock toConfirm = localSuperblockChain.getFirstDescendant(bestSuperblockId);
        if (toConfirm == null) {
            logger.info("Best superblock from contracts, {}, not found in local database. Stopping.", bestSuperblockId);
            return;
        }
        Keccak256Hash toConfirmId = toConfirm.getHash();
        Superblock highestDescendant = ethWrapper.getHighestApprovableOrNewDescendant(toConfirm, bestSuperblockId);
        if (highestDescendant == null) {
            logger.info("Highest descendent from contracts, {}, not found in local database. Stopping.", bestSuperblockId);
            return;
        }
        Keccak256Hash highestDescendantId = highestDescendant.getHash();


        // deal with your own superblock claims or if it has become unresponsive we allow someone else to check the claim or confirm it
        if (!isMine(highestDescendantId) && !unresponsiveTimeoutPassed(highestDescendantId)) return;

        if (ethWrapper.semiApprovedAndApprovable(toConfirm, highestDescendant)) {
            // The superblock is semi approved and it can be approved if it has enough confirmations
            logger.info("Confirming semi-approved superblock {} with descendant {}", toConfirmId, highestDescendantId);
            claimContractApi.confirmClaim(toConfirmId, highestDescendantId);
        }
        else if (ethWrapper.newAndTimeoutPassed(highestDescendantId) || claimContractApi.getInBattleAndSemiApprovable(highestDescendantId)) {
            // Either the superblock is unchallenged or it won all the battles;
            // it will get approved or semi-approved depending on the situation
            // (look at SyscoinClaimManager contract source code for more details)
            logger.info("Confirming superblock {}", highestDescendantId);
            claimContractApi.checkClaimFinished(highestDescendantId, false);

        }

    }



    /* - Reacting to events - */

    private void respondToNewBattles(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.NewBattleEvent> queryBattleEvents = battleContractApi.getNewBattleEvents(fromBlock, toBlock);

        for (EthWrapper.NewBattleEvent queryBattleEvent : queryBattleEvents) {
            if (isMyBattleEvent(queryBattleEvent) && (battleContractApi.getSessionChallengeState(queryBattleEvent.sessionId) == EthWrapper.ChallengeState.Challenged)) {
                logger.info("Battle detected for superblock {} session {}. Responding now with first set of headers.", queryBattleEvent.superblockHash, queryBattleEvent.sessionId);
                ethWrapper.respondBlockHeaders(queryBattleEvent.sessionId, queryBattleEvent.superblockHash, 0);
            }
        }
    }

    private boolean unresponsiveTimeoutPassed(Keccak256Hash superblockId) throws Exception {
        double delay = superblockTimeout.floatValue() * randomizationCounter.getValue();
        int timeout = superblockTimeout.intValue() + (int)delay;
        return claimContractApi.getNewEventTimestampDate(superblockId).before(SuperblockUtils.getNSecondsAgo(timeout));
    }

    private boolean isMine(EthWrapper.RespondHeadersEvent respondHeadersEvent) {
        return respondHeadersEvent.submitter.equals(myAddress);
    }

    /**
     * Removes superblocks from the data structure that keeps track of semi-approved superblocks.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    @Override
    protected void removeSuperblocks(long fromBlock, long toBlock, List<SuperblockContractApi.SuperblockEvent> superblockEvents)
            throws Exception {
        boolean removeFromContract = false;
        for (SuperblockContractApi.SuperblockEvent event : superblockEvents) {
            if (superblockToSessionsMap.containsKey(event.superblockId)) {
                sessionToSuperblockMap.keySet().removeAll(superblockToSessionsMap.get(event.superblockId));
                superblockToSessionsMap.remove(event.superblockId);
                removeFromContract = true;
            }

        }
        if (removeFromContract && config.isWithdrawFundsEnabled()) {
            claimContractApi.withdrawAllFundsExceptLimit(myAddress, false);
        }
    }

    /* ---- BATTLE MAP METHODS ---- */

    /**
     * Removes semi-approved superblocks from superblock to session map.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    private void removeSemiApprovedDescendants(long fromBlock, long toBlock) throws Exception {
        List<SuperblockContractApi.SuperblockEvent> semiApprovedSuperblockEvents =
                superblockContractApi.getSemiApprovedSuperblocks(fromBlock, toBlock);

        for (SuperblockContractApi.SuperblockEvent event : semiApprovedSuperblockEvents) {
            if (superblockToSessionsMap.containsKey(event.superblockId)) {
                sessionToSuperblockMap.keySet().removeAll(superblockToSessionsMap.get(event.superblockId));
                superblockToSessionsMap.remove(event.superblockId);
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
        List<SubmitterConvictedEvent> events = battleContractApi.getSubmitterConvictedEvents(agentRole, fromBlock, toBlock);

        for (SubmitterConvictedEvent event : events) {
            if (event.submitter.equals(myAddress)) {
                logger.info("Submitter convicted on session {}, superblock {}. Battle lost!",
                        event.sessionId, event.superblockHash);
                sessionToSuperblockMap.remove(event.sessionId);
                if (superblockToSessionsMap.containsKey(event.superblockHash)) {
                    superblockToSessionsMap.get(event.superblockHash).remove(event.sessionId);
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
        List<ChallengerConvictedEvent> events = battleContractApi.getChallengerConvictedEvents(agentRole, fromBlock, toBlock);

        for (ChallengerConvictedEvent event : events) {
            if (sessionToSuperblockMap.containsKey(event.sessionId)) {
                logger.info("Challenger convicted on session {}, superblock {}. Battle won!", event.sessionId, event.superblockHash);
                sessionToSuperblockMap.remove(event.sessionId);
            }
            if (superblockToSessionsMap.containsKey(event.superblockHash)) {
                superblockToSessionsMap.get(event.superblockHash).remove(event.sessionId);
            }
        }
    }

    @Override
    protected void restoreFiles() throws ClassNotFoundException, IOException {
        latestEthBlockProcessed = persistentFileStore.restore(latestEthBlockProcessed, latestEthBlockProcessedFile);
        sessionToSuperblockMap = persistentFileStore.restore(sessionToSuperblockMap, sessionToSuperblockMapFile);
        superblockToSessionsMap = persistentFileStore.restore(superblockToSessionsMap, superblockToSessionsMapFile);
    }

    @Override
    protected void flushFiles() throws IOException {
        persistentFileStore.flush(latestEthBlockProcessed, latestEthBlockProcessedFile);
        persistentFileStore.flush(sessionToSuperblockMap, sessionToSuperblockMapFile);
        persistentFileStore.flush(superblockToSessionsMap, superblockToSessionsMapFile);
    }

}
