package org.dogethereum.agents.core.eth;


import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;

import org.dogethereum.agents.constants.AgentConstants;
import org.dogethereum.agents.constants.SystemProperties;
import org.dogethereum.agents.contract.*;

import org.dogethereum.agents.core.dogecoin.*;
import org.dogethereum.agents.core.dogecoin.SuperblockUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.spongycastle.util.encoders.Hex;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import org.web3j.tuples.generated.Tuple3;
import org.web3j.tuples.generated.Tuple7;

import org.web3j.tx.ClientTransactionManager;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helps the agent communication with the Eth blockchain.
 * @author Oscar Guindzberg
 * @author Catalina Juarros
 */
@Component
@Slf4j(topic = "EthWrapper")
public class EthWrapper implements SuperblockConstantProvider {

    private Web3j web3;

    private DogeTokenExtended dogeToken;
    private DogeClaimManagerExtended claimManager;
    private DogeClaimManagerExtended claimManagerForChallenges;
    private DogeSuperblocksExtended superblocks;
    private DogeSuperblocksExtended superblocksForRelayTxs;

    private SystemProperties config;
    private BigInteger gasPriceMinimum;

    private String generalPurposeAndSendSuperblocksAddress;
    private String relayTxsAddress;
    private String priceOracleAddress;
    private String dogeBlockChallengerAddress;

    /* ---------------------------------- */
    /* ------ General code section ------ */
    /* ---------------------------------- */

    @Autowired
    public EthWrapper() throws Exception {
        config = SystemProperties.CONFIG;
        web3 = Web3j.build(new HttpService());  // defaults to http://localhost:8545/
        String dogeTokenContractAddress;
        String claimManagerContractAddress;
        String superblocksContractAddress;

        if (config.isGanache()) {
            dogeTokenContractAddress = getContractAddress("DogeToken");
            claimManagerContractAddress = getContractAddress("DogeClaimManager");
            superblocksContractAddress = getContractAddress("DogeSuperblocks");
            List<String> accounts = web3.ethAccounts().send().getAccounts();
            generalPurposeAndSendSuperblocksAddress = accounts.get(0);
            relayTxsAddress = accounts.get(1);
            priceOracleAddress = accounts.get(2);
            dogeBlockChallengerAddress = accounts.get(3);
        } else {
            dogeTokenContractAddress = config.dogeTokenContractAddress();
            claimManagerContractAddress = config.dogeClaimManagerContractAddress();
            superblocksContractAddress = config.dogeSuperblocksContractAddress();
            generalPurposeAndSendSuperblocksAddress = config.generalPurposeAndSendSuperblocksAddress();
            relayTxsAddress = config.relayTxsAddress();
            priceOracleAddress = config.priceOracleAddress();
            dogeBlockChallengerAddress = config.generalPurposeAndSendSuperblocksAddress();
        }

        gasPriceMinimum = BigInteger.valueOf(config.gasPriceMinimum());
        BigInteger gasLimit = BigInteger.valueOf(config.gasLimit());

        dogeToken = DogeTokenExtended.load(dogeTokenContractAddress, web3,
                new ClientTransactionManager(web3, priceOracleAddress),
                gasPriceMinimum, gasLimit);
        assert dogeToken.isValid();
        claimManager = DogeClaimManagerExtended.load(claimManagerContractAddress, web3,
                new ClientTransactionManager(web3, generalPurposeAndSendSuperblocksAddress),
                gasPriceMinimum, gasLimit);
        assert claimManager.isValid();
        claimManagerForChallenges = DogeClaimManagerExtended.load(claimManagerContractAddress, web3,
                new ClientTransactionManager(web3, dogeBlockChallengerAddress),
                gasPriceMinimum, gasLimit);
        assert claimManagerForChallenges.isValid();
        superblocks = DogeSuperblocksExtended.load(superblocksContractAddress, web3,
                new ClientTransactionManager(web3, generalPurposeAndSendSuperblocksAddress),
                gasPriceMinimum, gasLimit);
        assert superblocks.isValid();
        superblocksForRelayTxs = DogeSuperblocksExtended.load(superblocksContractAddress, web3,
                new ClientTransactionManager(web3, relayTxsAddress),
                gasPriceMinimum, gasLimit);
        assert superblocksForRelayTxs.isValid();

    }

    /**
     * Get the deployed contract address from a truffle json file
     *
     * @param contractName
     * @return The contract address
     * @throws Exception
     */
    private String getContractAddress(String contractName) throws Exception {
        String basePath = config.truffleBuildContractsDirectory();
        FileReader contractSpecFile = new FileReader(basePath + "/" + contractName + ".json");
        JSONParser parser = new JSONParser();
        JSONObject parsedSpecFile = (JSONObject) parser.parse(contractSpecFile);
        JSONObject networks = (JSONObject) parsedSpecFile.get("networks");
        JSONObject data = (JSONObject) networks.values().iterator().next();
        return (String) data.get("address");
    }

    public long getEthBlockCount() throws IOException {
        return web3.ethBlockNumber().send().getBlockNumber().longValue();
    }

    public boolean isEthNodeSyncing() throws IOException {
        return web3.ethSyncing().send().isSyncing();
    }

    public void updateContractFacadesGasPrice() throws IOException {
        BigInteger gasPriceSuggestedByEthNode = web3.ethGasPrice().send().getGasPrice();
        BigInteger gasPrice;
        if (gasPriceSuggestedByEthNode.compareTo(gasPriceMinimum) > 0) {
            gasPrice = gasPriceSuggestedByEthNode;
        } else {
            gasPrice = gasPriceMinimum;
        }

        dogeToken.setGasPrice(gasPrice);
        claimManager.setGasPrice(gasPrice);
        claimManagerForChallenges.setGasPrice(gasPrice);
        superblocks.setGasPrice(gasPrice);
        superblocksForRelayTxs.setGasPrice(gasPrice);
    }

    public String getGeneralPurposeAndSendSuperblocksAddress() {
        return generalPurposeAndSendSuperblocksAddress;
    }

    public String getDogeBlockChallengerAddress() {
        return dogeBlockChallengerAddress;
    }

    /**
     * E.g. input: 255
     * Output: "00000000000000000000000000000000000000000000000000000000000000FF"
     */
    private String bigIntegerToHexStringPad64(BigInteger input) {
        String input2 = input.toString(16);
        StringBuilder output = new StringBuilder(input2);
        while (output.length() < 64) {
            output.insert(0, "0");
        }
        return output.toString();
    }


    /* ---------------------------------- */
    /* - Relay Doge superblocks section - */
    /* ---------------------------------- */

    /**
     * Propose a superblock to DogeClaimManager in order to keep Dogethereum Contracts updated.
     *
     * @param superblock Oldest superblock that is already stored in the local database,
     *                   but still hasn't been submitted to Dogethereum Contracts.
     * @throws Exception If superblock hash cannot be calculated.
     */
    public void sendStoreSuperblock(Superblock superblock) throws Exception {
        log.info("About to send superblock {} to the bridge.", superblock.getSuperblockId());

        // Check if the parent has been approved before sending this superblock.
        Keccak256Hash parentId = superblock.getParentId();
        if (!(isSuperblockApproved(parentId) || isSuperblockSemiApproved(parentId))) {
            log.info("Superblock {} not sent because its parent was neither approved nor semi approved.",
                    superblock.getSuperblockId());
            return;
        }

        if (getClaimExists(superblock.getSuperblockId())) {
            log.info("Superblock {} has already been sent. Returning.", superblock.getSuperblockId());
            return;
        }

//        BigInteger bondedDeposit = getBondedDeposit(superblock.getSuperblockId());

        // TODO: see how much wei we should actually send and whether it's a paremeter for this method
        // Idea: make it a configuration variable
        CompletableFuture<TransactionReceipt> depositsReceipt =
                makeClaimDeposit(AgentConstants.getSuperblockInitialDeposit());

        // The parent is either approved or semi approved. We can send the superblock.
        CompletableFuture<TransactionReceipt> futureReceipt = proposeSuperblock(superblock);
        log.info("Sent superblock {}", superblock.getSuperblockId());
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("proposeSuperblock receipt {}", receipt.toString())
        );
        Thread.sleep(200); // TODO: see if this is necessary
    }

    /**
     * Propose a superblock to DogeClaimManager.
     *
     * @param superblock Superblock to be proposed.
     * @return
     */
    private CompletableFuture<TransactionReceipt> proposeSuperblock(Superblock superblock) {
        return claimManager.proposeSuperblock(superblock.getMerkleRoot().getBytes(),
                superblock.getChainWork(),
                BigInteger.valueOf(superblock.getLastDogeBlockTime()),
                BigInteger.valueOf(superblock.getPreviousToLastDogeBlockTime()),
                superblock.getLastDogeBlockHash().getBytes(),
                BigInteger.valueOf(superblock.getLastDogeBlockBits()),
                superblock.getParentId().getBytes()
        ).sendAsync();
    }

    /**
     * Get 9 ancestors of the relay's top superblock:
     * ancestor -1 (parent), ancestor -5, ancestor -25, ancestor -125, ...
     *
     * @return List of 9 ancestors where result[i] = ancestor -(5**i).
     * @throws Exception
     */
    public List<byte[]> getSuperblockLocator() throws Exception {
        return superblocks.getSuperblockLocator().send();
    }

    public boolean wasSuperblockAlreadySubmitted(Keccak256Hash superblockId) throws Exception {
        return !superblocks.getSuperblockIndex(superblockId.getBytes()).send().equals(BigInteger.ZERO);
    }

    private CompletableFuture<TransactionReceipt> makeClaimDeposit(BigInteger weiValue) throws InterruptedException {
        CompletableFuture<TransactionReceipt> futureReceipt = claimManager.makeDeposit(weiValue).sendAsync();
        log.info("Deposited {} wei.", weiValue);

        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("makeClaimDeposit receipt {}", receipt.toString())
        );
        Thread.sleep(200); // in case the transaction takes some time to complete

        return futureReceipt;
    }

    private BigInteger getBondedDeposit(byte[] claimId) throws Exception {
        return claimManager.getBondedDeposit(claimId, generalPurposeAndSendSuperblocksAddress).send();
    }

    public void invalidate(Keccak256Hash superblockId, String validator) throws Exception {
        CompletableFuture<TransactionReceipt> futureReceipt =
                superblocks.invalidate(superblockId.getBytes(), validator).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("Invalidated superblock {}", superblockId));
    }

    /**
     * Return the size of the header as a 4-byte byte[]
     */
    private byte[] calculateHeaderSize(byte[] header) {
        String size = BigInteger.valueOf(header.length).toString(16);
        while (size.length() < 8) {
            size = "0" + size;
        }
        return Hex.decode(size);
    }


    /* ---- SUPERBLOCK STATUS CHECKS ---- */

    private BigInteger getSuperblockStatus(Keccak256Hash superblockId) throws Exception {
        return superblocks.getSuperblockStatus(superblockId.getBytes()).send();
    }

    public boolean isSuperblockApproved(Keccak256Hash superblockId) throws Exception {
        return getSuperblockStatus(superblockId).equals(SuperblockUtils.STATUS_APPROVED);
    }

    public boolean isSuperblockSemiApproved(Keccak256Hash superblockId) throws Exception {
        return getSuperblockStatus(superblockId).equals(SuperblockUtils.STATUS_SEMI_APPROVED);
    }

    public boolean isSuperblockNew(Keccak256Hash superblockId) throws Exception {
        return getSuperblockStatus(superblockId).equals(SuperblockUtils.STATUS_NEW);
    }

    public boolean isSuperblockInBattle(Keccak256Hash superblockId) throws Exception {
        return getSuperblockStatus(superblockId).equals(SuperblockUtils.STATUS_IN_BATTLE);
    }

    public boolean isSuperblockInvalid(Keccak256Hash superblockId) throws Exception {
        return getSuperblockStatus(superblockId).equals(SuperblockUtils.STATUS_INVALID);
    }

    public boolean isSuperblockUninitialized(Keccak256Hash superblockId) throws Exception {
        return getSuperblockStatus(superblockId).equals(SuperblockUtils.STATUS_UNINITIALIZED);
    }

    public boolean statusAllowsConfirmation(Keccak256Hash superblockId) throws Exception {
        return isSuperblockSemiApproved(superblockId) || isSuperblockNew(superblockId);
    }

    public BigInteger getSuperblockHeight(Keccak256Hash superblockId) throws Exception {
        return superblocks.getSuperblockHeight(superblockId.getBytes()).send();
    }

    public List<SuperblockEvent> getNewSuperblocks(long startBlock, long endBlock) throws IOException {
        List<SuperblockEvent> result = new ArrayList<>();
        List<DogeSuperblocks.NewSuperblockEventResponse> newSuperblockEvents =
                superblocks.getNewSuperblockEvents(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeSuperblocks.NewSuperblockEventResponse response : newSuperblockEvents) {
            SuperblockEvent newSuperblockEvent = new SuperblockEvent();
            newSuperblockEvent.superblockId = Keccak256Hash.wrap(response.superblockId);
            newSuperblockEvent.who = response.who;
            result.add(newSuperblockEvent);
        }

        return result;
    }

    public List<SuperblockEvent> getApprovedSuperblocks(long startBlock, long endBlock)
            throws IOException {
        List<SuperblockEvent> result = new ArrayList<>();
        List<DogeSuperblocks.ApprovedSuperblockEventResponse> approvedSuperblockEvents =
                superblocks.getApprovedSuperblockEvents(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeSuperblocks.ApprovedSuperblockEventResponse response : approvedSuperblockEvents) {
            SuperblockEvent approvedSuperblockEvent = new SuperblockEvent();
            approvedSuperblockEvent.superblockId = Keccak256Hash.wrap(response.superblockId);
            approvedSuperblockEvent.who = response.who;
            result.add(approvedSuperblockEvent);
        }

        return result;
    }

    //
//    public List<SuperblockEvent> getChallengedSuperblocks(long startBlock, long endBlock)
//            throws IOException {
//        List<SuperblockEvent> result = new ArrayList<>();
//        List<DogeSuperblocks.ChallengeSuperblockEventResponse> challengeSuperblockEvents =
//                superblocks.getChallengeSuperblockEvents(
//                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
//                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));
//
//        for (DogeSuperblocks.ChallengeSuperblockEventResponse response : challengeSuperblockEvents) {
//            SuperblockEvent challengeSuperblockEvent = new SuperblockEvent();
//            challengeSuperblockEvent.superblockId = Keccak256Hash.wrap(response.superblockId);
//            challengeSuperblockEvent.who = response.who;
//            result.add(challengeSuperblockEvent);
//        }
//
//        return result;
//    }
//
    public List<SuperblockEvent> getSemiApprovedSuperblocks(long startBlock, long endBlock)
            throws IOException {
        List<SuperblockEvent> result = new ArrayList<>();
        List<DogeSuperblocks.SemiApprovedSuperblockEventResponse> semiApprovedSuperblockEvents =
                superblocks.getSemiApprovedSuperblockEvents(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeSuperblocks.SemiApprovedSuperblockEventResponse response : semiApprovedSuperblockEvents) {
            SuperblockEvent semiApprovedSuperblockEvent = new SuperblockEvent();
            semiApprovedSuperblockEvent.superblockId = Keccak256Hash.wrap(response.superblockId);
            semiApprovedSuperblockEvent.who = response.who;
            result.add(semiApprovedSuperblockEvent);
        }

        return result;
    }

    public List<SuperblockEvent> getInvalidSuperblocks(long startBlock, long endBlock)
            throws IOException {
        List<SuperblockEvent> result = new ArrayList<>();
        List<DogeSuperblocks.InvalidSuperblockEventResponse> invalidSuperblockEvents =
                superblocks.getInvalidSuperblockEvents(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeSuperblocks.InvalidSuperblockEventResponse response : invalidSuperblockEvents) {
            SuperblockEvent invalidSuperblockEvent = new SuperblockEvent();
            invalidSuperblockEvent.superblockId = Keccak256Hash.wrap(response.superblockId);
            invalidSuperblockEvent.who = response.who;
            result.add(invalidSuperblockEvent);
        }

        return result;
    }

    public static class SuperblockEvent {

        public Keccak256Hash superblockId;
        public String who;
    }


    /* ---- LOG PROCESSING METHODS ---- */

    public BigInteger getEthTimestampRaw(Log eventLog) throws InterruptedException, ExecutionException {
        String ethBlockHash = eventLog.getBlockHash();
        CompletableFuture<EthBlock> ethBlockCompletableFuture =
                web3.ethGetBlockByHash(ethBlockHash, true).sendAsync();
        checkNotNull(ethBlockCompletableFuture, "Error retrieving completable future");
        EthBlock ethBlock = ethBlockCompletableFuture.get();
        return ethBlock.getBlock().getTimestamp();
    }

    public Date getEthTimestampDate(Log eventLog) throws InterruptedException, ExecutionException {
        BigInteger rawTimestamp = getEthTimestampRaw(eventLog);
        return new Date(rawTimestamp.longValue() * 1000);
    }


    /* ---- GETTERS ---- */

    public BigInteger getSuperblockDuration() throws Exception {
        return claimManager.superblockDuration().send();
    }

    public BigInteger getSuperblockDelay() throws Exception {
        return claimManager.superblockDelay().send();
    }

    public BigInteger getSuperblockTimeout() throws Exception {
        return claimManager.superblockTimeout().send();
    }

    public int getMinDeposit() throws Exception {
        return claimManager.minDeposit().send().intValue();
    }

    public Keccak256Hash getBestSuperblockId() throws Exception {
        return Keccak256Hash.wrap(superblocks.getBestSuperblock().send());
    }

    public BigInteger getNewEventTimestampBigInteger(Keccak256Hash superblockId) throws Exception {
        return claimManager.getNewSuperblockEventTimestamp(superblockId.getBytes()).send();
    }

    public Date getNewEventTimestampDate(Keccak256Hash superblockId) throws Exception {
        return new Date(getNewEventTimestampBigInteger(superblockId).longValue() * 1000);
    }


    /* ---------------------------------- */
    /* ---- DogeClaimManager section ---- */
    /* ---------------------------------- */


    /* ---- CONFIRMING ---- */

    public void checkClaimFinished(Keccak256Hash superblockId) {
        CompletableFuture<TransactionReceipt> futureReceipt =
                claimManager.checkClaimFinished(superblockId.getBytes()).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("checkClaimFinished receipt {}", receipt.toString())
        );
    }

    /**
     * Confirm a semi-approved superblock.
     *
     * @param superblockId Superblock to be confirmed
     * @param descendantId Its first descendant
     */
    public void confirmClaim(Keccak256Hash superblockId, Keccak256Hash descendantId) {
        CompletableFuture<TransactionReceipt> futureReceipt =
                claimManager.confirmClaim(superblockId.getBytes(), descendantId.getBytes()).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("confirmClaim receipt {}", receipt.toString())
        );
    }


    /* ---- BATTLE EVENT RETRIEVAL METHODS AND CLASSES ---- */

    public List<NewBattleEvent> getNewBattleEvents(long startBlock, long endBlock) throws IOException {
        List<NewBattleEvent> result = new ArrayList<>();
        List<DogeClaimManager.NewBattleEventResponse> newBattleEvents =
                claimManager.getNewBattleEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeClaimManager.NewBattleEventResponse response : newBattleEvents) {
            NewBattleEvent newBattleEvent = new NewBattleEvent();
            newBattleEvent.superblockId = Keccak256Hash.wrap(response.superblockId);
            newBattleEvent.sessionId = Keccak256Hash.wrap(response.sessionId);
            newBattleEvent.submitter = response.submitter;
            newBattleEvent.challenger = response.challenger;
            result.add(newBattleEvent);
        }

        return result;
    }

    public List<ChallengerConvictedEvent> getChallengerConvictedEvents(long startBlock, long endBlock)
            throws IOException {
        List<ChallengerConvictedEvent> result = new ArrayList<>();
        List<DogeClaimManager.ChallengerConvictedEventResponse> challengerConvictedEvents =
                claimManager.getChallengerConvictedEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeClaimManager.ChallengerConvictedEventResponse response : challengerConvictedEvents) {
            ChallengerConvictedEvent challengerConvictedEvent = new ChallengerConvictedEvent();
            challengerConvictedEvent.superblockId = Keccak256Hash.wrap(response.superblockId);
            challengerConvictedEvent.sessionId = Keccak256Hash.wrap(response.sessionId);
            challengerConvictedEvent.challenger = response.challenger;
            result.add(challengerConvictedEvent);
        }

        return result;
    }

    public List<SubmitterConvictedEvent> getSubmitterConvictedEvents(long startBlock, long endBlock)
            throws IOException {
        List<SubmitterConvictedEvent> result = new ArrayList<>();
        List<DogeClaimManager.SubmitterConvictedEventResponse> submitterConvictedEvents =
                claimManager.getSubmitterConvictedEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeClaimManager.SubmitterConvictedEventResponse response : submitterConvictedEvents) {
            SubmitterConvictedEvent submitterConvictedEvent = new SubmitterConvictedEvent();
            submitterConvictedEvent.superblockId = Keccak256Hash.wrap(response.superblockId);
            submitterConvictedEvent.sessionId = Keccak256Hash.wrap(response.sessionId);
            submitterConvictedEvent.submitter = response.submitter;
            result.add(submitterConvictedEvent);
        }

        return result;
    }

    public static class NewBattleEvent {
        public Keccak256Hash superblockId;
        public Keccak256Hash sessionId;
        public String submitter;
        public String challenger;
    }

    public static class ChallengerConvictedEvent {
        public Keccak256Hash superblockId;
        public Keccak256Hash sessionId;
        public String challenger;
    }

    public static class SubmitterConvictedEvent {
        public Keccak256Hash superblockId;
        public Keccak256Hash sessionId;
        public String submitter;
    }

    public List<QueryBlockHeaderEvent> getBlockHeaderQueries(long startBlock, long endBlock)
            throws IOException {
        List<QueryBlockHeaderEvent> result = new ArrayList<>();
        List<DogeClaimManager.QueryBlockHeaderEventResponse> queryBlockHeaderEvents =
                claimManager.getQueryBlockHeaderEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeClaimManager.QueryBlockHeaderEventResponse response : queryBlockHeaderEvents) {
            QueryBlockHeaderEvent queryBlockHeaderEvent = new QueryBlockHeaderEvent();
            queryBlockHeaderEvent.sessionId = Keccak256Hash.wrap(response.sessionId);
            queryBlockHeaderEvent.submitter = response.submitter;
            queryBlockHeaderEvent.dogeBlockHash = Sha256Hash.wrap(response.blockHash);
            result.add(queryBlockHeaderEvent);
        }

        return result;
    }

    public List<QueryMerkleRootHashesEvent> getMerkleRootHashesQueries(long startBlock, long endBlock)
            throws IOException {
        List<QueryMerkleRootHashesEvent> result = new ArrayList<>();
        List<DogeClaimManager.QueryMerkleRootHashesEventResponse> queryMerkleRootHashesEvents =
                claimManager.getQueryMerkleRootHashesEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeClaimManager.QueryMerkleRootHashesEventResponse response : queryMerkleRootHashesEvents) {
            QueryMerkleRootHashesEvent queryMerkleRootHashesEvent = new QueryMerkleRootHashesEvent();
            queryMerkleRootHashesEvent.superblockId = Keccak256Hash.wrap(response.superblockId);
            queryMerkleRootHashesEvent.sessionId = Keccak256Hash.wrap(response.sessionId);
            queryMerkleRootHashesEvent.submitter = response.submitter;
            result.add(queryMerkleRootHashesEvent);
        }

        return result;
    }

    public static class QueryBlockHeaderEvent {
        public Keccak256Hash superblockId;
        public Keccak256Hash sessionId;
        public String submitter;
        public Sha256Hash dogeBlockHash;
    }

    public static class QueryMerkleRootHashesEvent {
        public Keccak256Hash superblockId;
        public Keccak256Hash sessionId;
        public String submitter;
    }

    public List<RespondMerkleRootHashesEvent> getRespondMerkleRootHashesEvents(long startBlock, long endBlock)
            throws IOException {
        List<RespondMerkleRootHashesEvent> result = new ArrayList<>();
        List<DogeClaimManager.RespondMerkleRootHashesEventResponse> respondMerkleRootHashesEvents =
                claimManager.getRespondMerkleRootHashesEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeClaimManager.RespondMerkleRootHashesEventResponse response : respondMerkleRootHashesEvents) {
            RespondMerkleRootHashesEvent respondMerkleRootHashesEvent = new RespondMerkleRootHashesEvent();
            respondMerkleRootHashesEvent.superblockId = Keccak256Hash.wrap(response.superblockId);
            respondMerkleRootHashesEvent.sessionId = Keccak256Hash.wrap(response.sessionId);
            respondMerkleRootHashesEvent.challenger = response.challenger;
            respondMerkleRootHashesEvent.blockHashes = new ArrayList<>();
            for (byte[] rawDogeBlockHash : response.blockHashes) {
                respondMerkleRootHashesEvent.blockHashes.add(Sha256Hash.wrap(rawDogeBlockHash));
            }
            result.add(respondMerkleRootHashesEvent);
        }

        return result;
    }

    public List<RespondBlockHeaderEvent> getRespondBlockHeaderEvents(long startBlock, long endBlock)
            throws IOException {
        List<RespondBlockHeaderEvent> result = new ArrayList<>();
        List<DogeClaimManager.RespondBlockHeaderEventResponse> respondBlockHeaderEvents =
                claimManager.getRespondBlockHeaderEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeClaimManager.RespondBlockHeaderEventResponse response : respondBlockHeaderEvents) {
            RespondBlockHeaderEvent respondBlockHeaderEvent = new RespondBlockHeaderEvent();
            respondBlockHeaderEvent.superblockId = Keccak256Hash.wrap(response.superblockId);
            respondBlockHeaderEvent.sessionId = Keccak256Hash.wrap(response.sessionId);
            respondBlockHeaderEvent.challenger = response.challenger;
            respondBlockHeaderEvent.blockScryptHash = response.blockScryptHash;
            respondBlockHeaderEvent.blockHeader = response.blockHeader;
            result.add(respondBlockHeaderEvent);
        }

        return result;
    }

    public static class RespondMerkleRootHashesEvent {
        public Keccak256Hash superblockId;
        public Keccak256Hash sessionId;
        public String challenger;
        public List<Sha256Hash> blockHashes;
    }

    public static class RespondBlockHeaderEvent {
        public Keccak256Hash superblockId;
        public Keccak256Hash sessionId;
        public String challenger;
        public byte[] blockScryptHash;
        public byte[] blockHeader; // TODO: change to AltcoinBlock
    }

    public List<SuperblockBattleDecidedEvent> getSuperblockBattleDecidedEvents(long startBlock, long endBlock)
            throws IOException {
        List<SuperblockBattleDecidedEvent> result = new ArrayList<>();
        List<DogeClaimManager.SuperblockBattleDecidedEventResponse> superblockBattleDecidedEvents =
                claimManager.getSuperblockBattleDecidedEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeClaimManager.SuperblockBattleDecidedEventResponse response : superblockBattleDecidedEvents) {
            SuperblockBattleDecidedEvent superblockBattleDecidedEvent = new SuperblockBattleDecidedEvent();
            superblockBattleDecidedEvent.sessionId = Keccak256Hash.wrap(response.sessionId);
            superblockBattleDecidedEvent.winner = response.winner;
            superblockBattleDecidedEvent.loser = response.loser;
            result.add(superblockBattleDecidedEvent);
        }

        return result;
    }

    public static class SuperblockBattleDecidedEvent {
        public Keccak256Hash sessionId;
        public String winner;
        public String loser;
    }


    /* ---------------------------------- */
    /* --------- Battle section --------- */
    /* ---------------------------------- */

    public void respondBlockHeader(Keccak256Hash superblockId, Keccak256Hash sessionId, AltcoinBlock dogeBlock) {
        byte[] scryptHashBytes = dogeBlock.getScryptHash().getBytes(); // TODO: check if this should be reversed
        byte[] blockHeaderBytes = dogeBlock.bitcoinSerialize();
        CompletableFuture<TransactionReceipt> futureReceipt = claimManager.respondBlockHeader(
                superblockId.getBytes(), sessionId.getBytes(), scryptHashBytes, blockHeaderBytes).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("Responded to block header query for session {}, Doge block {}",
                        sessionId, dogeBlock.getHash())
        );
    }

    public void respondMerkleRootHashes(Keccak256Hash superblockId, Keccak256Hash sessionId,
                                        List<Sha256Hash> dogeBlockHashes) {
        // TODO: double check if endianness is OK
        List<byte[]> rawHashes = new ArrayList<>();
        for (Sha256Hash dogeBlockHash : dogeBlockHashes)
            rawHashes.add(dogeBlockHash.getBytes());
        CompletableFuture<TransactionReceipt> futureReceipt =
                claimManager.respondMerkleRootHashes(superblockId.getBytes(), sessionId.getBytes(), rawHashes).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("Responded to Merkle root hashes query for session {}", sessionId));
    }

    public void queryBlockHeader(Keccak256Hash superblockId, Keccak256Hash sessionId, Sha256Hash dogeBlockHash) {
        CompletableFuture<TransactionReceipt> futureReceipt = claimManager.queryBlockHeader(superblockId.getBytes(),
                sessionId.getBytes(), dogeBlockHash.getBytes()).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("Requested Doge block header for block {}", dogeBlockHash));
    }

    public void verifySuperblock(Keccak256Hash sessionId) throws Exception {
        CompletableFuture<TransactionReceipt> futureReceipt =
                claimManager.verifySuperblock(sessionId.getBytes()).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("Verified superblock for session {}", sessionId));
    }

    public void timeout(Keccak256Hash sessionId) throws Exception {
        CompletableFuture<TransactionReceipt> futureReceipt = claimManager.timeout(sessionId.getBytes()).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("Called timeout for session {}", sessionId));
    }


    /* ---- CHALLENGER ---- */

    public void challengeSuperblock(Keccak256Hash superblockId)
            throws InterruptedException {
        makeChallengerDeposit(BigInteger.valueOf(1000));

        CompletableFuture<TransactionReceipt> futureReceipt =
                claimManagerForChallenges.challengeSuperblock(superblockId.getBytes()).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("challengeSuperblock receipt {}", receipt.toString()));
    }

    private void makeChallengerDeposit(BigInteger weiValue)
            throws InterruptedException {
        CompletableFuture<TransactionReceipt> futureReceipt = claimManagerForChallenges.makeDeposit(weiValue).sendAsync();
        log.info("Challenger deposited {} wei.", weiValue);

        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("makeChallengerDeposit receipt {}", receipt.toString())
        );
        Thread.sleep(200); // in case the transaction takes some time to complete
    }

    public void queryMerkleRootHashes(Keccak256Hash superblockId, Keccak256Hash sessionId)
            throws InterruptedException {
        log.info("Querying Merkle root hashes for superblock {}", superblockId);
        CompletableFuture<TransactionReceipt> futureReceipt = claimManagerForChallenges.queryMerkleRootHashes(
                superblockId.getBytes(), sessionId.getBytes()).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("queryMerkleRootHashes receipt {}", receipt.toString()));
    }


    /* ---- GETTERS ---- */

    public boolean getClaimExists(Keccak256Hash superblockId) throws Exception {
        return claimManager.getClaimExists(superblockId.getBytes()).send();
    }

    public boolean getClaimDecided(Keccak256Hash superblockId) throws Exception {
        return claimManager.getClaimDecided(superblockId.getBytes()).send();
    }

    public boolean getClaimInvalid(Keccak256Hash superblockId) throws Exception {
        return claimManager.getClaimInvalid(superblockId.getBytes()).send();
    }

    public boolean getClaimVerificationOngoing(Keccak256Hash superblockId) throws Exception {
        return claimManager.getClaimVerificationOngoing(superblockId.getBytes()).send();
    }

    public BigInteger getClaimChallengeTimeoutBigInteger(Keccak256Hash superblockId) throws Exception {
        return claimManager.getClaimChallengeTimeout(superblockId.getBytes()).send();
    }

    public Date getClaimChallengeTimeoutDate(Keccak256Hash superblockId) throws Exception {
        return new Date(getClaimChallengeTimeoutBigInteger(superblockId).longValue() * 1000);
    }

    public int getClaimRemainingChallengers(Keccak256Hash superblockId) throws Exception {
        return claimManager.getClaimRemainingChallengers(superblockId.getBytes()).send().intValue();
    }

    public boolean getInBattleAndSemiApprovable(Keccak256Hash superblockId) throws Exception {
        return claimManager.getInBattleAndSemiApprovable(superblockId.getBytes()).send();
    }

    public List<String> getClaimChallengers(Keccak256Hash superblockId) throws Exception {
        return claimManager.getClaimChallengers(superblockId.getBytes()).send();
    }

    public boolean getChallengerHitTimeout(Keccak256Hash sessionId) throws Exception {
        return claimManager.getChallengerHitTimeout(sessionId.getBytes()).send();
    }

    public boolean getSubmitterHitTimeout(Keccak256Hash sessionId) throws Exception {
        return claimManager.getSubmitterHitTimeout(sessionId.getBytes()).send();
    }

    public List<Sha256Hash> getDogeBlockHashes(Keccak256Hash sessionId) throws Exception {
        List<Sha256Hash> result = new ArrayList<>();
        List<byte[]> rawHashes = claimManager.getDogeBlockHashes(sessionId.getBytes()).send();
        for (byte[] rawHash : rawHashes)
            result.add(Sha256Hash.wrap(rawHash)); // TODO: check endianness
        return result;
    }


    /* ---------------------------------- */
    /* ----- Relay Doge tx section ------ */
    /* ---------------------------------- */

    public boolean wasDogeTxProcessed(Sha256Hash txHash) throws Exception {
        return dogeToken.wasDogeTxProcessed(txHash.toBigInteger()).send();
    }

    public void sendRelayTx(org.bitcoinj.core.Transaction tx, byte[] operatorPublicKeyHash, AltcoinBlock block,
                            Superblock superblock, PartialMerkleTree txPMT, PartialMerkleTree superblockPMT)
            throws Exception {
        byte[] dogeBlockHeader = Arrays.copyOfRange(block.bitcoinSerialize(), 0, 80);
        Sha256Hash dogeBlockHash = block.getHash();
        log.info("About to send to the bridge doge tx hash {}. Block hash {}", tx.getHash(), dogeBlockHash);

        byte[] txSerialized = tx.bitcoinSerialize();

        // Construct SPV proof for transaction
        BigInteger txIndex = BigInteger.valueOf(txPMT.getTransactionIndex(tx.getHash()));
        List<Sha256Hash> txSiblingsSha256Hash = txPMT.getTransactionPath(tx.getHash());
        List<BigInteger> txSiblingsBigInteger = new ArrayList<>();
        for (Sha256Hash sha256Hash : txSiblingsSha256Hash) {
            txSiblingsBigInteger.add(sha256Hash.toBigInteger());
        }
        BigInteger dogeBlockHashBigInteger = dogeBlockHash.toBigInteger();

        // Construct SPV proof for block
        BigInteger dogeBlockIndex = BigInteger.valueOf(superblockPMT.getTransactionIndex(dogeBlockHash));
        List<Sha256Hash> dogeBlockSiblingsSha256Hash = superblockPMT.getTransactionPath(dogeBlockHash);
        List<BigInteger> dogeBlockSiblingsBigInteger = new ArrayList<>();
        for (Sha256Hash sha256Hash : dogeBlockSiblingsSha256Hash)
            dogeBlockSiblingsBigInteger.add(sha256Hash.toBigInteger());

        BigInteger superblockMerkleRootBigInteger = superblock.getMerkleRoot().toBigInteger();

        String targetContract = dogeToken.getContractAddress();

        CompletableFuture<TransactionReceipt> futureReceipt = superblocksForRelayTxs.relayTx(txSerialized,
                operatorPublicKeyHash, txIndex, txSiblingsBigInteger, dogeBlockHeader, dogeBlockIndex,
                dogeBlockSiblingsBigInteger, superblock.getSuperblockId().getBytes(), targetContract).sendAsync();
        log.info("Sent relayTx {}", tx.getHash());
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("RelayTx receipt {}.", receipt.toString())
        );
    }


    /* ---------------------------------- */
    /* --------- Unlock section --------- */
    /* ---------------------------------- */

    public void updatePrice(long price) {
        BigInteger priceBI = BigInteger.valueOf(price);
        CompletableFuture<TransactionReceipt> futureReceipt = dogeToken.setDogeEthPrice(priceBI).sendAsync();
        log.info("Sent update doge-eth price tx. Price: {}", price);
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("Update doge-eth price tx receipt {}.", receipt.toString())
        );
    }

    public List<UnlockRequestEvent> getNewUnlockRequests(long startBlock, long endBlock)
            throws ExecutionException, InterruptedException, IOException {
        List<UnlockRequestEvent> result = new ArrayList<>();
        List<DogeToken.UnlockRequestEventResponse> unlockRequestEvents = dogeToken.getUnlockRequestEvents(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));
        for (DogeToken.UnlockRequestEventResponse unlockRequestEvent : unlockRequestEvents) {
            UnlockRequestEvent unlockRequestEventEthWrapper = new UnlockRequestEvent();
            unlockRequestEventEthWrapper.id = unlockRequestEvent.id.longValue();
            unlockRequestEventEthWrapper.operatorPublicKeyHash = unlockRequestEvent.operatorPublicKeyHash;
            result.add(unlockRequestEventEthWrapper);
        }
        return result;
    }

    public static class UnlockRequestEvent {
        public long id;
        public byte[] operatorPublicKeyHash;
    }

    public Unlock getUnlock(Long unlockRequestId) throws Exception {
        Tuple7<String, String, BigInteger, BigInteger, List<BigInteger>, BigInteger, byte[]> tuple =
                dogeToken.getUnlockPendingInvestorProof(BigInteger.valueOf(unlockRequestId)).send();
        Unlock unlock = new Unlock();
        unlock.from = tuple.getValue1();
        unlock.dogeAddress = tuple.getValue2();
        unlock.value = tuple.getValue3().longValue();
        unlock.timestamp = tuple.getValue4().longValue();
        unlock.fee = tuple.getValue6().longValue();
        unlock.operatorPublicKeyHash = tuple.getValue7();

        List<BigInteger> selectedUtxosIndexes = tuple.getValue5();
        List<UTXO> selectedUtxosOutpoints = new ArrayList<>();
        for (BigInteger selectedUtxo : selectedUtxosIndexes) {
            Tuple3<BigInteger, BigInteger, BigInteger> utxo = dogeToken.getUtxo(unlock.operatorPublicKeyHash, selectedUtxo).send();
            long value = utxo.getValue1().longValue();
            Sha256Hash txHash = Sha256Hash.wrap(bigIntegerToHexStringPad64(utxo.getValue2()));
            long outputIndex = utxo.getValue3().longValue();
            selectedUtxosOutpoints.add(new UTXO(txHash, outputIndex, Coin.valueOf(value), 0, false, null));
        }
        unlock.selectedUtxos = selectedUtxosOutpoints;
        return unlock;
    }

    public static class Unlock {
        public String from;
        public String dogeAddress;
        public long value;
        public long timestamp;
        public List<UTXO> selectedUtxos;
        public long fee;
        public byte[] operatorPublicKeyHash;
    }

}