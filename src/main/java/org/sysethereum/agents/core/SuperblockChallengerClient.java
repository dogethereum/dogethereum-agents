package org.sysethereum.agents.core;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Sha256Hash;
import org.sysethereum.agents.constants.AgentConstants;
import org.sysethereum.agents.constants.AgentRole;
import org.sysethereum.agents.constants.EthAddresses;
import org.sysethereum.agents.constants.SystemProperties;
import org.sysethereum.agents.contract.SyscoinBattleManagerExtended;
import org.sysethereum.agents.core.bridge.*;
import org.sysethereum.agents.core.bridge.battle.NewBattleEvent;
import org.sysethereum.agents.core.bridge.battle.NewCancelTransferRequestEvent;
import org.sysethereum.agents.core.bridge.battle.SuperblockFailedEvent;
import org.sysethereum.agents.core.eth.BridgeTransferInfo;
import org.sysethereum.agents.core.syscoin.*;
import org.sysethereum.agents.core.eth.EthWrapper;
import org.sysethereum.agents.core.eth.SuperblockSPVProof;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.sysethereum.agents.service.ChallengeEmailNotifier;
import org.sysethereum.agents.service.PersistentFileStore;
import org.sysethereum.agents.util.RandomizationCounter;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Paths;
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
    private final SystemProperties config;
    private final PersistentFileStore persistentFileStore;
    private final SuperblockChain localSuperblockChain;
    private final SuperblockContractApi superblockContractApi;
    private final ClaimContractApi claimContractApi;
    private final BattleContractApi battleContractApi;
    private final ERC20ManagerContractApi erc20ManagerContractApi;

    private HashSet<Keccak256Hash> semiApprovedSet;
    private final File semiApprovedSetFile;
    private final SyscoinToEthClient syscoinToEthClient;
    private final SyscoinRPCClient syscoinRPCClient;
    public SuperblockChallengerClient(
            SystemProperties config,
            AgentConstants agentConstants,
            PersistentFileStore persistentFileStore,
            EthWrapper ethWrapper,
            SuperblockChain superblockChain,
            EthAddresses ethAddresses,
            SuperblockContractApi superblockContractApi,
            ClaimContractApi claimContractApi,
            BattleContractApi battleContractApi,
            ERC20ManagerContractApi erc20ManagerContractApi,
            SyscoinBattleManagerExtended battleManagerForChallenges,
            ChallengeEmailNotifier challengeEmailNotifier,
            SyscoinToEthClient syscoinToEthClient,
            SyscoinRPCClient syscoinRPCClient
    ) {
        super(AgentRole.CHALLENGER, config, agentConstants, ethWrapper, superblockContractApi, battleContractApi, claimContractApi, challengeEmailNotifier);

        this.config = config;
        this.persistentFileStore = persistentFileStore;
        this.localSuperblockChain = superblockChain;
        this.superblockContractApi = superblockContractApi;
        this.claimContractApi = claimContractApi;
        this.battleContractApi = battleContractApi;

        this.randomizationCounter = new RandomizationCounter();
        this.battleManagerForChallenges = battleManagerForChallenges;
        this.erc20ManagerContractApi = erc20ManagerContractApi;
        this.myAddress = ethAddresses.challengerAddress;

        this.semiApprovedSet = new HashSet<>();
        this.semiApprovedSetFile = Paths.get(config.dataDirectory(), "SemiApprovedSet.dat").toAbsolutePath().toFile();
        this.syscoinToEthClient = syscoinToEthClient;
        this.syscoinRPCClient = syscoinRPCClient;
    }

    @Override
    public long reactToEvents(long fromBlock, long toBlock) {
        try {
            validateNewSuperblocks(fromBlock, toBlock);
            respondToNewBattles(fromBlock, toBlock);

            // Maintain data structures
            getSemiApproved(fromBlock, toBlock);
            getApproved(fromBlock, toBlock);

            challengerWonBattles(fromBlock, toBlock);
            getCancelTransferRequests(fromBlock, toBlock);
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
    private BlockSPVProof GetBlockSPVProof(String txid){
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("method", "syscoingetspvproof");
        params.put("txid", txid);
        String response;
        try {
            String method = params.get("method");
            params.remove("method");
            ArrayList<Object> paramList = new ArrayList<>(params.values());
            response = syscoinRPCClient.makeCoreCall(method, paramList);
            Gson gson = new Gson();
            return gson.fromJson(response, BlockSPVProof.class);
        } catch (Exception e) {

        }
        return null;
    }
    private Object GetSuperblockSPVProof(String blockhash){
        try {
            return syscoinToEthClient.getSuperblockSPVProof(Sha256Hash.wrap(blockhash), 0, false);
        }
        catch(Exception e){

        }
        return null;
    }
    private SyscoinMintProof GetSysTXIDFromBridgeTransferID(String bridgeTransferID){
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("method", "syscoincheckmint");
        params.put("bridgeTransferID", bridgeTransferID);
        String response;
        try {
            String method = params.get("method");
            params.remove("method");
            ArrayList<Object> paramList = new ArrayList<>(params.values());
            response = syscoinRPCClient.makeCoreCall(method, paramList);
            Gson gson = new Gson();
            return gson.fromJson(response, SyscoinMintProof.class);
        } catch (Exception e) {

        }
        return null;
    }



    /* ---- CHALLENGING ---- */

    /* - Reacting to elapsed time */

    private void invalidateNonMainChainSuperblocks() throws Exception {
        for (Keccak256Hash superblockId : semiApprovedSet) {
            long semiApprovedHeight = superblockContractApi.getHeight(superblockId).longValue();
            Superblock mainChainSuperblock = localSuperblockChain.getByHeight(semiApprovedHeight);
            if (mainChainSuperblock != null) {
                if (!mainChainSuperblock.getHash().equals(superblockId) && superblockContractApi.getChainHeight().longValue() >= semiApprovedHeight) {
                    logger.info("Semi-approved superblock {} not found in main chain. Invalidating.", superblockId);
                    claimContractApi.rejectClaim(superblockId);
                }
            }
        }
    }

    private void invalidateLoserSuperblocks() throws Exception {
        Iterator<Keccak256Hash> i = sessionToSuperblockMap.iterator();
        while (i.hasNext()){
            Keccak256Hash superblockId = i.next();
            // decided is set to true inside of checkClaimFinished and thus only allows it to call once
            if (claimContractApi.getClaimInvalid(superblockId) && claimContractApi.getClaimExists(superblockId) && !claimContractApi.getClaimDecided(superblockId)) {
                logger.info("Superblock {} lost a battle. Invalidating.", superblockId);
                claimContractApi.checkClaimFinished(superblockId, true);
            }
        }
    }


    /* - Reacting to events */

    /**
     * Filters battles where this challenger won challenge on the superblock,
     * and deletes them from active battle/semi approved set and pays out the deposit if configuration is set to do so
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    protected void challengerWonBattles(long fromBlock, long toBlock) throws Exception {
        List<SuperblockFailedEvent> events = claimContractApi.getSuperblockClaimFailedEvents(fromBlock, toBlock);
        boolean removeFromContract = false;
        for (SuperblockFailedEvent event : events) {
            if (isMine(event)) {
                logger.info("Challenger won battle on superblock {} process counter {}",
                        event.superblockHash, event.processCounter.getValue().intValue());
                if (sessionToSuperblockMap.contains(event.superblockHash)) {
                    sessionToSuperblockMap.remove(event.superblockHash);
                    removeFromContract = true;
                }
                if (semiApprovedSet.contains(event.superblockHash)) {
                    semiApprovedSet.remove(event.superblockHash);
                    removeFromContract = true;
                }
            }
        }
        if (removeFromContract && config.isWithdrawFundsEnabled()) {
            claimContractApi.withdrawAllFundsExceptLimit(AgentRole.CHALLENGER, myAddress);
        }
    }
    /**
     * Starts challenges for all new superblocks that aren't in the challenger's local chain.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    private void validateNewSuperblocks(long fromBlock, long toBlock) throws Exception {
        List<SuperblockContractApi.SuperblockEvent> newSuperblockEvents = superblockContractApi.getNewSuperblocks(fromBlock, toBlock);
        List<Keccak256Hash> toChallenge = new ArrayList<>();
        for (SuperblockContractApi.SuperblockEvent newSuperblock : newSuperblockEvents) {
            logger.info("NewSuperblock {}. Validating...", newSuperblock.superblockId);

            Superblock superblock = localSuperblockChain.getByHash(newSuperblock.superblockId);
            if (superblock == null) {
                BigInteger height = superblockContractApi.getHeight(newSuperblock.superblockId);
                Superblock localSuperblock = localSuperblockChain.getByHeight(height.longValue());

                if (localSuperblock == null) {
                    // local superblock chain should not be out of sync because there is 2 hour discrepancy between saving and sending
                    // this could mean our local syscoin node is out of sync (out of our control) in which case we have no choice but to challenge
                    // we have to assume if your local syscoin node is forked or not synced and we cannot detect difference between bad and good SB in that case we must challenge
                    logger.info("Superblock {} not present in our superblock chain", newSuperblock.superblockId);
                } else {
                    logger.info("Superblock {} at height {} is replaced by {} in our superblock chain",
                            newSuperblock.superblockId,
                            height,
                            localSuperblock.getHash());
                }

                toChallenge.add(newSuperblock.superblockId);
            } else {
                logger.info("Superblock height: {}... superblock present in our superblock chain", superblock.getHeight());
            }
        }
        // check for pending if we have superblocks to challenge
        if (toChallenge.size() > 0) {
            Thread.sleep(500); // in case the transaction takes some time to complete
            if (ethWrapper.arePendingTransactionsForChallengerAddress()) {
                throw new Exception("Skipping challenging superblocks, there are pending transaction for the challenger address.");
            }
        }
        for (Keccak256Hash superblockId : toChallenge) {
            claimContractApi.challengeSuperblock(superblockId);
        }
    }


    /**
     * Saves all new battle events that the challenger is taking part in.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    private void respondToNewBattles(long fromBlock, long toBlock) throws Exception {
        List<NewBattleEvent> newBattleEvents = battleContractApi.getNewBattleEvents(fromBlock, toBlock);
        // switch modes and only when we aren't looking back 5k blocks (initial sync)
        if(newBattleEvents.size() > 0 && fromBlock != (toBlock - 5000)){
            // aggressive mode
            if(!ethWrapper.getAggressiveMode()) {
                logger.info("Switching to aggressive mode...");
                // only set to aggressive mode on timer if its the first time, since it clears it and next time shouldn't clear this timer
                ethWrapper.setAggressiveMode(true);
                syscoinToEthClient.setupTimer();
            }
        }
        for (NewBattleEvent newBattleEvent : newBattleEvents) {
            if (isMyBattleEvent(newBattleEvent) && battleContractApi.sessionExists(newBattleEvent.superblockHash)) {
                sessionToSuperblockMap.add(newBattleEvent.superblockHash);
            }
        }
    }
    private void updateAggressiveMode(boolean bMode){
        // set timer back to normal
        if(ethWrapper.getAggressiveMode()) {
            logger.info("Switching back to normal mode from aggressive...");
            // only set timer to normal delay once, since this will be latching when we go from challenge to new superblock
            ethWrapper.setAggressiveMode(bMode);
            syscoinToEthClient.setupTimer();
        }
        randomizationCounter.updateRandomValue();
    }
    /**
     * Gets new approved superblocks to track aggressive mode
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    private void getApproved(long fromBlock, long toBlock) throws Exception {
        List<SuperblockContractApi.SuperblockEvent> approvedSuperblockEvents =
                superblockContractApi.getSemiApprovedSuperblocks(fromBlock, toBlock);
        // switch modes and only when we aren't looking back 5k blocks (initial sync)
        if(approvedSuperblockEvents.size() > 0 && fromBlock != (toBlock - 5000)){
            // set timer back to normal
            updateAggressiveMode(false);
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
        // switch modes and only when we aren't looking back 5k blocks (initial sync)
        if(semiApprovedSuperblockEvents.size() > 0 && fromBlock != (toBlock - 5000)){
            // set timer back to normal
            updateAggressiveMode(false);
        }
        for (SuperblockContractApi.SuperblockEvent superblockEvent : semiApprovedSuperblockEvents) {
            if (challengedByMe(superblockEvent))
                semiApprovedSet.add(superblockEvent.superblockId);
        }
    }
    /**
     * Watches for CancelTransferRequest events and sees if the bridge id exists on Syscoin meaning the cancel request should be invalid, challenge it.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    private void getCancelTransferRequests(long fromBlock, long toBlock) throws Exception {
        List<NewCancelTransferRequestEvent> cancelTransfersList =
                erc20ManagerContractApi.getNewCancelTransferEvents(fromBlock, toBlock);

        for (NewCancelTransferRequestEvent cancelTransferRequest : cancelTransfersList) {
            logger.info("Found cancel transfer request for id {}...", cancelTransferRequest.bridgeTransferId.toString());
            // lookup eth txid to get sys txid and if exists then we may want to challenge
            SyscoinMintProof mintProof = GetSysTXIDFromBridgeTransferID(cancelTransferRequest.bridgeTransferId.toString());
            // if sys txid exists for this transfer id
            if(mintProof != null) {
                logger.info("Checking to see if the transfer is still valid to challenge...");
                // check if cancellation request is still valid
                BridgeTransferInfo bridgeTransferInfo = erc20ManagerContractApi.getBridgeTransfer(cancelTransferRequest.bridgeTransferId);
                if(bridgeTransferInfo.status == BridgeTransferInfo.BridgeTransferStatus.CancelRequested) {
                    logger.info("Getting SPV proofs and challenging bridge transfer cancellation...");
                    // get SPV proof of sys tx linking to block
                    BlockSPVProof blockSPVProof = GetBlockSPVProof(mintProof.txid);
                    // get SPV proof of block linking to superblock
                    SuperblockSPVProof superblockSPVProof = (SuperblockSPVProof) GetSuperblockSPVProof(blockSPVProof.blockhash);
                    // fill merkle proof in siblings section of blocksSPVProof
                    syscoinToEthClient.fillBlockSPVProof(blockSPVProof, Sha256Hash.wrap(mintProof.txid));

                    Thread.sleep(500); // let pending transactions propogate
                    if (ethWrapper.arePendingTransactionsForChallengerAddress()) {
                        throw new Exception("Skipping challenging cancel transfer, there are pending transaction for the challenger address.");
                    }
                    // submit spv proof of sys tx to claim submitters deposit and close session
                    superblockContractApi.challengeCancelTransfer(blockSPVProof, superblockSPVProof);
                }
            }
            else{
                logger.info("Could not find Syscoin Mint Proof to challenge with...");
            }
        }

    }
    /* ---- HELPER METHODS ---- */

    private boolean challengedByMe(SuperblockContractApi.SuperblockEvent superblockEvent) throws Exception {
        return claimContractApi.getClaimChallenger(superblockEvent.superblockId).getValue().equalsIgnoreCase(myAddress);
    }

    protected void callBattleTimeouts() throws Exception {
        Iterator<Keccak256Hash> i = sessionToSuperblockMap.iterator();
        while (i.hasNext()){
            Keccak256Hash superblockId = i.next();
            if (battleContractApi.getSubmitterHitTimeout(superblockId) && battleContractApi.sessionExists(superblockId)) {
                logger.info("Submitter hit timeout on superblock {}. Calling timeout.", superblockId);
                ethWrapper.timeout(superblockId, battleManagerForChallenges);
            }
        }
    }
    private boolean isMine(SuperblockFailedEvent superblockFailedEvent) {
        return superblockFailedEvent.challenger.equalsIgnoreCase(myAddress);
    }

    @Override
    protected void restoreFiles() throws ClassNotFoundException, IOException {
        latestEthBlockProcessed = persistentFileStore.restore(latestEthBlockProcessed, latestEthBlockProcessedFile);
        sessionToSuperblockMap = persistentFileStore.restore(sessionToSuperblockMap, sessionToSuperblockMapFile);
        semiApprovedSet = persistentFileStore.restore(semiApprovedSet, semiApprovedSetFile);
    }

    @Override
    protected void flushFiles() throws IOException {
        persistentFileStore.flush(latestEthBlockProcessed, latestEthBlockProcessedFile);
        persistentFileStore.flush(sessionToSuperblockMap, sessionToSuperblockMapFile);
        persistentFileStore.flush(semiApprovedSet, semiApprovedSetFile);
    }

}
