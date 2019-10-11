package org.sysethereum.agents.core.eth;

import com.google.common.primitives.Bytes;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.bitcoinj.store.BlockStoreException;
import org.sysethereum.agents.constants.AgentRole;
import org.sysethereum.agents.constants.EthAddresses;
import org.sysethereum.agents.constants.SystemProperties;
import org.sysethereum.agents.contract.*;
import org.sysethereum.agents.core.bridge.BattleContractApi;
import org.sysethereum.agents.core.bridge.ClaimContractApi;
import org.sysethereum.agents.core.bridge.Superblock;
import org.sysethereum.agents.core.bridge.SuperblockContractApi;
import org.sysethereum.agents.core.syscoin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.generated.Bytes32;

import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Helps the agent communication with the Eth blockchain.
 * @author Oscar Guindzberg
 * @author Catalina Juarros
 */
@Component
@Slf4j(topic = "EthWrapper")
public class EthWrapper {

    private static final Logger logger = LoggerFactory.getLogger("EthWrapper");

    private final Web3j web3;
    private final Web3j web3Secondary;
    private final EthAddresses ethAddresses;

    public enum ChallengeState {
        @SuppressWarnings("unused")
        Unchallenged,            // Unchallenged submission
        Challenged               // Claims was challenged
    }

    // Extensions of contracts generated automatically by web3j
    private final SyscoinClaimManagerExtended claimManager;
    private final SyscoinClaimManagerExtended claimManagerForChallenges;
    private final SyscoinBattleManagerExtended battleManager;
    private final SyscoinBattleManagerExtended battleManagerForChallengesGetter;

    private BigInteger gasPriceMinimum;
    private final BigInteger gasPriceMaximum;

    private final SuperblockContractApi superblockContractApi;
    private final BattleContractApi battleContractApi;
    private final ClaimContractApi claimContractApi;
    private final BigInteger superblockDuration;

    private final BigInteger minProposalDeposit;
    private final SuperblockChain superblockChain;
    private final SyscoinWrapper syscoinWrapper;

    @Autowired
    public EthWrapper(
            SystemProperties config,
            SuperblockChain superblockChain,
            SyscoinWrapper syscoinWrapper,
            Web3j web3,
            Web3j web3Secondary,
            EthAddresses ethAddresses,
            SyscoinBattleManagerExtended battleManager,
            SyscoinBattleManagerExtended battleManagerForChallengesGetter,
            SyscoinClaimManagerExtended claimManager,
            SyscoinClaimManagerExtended claimManagerGetter,
            SyscoinClaimManagerExtended claimManagerForChallenges,
            SuperblockContractApi superblockContractApi,
            BattleContractApi battleContractApi,
            ClaimContractApi claimContractApi,
            BigInteger superblockDuration
    ) throws Exception {

        this.superblockChain = superblockChain;
        this.syscoinWrapper = syscoinWrapper;
        this.web3 = web3;
        this.web3Secondary = web3Secondary;
        this.ethAddresses = ethAddresses;
        this.battleManager = battleManager;
        this.battleManagerForChallengesGetter = battleManagerForChallengesGetter;
        this.claimManager = claimManager;
        this.claimManagerForChallenges = claimManagerForChallenges;
        this.superblockContractApi = superblockContractApi;
        this.battleContractApi = battleContractApi;
        this.claimContractApi = claimContractApi;
        this.superblockDuration = superblockDuration;

        gasPriceMinimum = BigInteger.valueOf(config.gasPriceMinimum());
        gasPriceMaximum = BigInteger.valueOf(config.gasPriceMaximum());
        updateContractFacadesGasPrice();

        minProposalDeposit = claimManagerGetter.minProposalDeposit().send().getValue();
    }


    /**
     * Returns height of the Ethereum blockchain.
     * @return Ethereum block count.
     * @throws IOException
     */
    public long getEthBlockCount() throws IOException {
        return web3.ethBlockNumber().send().getBlockNumber().longValue();
    }

    public boolean isEthNodeSyncing() throws IOException {
        return web3.ethSyncing().send().isSyncing();
    }

    public boolean arePendingTransactionsForSendSuperblocksAddress() throws InterruptedException,IOException {
        return arePendingTransactionsFor(ethAddresses.generalPurposeAndSendSuperblocksAddress);
    }

    public boolean arePendingTransactionsForChallengerAddress() throws InterruptedException, IOException {
        return arePendingTransactionsFor(ethAddresses.syscoinSuperblockChallengerAddress);
    }

    /**
     * Checks if there are pending transactions for a given contract.
     * @param address
     * @return
     * @throws IOException
     */
    private boolean arePendingTransactionsFor(String address) throws InterruptedException, IOException {
        BigInteger latest = web3Secondary.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send().getTransactionCount();
        BigInteger pending;
        try{
            pending = web3.ethGetTransactionCount(address, DefaultBlockParameterName.PENDING).send().getTransactionCount();
        }
        catch(Exception e){
            Thread.sleep(500);
            pending = web3Secondary.ethGetTransactionCount(address, DefaultBlockParameterName.PENDING).send().getTransactionCount();
        }
        return pending.compareTo(latest) > 0;
    }

    /**
     * Sets gas prices for all contract instances.
     * @throws IOException
     */
    public void updateContractFacadesGasPrice() throws IOException {
        BigInteger gasPriceSuggestedByEthNode = web3Secondary.ethGasPrice().send().getGasPrice();
        if (gasPriceSuggestedByEthNode.compareTo(gasPriceMinimum) > 0) {
            if (gasPriceSuggestedByEthNode.compareTo(gasPriceMaximum) > 0) {
                gasPriceSuggestedByEthNode = gasPriceMaximum;
            }
            if(!gasPriceMinimum.equals(gasPriceSuggestedByEthNode)) {
                gasPriceMinimum = gasPriceSuggestedByEthNode;
                logger.info("setting new min gas price to " + gasPriceMinimum);

                claimContractApi.updateGasPrice(gasPriceMinimum);
                battleContractApi.updateGasPrice(gasPriceMinimum);
                superblockContractApi.updateGasPrice(gasPriceMinimum);
            }
        }
    }

    /* ---------------------------------- */
    /* - Relay Syscoin superblocks section - */
    /* ---------------------------------- */
    /**
     * Helper method for confirming a semi-approved superblock.
     * Finds the highest semi-approved or new superblock in the main chain that comes after a given semi-approved superblock.
     * @param superblockId Superblsock to be confirmed.
     * @return Highest superblock in main chain that's newer than the given superblock
     *         if such a superblock exists, null otherwise (i.e. given superblock isn't in main chain
     *         or has no semi-approved descendants).
     * @throws BlockStoreException
     * @throws IOException
     * @throws Exception
     */
    public Superblock getHighestApprovableOrNewDescendant(Superblock toConfirm, Keccak256Hash superblockId)
            throws BlockStoreException, IOException, Exception {
        if (superblockChain.getSuperblock(superblockId) == null) {
            // The superblock isn't in the main chain.
            logger.info("Superblock {} is not in the main chain. Returning from getHighestApprovableOrNewDescendant.", superblockId);
            return null;
        }

        if (superblockChain.getSuperblock(superblockId).getSuperblockHeight() == superblockChain.getChainHeight()) {
            // There's nothing above the tip of the chain.
            logger.info("Superblock {} is above the tip of the chain. Returning from getHighestApprovableOrNewDescendant.", superblockId);
            return null;
        }
        Superblock currentSuperblock = superblockChain.getChainHead();
        while (currentSuperblock != null &&
                !currentSuperblock.getSuperblockId().equals(superblockId) &&
                !newAndTimeoutPassed(currentSuperblock.getSuperblockId()) &&
                !claimContractApi.getInBattleAndSemiApprovable(currentSuperblock.getSuperblockId()) &&
                !semiApprovedAndApprovable(toConfirm, currentSuperblock)) {
            currentSuperblock = superblockChain.getSuperblock(currentSuperblock.getParentId());
        }
        return currentSuperblock;
    }
    /**
     * Helper method for confirming a semi-approved/approved superblock.
     * Finds the highest semi-approved or approved in the main chain that comes after a given superblock.
     * @param superblockId Superblock to be confirmed.
     * @return Highest superblock in main chain that's newer than the given superblock
     *         if such a superblock exists, null otherwise (i.e. given superblock isn't in main chain
     *         or has no semi-approved/approved descendants).
     * @throws BlockStoreException
     * @throws IOException
     * @throws Exception
     */
    public Superblock getHighestSemiApprovedOrApprovedDescendant(Keccak256Hash superblockId)
            throws BlockStoreException, IOException, Exception {
        if (superblockChain.getSuperblock(superblockId) == null) {
            // The superblock isn't in the main chain.
            logger.info("Superblock {} is not in the main chain. Returning from getHighestSemiApprovedOrApprovedDescendant.", superblockId);
            return null;
        }

        if (superblockChain.getSuperblock(superblockId).getSuperblockHeight() == superblockChain.getChainHeight()) {
            // There's nothing above the tip of the chain.
            logger.info("Superblock {} is the tip of the superblock chain, no descendant exists. Returning from getHighestSemiApprovedOrApprovedDescendant.", superblockId);
            return null;
        }
        Superblock currentSuperblock = superblockChain.getChainHead();
        while (currentSuperblock != null
                && !currentSuperblock.getSuperblockId().equals(superblockId)
                && !superblockContractApi.isSemiApproved(currentSuperblock.getSuperblockId())
                && !superblockContractApi.isApproved(currentSuperblock.getSuperblockId())) {
            currentSuperblock = superblockChain.getSuperblock(currentSuperblock.getParentId());
        }

        return currentSuperblock;
    }
    /**
     * Proposes a superblock to SyscoinClaimManager in order to keep the Sysethereum contracts updated.
     * @param superblock Oldest superblock that is already stored in the local database,
     *                   but still hasn't been submitted to Sysethereum Contracts.
     * @throws Exception If superblock hash cannot be calculated.
     */
    public boolean sendStoreSuperblock(Superblock superblock, String account) throws Exception {

        // Check if the parent has been approved before sending this superblock.
        Keccak256Hash parentId = superblock.getParentId();
        if (!(superblockContractApi.isApproved(parentId) || superblockContractApi.isSemiApproved(parentId))) {
            logger.info("Superblock {} not sent because its parent was neither approved nor semi approved.",
                    superblock.getSuperblockId());
            return false;
        }
        // if claim exists we check to ensure the superblock chain isn't "stuck" and can be re-approved to be built even if it exists
        if (claimContractApi.getClaimExists(superblock.getSuperblockId())){
            boolean allowed = claimContractApi.getClaimInvalid(superblock.getSuperblockId())
                    && claimContractApi.getClaimDecided(superblock.getSuperblockId())
                    && !claimContractApi.getClaimSubmitter(superblock.getSuperblockId()).equals(account);

            if(allowed){
                if(superblockContractApi.isApproved(parentId)){
                    allowed = superblockContractApi.getBestSuperblockId().equals(parentId);
                }
                else allowed = superblockContractApi.isSemiApproved(parentId);
            }
           if(!allowed){
               logger.info("Superblock {} has already been sent. Returning.", superblock.getSuperblockId());
               return false;
            }
        }


        logger.info("About to send superblock {} to the bridge.", superblock.getSuperblockId());
        Thread.sleep(500);
        if (arePendingTransactionsForSendSuperblocksAddress()) {
            logger.debug("Skipping sending superblocks, there are pending transaction for the sender address.");
            return false;
        }

        // Make any necessary deposits for sending the superblock
        claimContractApi.makeDepositIfNeeded(AgentRole.SUBMITTER, account, getSuperblockDeposit());

        // The parent is either approved or semi approved. We can send the superblock.
        CompletableFuture<TransactionReceipt> futureReceipt = proposeSuperblock(superblock);

        logger.info("Sent superblock {}", superblock.getSuperblockId());
        futureReceipt.handle((receipt, throwable) -> {
            if (receipt != null) {
                logger.info("proposeSuperblock receipt {}", receipt.toString());
            } else {
                logger.info("proposeSuperblock EXCEPTION:", throwable);
            }
            return true;
        });

        Thread.sleep(200);
        return true;
    }

    /**
     * Proposes a superblock to SyscoinClaimManager. To be called from sendStoreSuperblock.
     * @param superblock Superblock to be proposed.
     * @return
     */
    private CompletableFuture<TransactionReceipt> proposeSuperblock(Superblock superblock) {
        return claimManager.proposeSuperblock(
                new Bytes32(superblock.getMerkleRoot().getBytes()),
                new Uint256(superblock.getChainWork()),
                new Uint256(superblock.getLastSyscoinBlockTime()),
                new Uint256(superblock.getLastSyscoinBlockMedianTime()),
                new Bytes32(superblock.getLastSyscoinBlockHash().getBytes()),
                new Uint32(superblock.getlastSyscoinBlockBits()),
                new Bytes32(superblock.getParentId().getBytes())
            ).sendAsync();
    }


    /**
     * Returns the initial deposit for proposing a superblock, i.e. enough to cover the challenge,
     * all battle steps and a reward for the opponent in case the battle is lost.
     * This deposit only covers one battle and it's meant to optimise the number of transactions performed
     * by the submitter - it's still necessary to make a deposit for each step if another battle is carried out
     * over the same superblock.
     * @return Initial deposit for covering a reward and a single battle.
     */
    private BigInteger getSuperblockDeposit() {
        return minProposalDeposit;
    }

    /**
     * Returns the initial deposit for challenging a superblock, just a best guess based on
     * 60 requests max for block headers and the final verify superblock cost
     * @return Initial deposit for covering single battle during a challenge.
     */
    private BigInteger getChallengeDeposit() {
        return minProposalDeposit;
    }



    /* ---------------------------------- */
    /* ---- SyscoinClaimManager section ---- */
    /* ---------------------------------- */


    /* ---- CONFIRMING/REJECTING ---- */

    /**
     * Approves, semi-approves or invalidates a superblock depending on its situation.
     * See SyscoinClaimManager source code for further reference.
     * @param superblockId Superblock to be approved, semi-approved or invalidated.
     * @param isChallenger Whether the caller is challenging. Used to determine
     *                     which SyscoinClaimManager should be used for withdrawing funds.
     */
    public void checkClaimFinished(Keccak256Hash superblockId, boolean isChallenger) {
        SyscoinClaimManagerExtended myClaimManager;
        if (isChallenger) {
            myClaimManager = claimManagerForChallenges;
        } else {
            myClaimManager = claimManager;
        }

        CompletableFuture<TransactionReceipt> futureReceipt =
                myClaimManager.checkClaimFinished(new Bytes32(superblockId.getBytes())).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                logger.info("checkClaimFinished receipt {}", receipt.toString())
        );
    }

    /**
     * Confirms a semi-approved superblock with a high enough semi-approved descendant;
     * 'high enough' means that superblock.height - descendant.height is greater than or equal
     * to the number of confirmations necessary for appoving a superblock.
     * See SyscoinClaimManager source code for further reference.
     * @param superblockId Superblock to be confirmed.
     * @param descendantId Its highest semi-approved descendant.
     */
    public void confirmClaim(Keccak256Hash superblockId, Keccak256Hash descendantId) {
        CompletableFuture<TransactionReceipt> futureReceipt =
                claimManager.confirmClaim(new Bytes32(superblockId.getBytes()), new Bytes32(descendantId.getBytes())).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                logger.info("confirmClaim receipt {}", receipt.toString())
        );
    }

    /**
     * Rejects a claim.
     * See SyscoinClaimManager source code for further reference.
     * @param superblockId ID of superblock to be rejected.
     */
    public void rejectClaim(Keccak256Hash superblockId) {
        CompletableFuture<TransactionReceipt> futureReceipt =
                claimManager.rejectClaim(new Bytes32(superblockId.getBytes())).sendAsync();
        futureReceipt.thenAcceptAsync( (TransactionReceipt receipt) ->
                logger.info("rejectClaim receipt {}", receipt.toString())
        );
    }


    /* ---- BATTLE EVENT RETRIEVAL METHODS AND CLASSES ---- */

    /**
     * Listens to NewBattle events from SyscoinBattleManager contract within a given block window
     * and parses web3j-generated instances into easier to manage NewBattleEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All NewBattle events from SyscoinBattleManager as NewBattleEvent objects.
     * @throws IOException
     */
    public List<NewBattleEvent> getNewBattleEvents(long startBlock, long endBlock) throws IOException {
        List<NewBattleEvent> result = new ArrayList<>();
        List<SyscoinBattleManager.NewBattleEventResponse> newBattleEvents =
                battleManagerForChallengesGetter.getNewBattleEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (SyscoinBattleManager.NewBattleEventResponse response : newBattleEvents) {
            NewBattleEvent newBattleEvent = new NewBattleEvent();
            newBattleEvent.superblockHash = Keccak256Hash.wrap(response.superblockHash.getValue());
            newBattleEvent.sessionId = Keccak256Hash.wrap(response.sessionId.getValue());
            newBattleEvent.submitter = response.submitter.getValue();
            newBattleEvent.challenger = response.challenger.getValue();
            result.add(newBattleEvent);
        }

        return result;
    }
    /**
     * Listens to RespondBlockHeaders events from SyscoinBattleManager contract within a given block window
     * and parses web3j-generated instances into easier to manage RespondBlockHeaders objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All NewBattle events from SyscoinBattleManager as RespondBlockHeaders objects.
     * @throws IOException
     */
    public List<RespondHeadersEvent> getNewRespondHeadersEvents(long startBlock, long endBlock) throws IOException {
        List<RespondHeadersEvent> result = new ArrayList<>();
        List<SyscoinBattleManager.RespondBlockHeadersEventResponse> newBattleEvents =
                battleManagerForChallengesGetter.getNewBlockHeadersEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (SyscoinBattleManager.RespondBlockHeadersEventResponse response : newBattleEvents) {
            RespondHeadersEvent newRespondHeadersEvent = new RespondHeadersEvent();
            newRespondHeadersEvent.superblockHash = Keccak256Hash.wrap(response.superblockHash.getValue());
            newRespondHeadersEvent.sessionId = Keccak256Hash.wrap(response.sessionId.getValue());
            newRespondHeadersEvent.merkleHashCount = response.merkleHashCount.getValue().intValue();
            newRespondHeadersEvent.submitter = response.submitter.getValue();
            result.add(newRespondHeadersEvent);
        }

        return result;
    }


    /**
     * Listens to ChallengerConvicted events from a given SyscoinBattleManager contract within a given block window
     * and parses web3j-generated instances into easier to manage ChallengerConvictedEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @param myBattleManager SyscoinBattleManager contract that the caller is using to handle its battles.
     * @return All ChallengerConvicted events from SyscoinBattleManager as ChallengerConvictedEvent objects.
     * @throws IOException
     */
    public List<ChallengerConvictedEvent> getChallengerConvictedEvents(long startBlock, long endBlock,
                                                                       SyscoinBattleManagerExtended myBattleManager)
            throws IOException {
        List<ChallengerConvictedEvent> result = new ArrayList<>();
        List<SyscoinBattleManager.ChallengerConvictedEventResponse> challengerConvictedEvents =
                myBattleManager.getChallengerConvictedEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (SyscoinBattleManager.ChallengerConvictedEventResponse response : challengerConvictedEvents) {
            ChallengerConvictedEvent challengerConvictedEvent = new ChallengerConvictedEvent();
            challengerConvictedEvent.superblockHash = Keccak256Hash.wrap(response.superblockHash.getValue());
            challengerConvictedEvent.sessionId = Keccak256Hash.wrap(response.sessionId.getValue());
            challengerConvictedEvent.challenger = response.challenger.getValue();
            result.add(challengerConvictedEvent);
        }

        return result;
    }

    /**
     * Listens to SubmitterConvicted events from a given SyscoinBattleManager contract within a given block window
     * and parses web3j-generated instances into easier to manage SubmitterConvictedEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @param myBattleManager SyscoinBattleManager contract that the caller is using to handle its battles.
     * @return All SubmitterConvicted events from SyscoinBattleManager as SubmitterConvictedEvent objects.
     * @throws IOException
     */
    public List<SubmitterConvictedEvent> getSubmitterConvictedEvents(long startBlock, long endBlock,
                                                                     SyscoinBattleManagerExtended myBattleManager)
            throws IOException {
        List<SubmitterConvictedEvent> result = new ArrayList<>();
        List<SyscoinBattleManager.SubmitterConvictedEventResponse> submitterConvictedEvents =
                myBattleManager.getSubmitterConvictedEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (SyscoinBattleManager.SubmitterConvictedEventResponse response : submitterConvictedEvents) {
            SubmitterConvictedEvent submitterConvictedEvent = new SubmitterConvictedEvent();
            submitterConvictedEvent.superblockHash = Keccak256Hash.wrap(response.superblockHash.getValue());
            submitterConvictedEvent.sessionId = Keccak256Hash.wrap(response.sessionId.getValue());
            submitterConvictedEvent.submitter = response.submitter.getValue();
            result.add(submitterConvictedEvent);
        }

        return result;
    }

    // Event wrapper classes

    public static class NewBattleEvent {
        public Keccak256Hash superblockHash;
        public Keccak256Hash sessionId;
        public String submitter;
        public String challenger;
    }

    public static class ChallengerConvictedEvent {
        public Keccak256Hash superblockHash;
        public Keccak256Hash sessionId;
        public String challenger;
    }

    public static class SubmitterConvictedEvent {
        public Keccak256Hash superblockHash;
        public Keccak256Hash sessionId;
        public String submitter;
    }

    public static class RespondHeadersEvent {
        public Keccak256Hash superblockHash;
        public Keccak256Hash sessionId;
        public int merkleHashCount;
        public String submitter;
    }


    /* ---------------------------------- */
    /* --------- Battle section --------- */
    /* ---------------------------------- */
    /**
     * Responds to a challenge with all block headers
     * @param sessionId Battle session ID.
     */
    public void respondBlockHeaders(Keccak256Hash sessionId, Keccak256Hash superblockId, int merkleHashCount) throws Exception {
        Thread.sleep(500); // in case the transaction takes some time to complete
        if (arePendingTransactionsForSendSuperblocksAddress()) {
            throw new Exception("Skipping respondBlockHeader, there are pending transaction for the sender address.");
        }
        int numHashesRequired = merkleHashCount < 3? 16: 12;
        int startIndex = merkleHashCount*16;
        int endIndex = startIndex + numHashesRequired;
        if(startIndex > 48)
            throw new Exception("Skipping respondBlockHeader, startIndex cannot be > 48.");
        Superblock superblock = superblockChain.getSuperblock(superblockId);
        List<Sha256Hash> listHashes = superblock.getSyscoinBlockHashes();
        if(!superblockDuration.equals(BigInteger.valueOf(listHashes.size())))
            throw new Exception("Skipping respondBlockHeader, superblock hash array list is incorrect length.");

        byte[] blockHeaderBytes = null;
        for(int i = startIndex;i<endIndex;i++){
            AltcoinBlock altBlock = (AltcoinBlock) syscoinWrapper.getBlock(listHashes.get(i)).getHeader();
            byte[] serializedBytes = altBlock.bitcoinSerialize();
            if(blockHeaderBytes == null)
                blockHeaderBytes = serializedBytes;
            else
                blockHeaderBytes = Bytes.concat(blockHeaderBytes, serializedBytes);
        }

        CompletableFuture<TransactionReceipt> futureReceipt = battleManager.respondBlockHeaders(
                new Bytes32(sessionId.getBytes()), new DynamicBytes(blockHeaderBytes), new Uint256(numHashesRequired)).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                logger.info("Responded to last block header query for Syscoin superblock {} session {}, Receipt: {}",
                        superblockId, sessionId, receipt)
        );


    }
    // TODO: see if the challenger should know which superblock this is


    /**
     * Calls timeout for a session where a participant hasn't responded in time, thus closing the battle.
     * @param sessionId Battle session ID.
     * @param myBattleManager SyscoinBattleManager contract that the caller is using to handle its battles.
     */
    public void timeout(Keccak256Hash sessionId, SyscoinBattleManagerExtended myBattleManager) {
        CompletableFuture<TransactionReceipt> futureReceipt = myBattleManager.timeout(new Bytes32(sessionId.getBytes())).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                logger.info("Called timeout for session {}", sessionId));
    }


    /* ---- CHALLENGER ---- */

    /**
     * Challenges a superblock.
     * @param superblockId Hash of superblock to be challenged.
     * @param account Caller's address.
     * @throws Exception
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean challengeSuperblock(Keccak256Hash superblockId, String account) throws Exception {
        if(!claimContractApi.getClaimExists(superblockId) || claimContractApi.getClaimDecided(superblockId)) {
            logger.info("Superblock has already been decided upon or claim doesn't exist, skipping...{}", superblockId.toString());
            return false;
        }

        if(claimContractApi.getClaimSubmitter(superblockId).equals(ethAddresses.syscoinSuperblockChallengerAddress)){
            logger.info("You cannot challenge a superblock you have submitted yourself, skipping...{}", superblockId.toString());
            return false;
        }

        // Make necessary deposit to cover reward
        claimContractApi.makeDepositIfNeeded(AgentRole.CHALLENGER, account, getChallengeDeposit());

        CompletableFuture<TransactionReceipt> futureReceipt =
                claimManagerForChallenges.challengeSuperblock(new Bytes32(superblockId.getBytes())).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                logger.info("challengeSuperblock receipt {}", receipt.toString()));
        return true;
    }

    /**
     * Checks if a superblock is semi-approved and has enough confirmations, i.e. semi-approved descendants.
     * To be used after finding a descendant with getHighestApprovableOrNewDescendant.
     * @param superblock Superblock to be confirmed.
     * @param descendant Highest semi-approved descendant of superblock to be confirmed.
     * @return True if the superblock can be safely approved, false otherwise.
     * @throws Exception
     */
    public boolean semiApprovedAndApprovable(Superblock superblock, Superblock descendant) throws Exception {
        Keccak256Hash superblockId = superblock.getSuperblockId();
        Keccak256Hash descendantId = descendant.getSuperblockId();
        return (descendant.getSuperblockHeight() - superblock.getSuperblockHeight() >=
                claimContractApi.getSuperblockConfirmations() &&
                superblockContractApi.isSemiApproved(descendantId) &&
                superblockContractApi.isSemiApproved(superblockId));
    }


    public boolean newAndTimeoutPassed(Keccak256Hash superblockId) throws Exception {
        return (superblockContractApi.isNew(superblockId) && claimContractApi.submittedTimeoutPassed(superblockId));
    }

}