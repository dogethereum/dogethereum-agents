package org.dogethereum.agents.core.eth;


import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;

import org.dogethereum.agents.constants.SystemProperties;
import org.dogethereum.agents.contract.*;

import org.dogethereum.agents.core.dogecoin.*;
import org.dogethereum.agents.core.dogecoin.SuperblockUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.libdohj.core.ScryptHash;

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
    private DogeSuperblocksExtended superblocks;

    private SystemProperties config;
    private BigInteger gasPriceMinimum;

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
        String fromAddressGeneralPurposeAndSendBlocks;
        String fromAddressRelayTxs;
        String fromAddressPriceOracle;

        if (config.isGanache()) {
            dogeTokenContractAddress = getContractAddress("DogeToken");
            claimManagerContractAddress = getContractAddress("DogeClaimManager");
            superblocksContractAddress = getContractAddress("DogeSuperblocks");
            List<String> accounts = web3.ethAccounts().send().getAccounts();
            fromAddressGeneralPurposeAndSendBlocks = accounts.get(0);
            fromAddressRelayTxs = accounts.get(1);
            fromAddressPriceOracle = accounts.get(2);
        } else {
            dogeTokenContractAddress = config.dogeTokenContractAddress();
            claimManagerContractAddress = config.dogeClaimManagerContractAddress();
            superblocksContractAddress = config.dogeSuperblocksContractAddress();
            fromAddressGeneralPurposeAndSendBlocks = config.addressGeneralPurposeAndSendBlocks();
            fromAddressRelayTxs = config.addressRelayTxs();
            fromAddressPriceOracle = config.addressPriceOracle();
        }

        gasPriceMinimum = BigInteger.valueOf(config.gasPriceMinimum());
        BigInteger gasLimit = BigInteger.valueOf(config.gasLimit());

        dogeToken = DogeTokenExtended.load(dogeTokenContractAddress, web3,
                                           new ClientTransactionManager(web3, fromAddressPriceOracle),
                                           gasPriceMinimum, gasLimit);
        assert dogeToken.isValid();
        claimManager = DogeClaimManagerExtended.load(claimManagerContractAddress, web3,
                                                     new ClientTransactionManager(web3, fromAddressGeneralPurposeAndSendBlocks),
                                                     gasPriceMinimum, gasLimit);
        assert claimManager.isValid();
        superblocks = DogeSuperblocksExtended.load(superblocksContractAddress, web3,
                                                   new ClientTransactionManager(web3, fromAddressGeneralPurposeAndSendBlocks),
                                                   gasPriceMinimum, gasLimit);
        assert superblocks.isValid();
    }

    /**
     * Get the deployed contract address from a truffle json file
     * @param contractName
     * @return The contract address
     * @throws Exception
     */
    private String getContractAddress(String contractName) throws Exception {
        String basePath = config.truffleBuildContractsDirectory();
        FileReader dogeRelaySpecFile = new FileReader(basePath + "/" + contractName + ".json");
        JSONParser parser = new JSONParser();
        JSONObject parsedSpecFile =  (JSONObject) parser.parse(dogeRelaySpecFile);
        JSONObject networks =  (JSONObject) parsedSpecFile.get("networks");
        JSONObject data =  (JSONObject) networks.values().iterator().next();
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
        superblocks.setGasPrice(gasPrice);
    }

    /**
     * E.g. input: 255
     * Output: "00000000000000000000000000000000000000000000000000000000000000FF"
     */
    private String bigIntegerToHexStringPad64(BigInteger input) {
        String input2 = input.toString(16);
        StringBuilder output = new StringBuilder(input2);
        while (output.length()<64) {
            output.insert(0,"0");
        }
        return output.toString();
    }


    /* ---------------------------------- */
    /* - Relay Doge superblocks section - */
    /* ---------------------------------- */

    /**
     * Propose a series of superblocks to DogeClaimManager in order to keep Dogethereum Contracts updated.
     * @param superblocksToSend DogeSuperblocks that are already stored in the local database,
     *                          but still haven't been submitted to Dogethereum Contracts.
     * @throws Exception If a superblock hash cannot be calculated.
     */
    public void sendStoreSuperblocks(Deque<Superblock> superblocksToSend) throws Exception {
        log.info("About to send to the bridge superblocks from {} to {}",
                superblocksToSend.peekFirst().getSuperblockId(),
                superblocksToSend.peekLast().getSuperblockId());

        for (Superblock superblock : superblocksToSend) {
            CompletableFuture<TransactionReceipt> futureReceipt = proposeSuperblock(superblock);
            log.info("Sent superblock {}", superblock.getSuperblockId());
            futureReceipt.thenAcceptAsync( (TransactionReceipt receipt) ->
                log.info("proposeSuperblock receipt {}", receipt.toString())
            );
        }
        // This is because sendStoreBlocks does it; TODO: look into it later
        Thread.sleep(200);
    }

    /**
     * Propose a superblock to DogeClaimManager in order to keep Dogethereum Contracts updated.
     * @param superblock Oldest superblock that is already stored in the local database,
     *                   but still hasn't been submitted to Dogethereum Contracts.
     * @throws Exception If superblock hash cannot be calculated.
     */
    public void sendStoreSuperblock(Superblock superblock) throws Exception {
        log.info("About to send superblock {} to the bridge.", Sha256Hash.wrap(superblock.getSuperblockId()));

        // Check if the parent has been approved before sending this superblock.
        byte[] parentId = superblock.getParentId();
        if (!(isSuperblockApproved(parentId) || isSuperblockSemiApproved(parentId))) {
            log.info("Superblock {} not sent because its parent was neither approved nor semi approved.",
                    Sha256Hash.wrap(superblock.getSuperblockId()));
            return;
        }

        if (getClaimExists(superblock.getSuperblockId())) {
            log.info("Superblock {} has already been sent. Returning.",
                    Sha256Hash.wrap(superblock.getSuperblockId()));
            return;
        }

//        BigInteger bondedDeposit = getBondedDeposit(superblock.getSuperblockId());

        // TODO: see how much wei we should actually send and whether it's a paremeter for this method
        // Idea: make it a configuration variable
        CompletableFuture<TransactionReceipt> depositsReceipt = makeClaimDeposit(BigInteger.valueOf(1000));

        // The parent is either approved or semi approved. We can send the superblock.
        CompletableFuture<TransactionReceipt> futureReceipt = proposeSuperblock(superblock);
        log.info("Sent superblock {}", Sha256Hash.wrap(superblock.getSuperblockId()));
        futureReceipt.thenAcceptAsync( (TransactionReceipt receipt) ->
            log.info("proposeSuperblock receipt {}", receipt.toString())
        );
        Thread.sleep(200);
    }

    /**
     * Propose a superblock to DogeClaimManager.
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
                superblock.getParentId()
        ).sendAsync();
    }

    /**
     * Get 9 ancestors of the relay's top superblock:
     * ancestor -1 (parent), ancestor -5, ancestor -25, ancestor -125, ...
     * @return List of 9 ancestors where result[i] = ancestor -(5**i).
     * @throws Exception
     */
    public List<byte[]> getSuperblockLocator() throws Exception {
        return superblocks.getSuperblockLocator().send();
    }

    private CompletableFuture<TransactionReceipt> makeClaimDeposit(BigInteger weiValue) throws InterruptedException {
        CompletableFuture<TransactionReceipt> futureReceipt = claimManager.makeDeposit(weiValue).sendAsync();
        log.info("Deposited {} wei.", weiValue);

        futureReceipt.thenAcceptAsync( (TransactionReceipt receipt) ->
            log.info("makeClaimDeposit receipt {}", receipt.toString())
        );
        Thread.sleep(200); // in case the transaction takes some time to complete

        return futureReceipt;
    }

    private BigInteger getBondedDeposit(byte[] claimId) throws Exception {
        String fromAddressGeneralPurposeAndSendBlocks;

        if (config.isGanache()) {
            List<String> accounts = web3.ethAccounts().send().getAccounts();
            fromAddressGeneralPurposeAndSendBlocks = accounts.get(0);
        } else {
            fromAddressGeneralPurposeAndSendBlocks = config.addressGeneralPurposeAndSendBlocks();
        }

        return claimManager.getBondedDeposit(claimId, fromAddressGeneralPurposeAndSendBlocks).send();
    }

    /**
     * Return the size of the header as a 4-byte byte[]
     */
    private byte[] calculateHeaderSize (byte[] header) {
        String size = BigInteger.valueOf(header.length).toString(16);
        while (size.length() < 8) {
            size = "0" + size;
        }
        return Hex.decode(size);
    }


    /* ---- SUPERBLOCK STATUS CHECKS ---- */

    public BigInteger getSuperblockStatus(byte[] superblockId) throws Exception {
        return superblocks.getSuperblockStatus(superblockId).send();
    }

    public boolean isSuperblockApproved(byte[] superblockId) throws Exception {
        return getSuperblockStatus(superblockId).equals(SuperblockUtils.STATUS_APPROVED);
    }

    public boolean isSuperblockSemiApproved(byte[] superblockId) throws Exception {
        return getSuperblockStatus(superblockId).equals(SuperblockUtils.STATUS_SEMI_APPROVED);
    }

    public boolean isSuperblockNew(byte[] superblockId) throws Exception {
        return getSuperblockStatus(superblockId).equals(SuperblockUtils.STATUS_NEW);
    }

    public boolean isSuperblockInBattle(byte[] superblockId) throws Exception {
        return getSuperblockStatus(superblockId).equals(SuperblockUtils.STATUS_IN_BATTLE);
    }

    public boolean isSuperblockInvalid(byte[] superblockId) throws Exception {
        return getSuperblockStatus(superblockId).equals(SuperblockUtils.STATUS_INVALID);
    }

    public boolean isSuperblockUninitialized(byte[] superblockId) throws Exception {
        return getSuperblockStatus(superblockId).equals(SuperblockUtils.STATUS_UNINITIALIZED);
    }

    public boolean statusAllowsConfirmation(byte[] superblockId) throws Exception {
        return isSuperblockSemiApproved(superblockId) || isSuperblockNew(superblockId);
    }

    // Right now this is just for testing, look into security measures later
    public void checkClaimFinished(byte[] superblockId) {
        CompletableFuture<TransactionReceipt> futureReceipt = claimManager.checkClaimFinished(superblockId).sendAsync();
        futureReceipt.thenAcceptAsync( (TransactionReceipt receipt) ->
                log.info("checkClaimFinished receipt {}", receipt.toString())
        );
    }


    /* ---- EVENT RETRIEVAL METHODS AND CLASSES ---- */

    public List<SuperblockEvent> getNewSuperblocks(long startBlock, long endBlock) throws IOException {
        List<SuperblockEvent> result = new ArrayList<>();
        List<DogeSuperblocks.NewSuperblockEventResponse> newSuperblockEvents =
                superblocks.getNewSuperblockEvents(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeSuperblocks.NewSuperblockEventResponse response : newSuperblockEvents) {
            SuperblockEvent newSuperblockEvent = new SuperblockEvent();
            newSuperblockEvent.superblockId = response.superblockId;
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
            approvedSuperblockEvent.superblockId = response.superblockId;
            approvedSuperblockEvent.who = response.who;
            result.add(approvedSuperblockEvent);
        }

        return result;
    }

    public List<SuperblockEvent> getChallengedSuperblocks(long startBlock, long endBlock)
            throws IOException {
        List<SuperblockEvent> result = new ArrayList<>();
        List<DogeSuperblocks.ChallengeSuperblockEventResponse> challengeSuperblockEvents =
                superblocks.getChallengeSuperblockEvents(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeSuperblocks.ChallengeSuperblockEventResponse response : challengeSuperblockEvents) {
            SuperblockEvent challengeSuperblockEvent = new SuperblockEvent();
            challengeSuperblockEvent.superblockId = response.superblockId;
            challengeSuperblockEvent.who = response.who;
            result.add(challengeSuperblockEvent);
        }

        return result;
    }

    public List<SuperblockEvent> getSemiApprovedSuperblocks(long startBlock, long endBlock)
            throws IOException {
        List<SuperblockEvent> result = new ArrayList<>();
        List<DogeSuperblocks.SemiApprovedSuperblockEventResponse> semiApprovedSuperblockEvents =
                superblocks.getSemiApprovedSuperblockEvents(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeSuperblocks.SemiApprovedSuperblockEventResponse response : semiApprovedSuperblockEvents) {
            SuperblockEvent semiApprovedSuperblockEvent = new SuperblockEvent();
            semiApprovedSuperblockEvent.superblockId = response.superblockId;
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
            invalidSuperblockEvent.superblockId = response.superblockId;
            invalidSuperblockEvent.who = response.who;
            result.add(invalidSuperblockEvent);
        }

        return result;
    }

    public static class SuperblockEvent {

        public byte[] superblockId;
        public String who;
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
            queryBlockHeaderEvent.sessionId = response.sessionId;
            queryBlockHeaderEvent.submitter = response.submitter;
            queryBlockHeaderEvent.dogeBlockHash = response.blockHash;
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
            queryMerkleRootHashesEvent.sessionId = response.sessionId;
            queryMerkleRootHashesEvent.submitter = response.submitter;
            result.add(queryMerkleRootHashesEvent);
        }

        return result;
    }

    public static class QueryBlockHeaderEvent {
        public byte[] sessionId;
        public String submitter;
        public byte[] dogeBlockHash;
    }

    public static class QueryMerkleRootHashesEvent {
        public byte[] sessionId;
        public String submitter;
    }

    public static class QueryEvent {

        public byte[] sessionId;
        public String claimant;
    }


    /* ---- BATTLE METHODS ---- */

    public void respondBlockHeader(byte[] sessionId, AltcoinBlock dogeBlock) {
        byte[] scryptHashBytes = dogeBlock.getScryptHash().getBytes(); // TODO: check if this should be reversed
        byte[] blockHeaderBytes = dogeBlock.bitcoinSerialize();
        CompletableFuture<TransactionReceipt> futureReceipt = claimManager.respondBlockHeader(
                sessionId, scryptHashBytes, blockHeaderBytes).sendAsync();
        futureReceipt.thenAcceptAsync( (TransactionReceipt receipt) ->
                log.info("Responded to block header query for session {}, Doge block {}",
                        Sha256Hash.wrap(sessionId), dogeBlock.getHash())
        );
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

    public byte[] getBestSuperblockId() throws Exception {
        return superblocks.getBestSuperblock().send();
    }

    public BigInteger getNewEventTimestampBigInteger(byte[] superblockId) throws Exception {
        return claimManager.getNewSuperblockEventTimestamp(superblockId).send();
    }

    public Date getNewEventTimestampDate(byte[] superblockId) throws Exception {
        return new Date(getNewEventTimestampBigInteger(superblockId).longValue());
    }


    /* ---------------------------------- */
    /* ---- DogeClaimManager section ---- */
    /* ---------------------------------- */


    /* ---- GETTERS ---- */

    public boolean getClaimExists(byte[] superblockId) throws Exception {
        return claimManager.getClaimExists(superblockId).send();
    }

    public boolean getClaimDecided(byte[] superblockId) throws Exception {
        return claimManager.getClaimDecided(superblockId).send();
    }

    public boolean getClaimInvalid(byte[] superblockId) throws Exception {
        return claimManager.getClaimInvalid(superblockId).send();
    }

    public boolean getClaimVerificationOngoing(byte[] superblockId) throws Exception {
        return claimManager.getClaimVerificationOngoing(superblockId).send();
    }

    public BigInteger getClaimChallengeTimeoutBigInteger(byte[] superblockId) throws Exception {
        return claimManager.getClaimChallengeTimeout(superblockId).send();
    }

    public Date getClaimChallengeTimeoutDate(byte[] superblockId) throws Exception {
        return new Date(getClaimChallengeTimeoutBigInteger(superblockId).longValue());
    }

    public int getClaimRemainingChallengers(byte[] superblockId) throws Exception {
        return claimManager.getClaimRemainingChallengers(superblockId).send().intValue();
    }


    /* ---- EVENT RETRIEVAL METHODS AND CLASSES ---- */


    /* ---------------------------------- */
    /* ----- Relay Doge tx section ------ */
    /* ---------------------------------- */

    public boolean wasDogeTxProcessed(Sha256Hash txHash) throws Exception {
        return dogeToken.wasDogeTxProcessed(txHash.toBigInteger()).send();
    }

    // TODO: test with operator enabled
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

        CompletableFuture<TransactionReceipt> futureReceipt = superblocks.relayTx(txSerialized,
                operatorPublicKeyHash, txIndex, txSiblingsBigInteger, dogeBlockHeader, dogeBlockIndex,
                dogeBlockSiblingsBigInteger, superblock.getSuperblockId(), targetContract).sendAsync();
        log.info("Sent relayTx {}", tx.getHash());
        futureReceipt.thenAcceptAsync( (TransactionReceipt receipt) ->
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
        futureReceipt.thenAcceptAsync( (TransactionReceipt receipt) ->
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
        unlock.timestamp =  tuple.getValue4().longValue();
        unlock.fee = tuple.getValue6().longValue();
        unlock.operatorPublicKeyHash = tuple.getValue7();

        List<BigInteger> selectedUtxosIndexes = tuple.getValue5();
        List<UTXO> selectedUtxosOutpoints = new ArrayList<>();
        for (BigInteger selectedUtxo : selectedUtxosIndexes) {
            Tuple3<BigInteger, BigInteger, BigInteger> utxo = dogeToken.getUtxo(unlock.operatorPublicKeyHash, selectedUtxo).send();
            long value = utxo.getValue1().longValue();
            Sha256Hash txHash = Sha256Hash.wrap(bigIntegerToHexStringPad64(utxo.getValue2()));
            long outputIndex = utxo.getValue3().longValue();
            selectedUtxosOutpoints.add(new UTXO(txHash, outputIndex, Coin.valueOf(value), 0 ,false, null));
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
