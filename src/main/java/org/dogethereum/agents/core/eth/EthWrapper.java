package org.dogethereum.agents.core.eth;


import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;

import org.dogethereum.agents.constants.SystemProperties;
import org.dogethereum.agents.contract.*;

import org.dogethereum.agents.core.dogecoin.*;
import org.dogethereum.agents.core.dogecoin.SuperblockUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.libdohj.core.ScryptHash;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import org.web3j.tuples.generated.Tuple3;

import org.web3j.tuples.generated.Tuple9;
import org.web3j.tx.ClientTransactionManager;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
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

    // Extensions of contracts generated automatically by web3j
    private DogeTokenExtended dogeToken;
    private SuperblockClaimsExtended superblockClaims;
    private SuperblockClaimsExtended superblockClaimsForChallenges;
    private DogeBattleManagerExtended battleManager;
    private DogeBattleManagerExtended battleManagerForChallenges;
    private DogeSuperblocksExtended superblocks;
    private DogeSuperblocksExtended superblocksForRelayTxs;
    private ScryptClaims scryptVerifier;

    private SystemProperties config;
    private BigInteger gasPriceMinimum;

    private String generalPurposeAndSendSuperblocksAddress;
    private String relayTxsAddress;
    private String dogeSuperblockChallengerAddress;

    private BigInteger minProposalDeposit;
    private BigInteger minChallengeDeposit;
    private BigInteger queryMerkleRootHashesCost;
    private BigInteger queryBlockHeaderCost;
    private BigInteger respondMerkleRootHashesCost;
    private BigInteger respondBlockHeaderCost;
    private BigInteger verifySuperblockCost;


    /* ---------------------------------- */
    /* ------ General code section ------ */
    /* ---------------------------------- */

    @Autowired
    public EthWrapper() throws Exception {
        config = SystemProperties.CONFIG;
        web3 = Web3j.build(new HttpService());  // defaults to http://localhost:8545/
        String dogeTokenContractAddress;
        String superblockClaimsContractAddress;
        String battleManagerContractAddress;
        String superblocksContractAddress;
        String scryptVerifierAddress;

        if (config.isGanache()) {
            ContractAddresses addresses = getContractAddresses();
            dogeTokenContractAddress = addresses.getDogeToken();
            superblockClaimsContractAddress = addresses.getSuperblockClaims();
            battleManagerContractAddress = addresses.getBattleManager();
            superblocksContractAddress = addresses.getSuperblocks();
            scryptVerifierAddress = addresses.getScryptChecker();
            List<String> accounts = web3.ethAccounts().send().getAccounts();
            generalPurposeAndSendSuperblocksAddress = accounts.get(0);
            relayTxsAddress = accounts.get(1);
            dogeSuperblockChallengerAddress = accounts.get(3);
        } else {
            dogeTokenContractAddress = config.dogeTokenContractAddress();
            superblockClaimsContractAddress = config.superblockClaimsContractAddress();
            battleManagerContractAddress = config.dogeBattleManagerContractAddress();
            superblocksContractAddress = config.dogeSuperblocksContractAddress();
            scryptVerifierAddress = config.dogeScryptVerifierContractAddress();
            generalPurposeAndSendSuperblocksAddress = config.generalPurposeAndSendSuperblocksAddress();
            relayTxsAddress = config.relayTxsAddress();
            dogeSuperblockChallengerAddress = config.dogeSuperblockChallengerAddress();
        }

        gasPriceMinimum = BigInteger.valueOf(config.gasPriceMinimum());
        BigInteger gasLimit = BigInteger.valueOf(config.gasLimit());

        dogeToken = DogeTokenExtended.load(dogeTokenContractAddress, web3,
                new ClientTransactionManager(web3, generalPurposeAndSendSuperblocksAddress),
                gasPriceMinimum, gasLimit);
        assert dogeToken.isValid();
        superblockClaims = SuperblockClaimsExtended.load(superblockClaimsContractAddress, web3,
                new ClientTransactionManager(web3, generalPurposeAndSendSuperblocksAddress),
                gasPriceMinimum, gasLimit);
        assert superblockClaims.isValid();
        superblockClaimsForChallenges = SuperblockClaimsExtended.load(superblockClaimsContractAddress, web3,
                new ClientTransactionManager(web3, dogeSuperblockChallengerAddress),
                gasPriceMinimum, gasLimit);
        assert superblockClaimsForChallenges.isValid();
        battleManager = DogeBattleManagerExtended.load(battleManagerContractAddress, web3,
                new ClientTransactionManager(web3, generalPurposeAndSendSuperblocksAddress),
                gasPriceMinimum, gasLimit);
        assert battleManager.isValid();
        battleManagerForChallenges = DogeBattleManagerExtended.load(battleManagerContractAddress, web3,
                new ClientTransactionManager(web3, dogeSuperblockChallengerAddress),
                gasPriceMinimum, gasLimit);
        assert battleManagerForChallenges.isValid();
        superblocks = DogeSuperblocksExtended.load(superblocksContractAddress, web3,
                new ClientTransactionManager(web3, generalPurposeAndSendSuperblocksAddress),
                gasPriceMinimum, gasLimit);
        assert superblocks.isValid();
        superblocksForRelayTxs = DogeSuperblocksExtended.load(superblocksContractAddress, web3,
                new ClientTransactionManager(web3, relayTxsAddress),
                gasPriceMinimum, gasLimit);
        assert superblocksForRelayTxs.isValid();
        scryptVerifier = ScryptClaims.load(scryptVerifierAddress, web3,
                new ClientTransactionManager(web3, generalPurposeAndSendSuperblocksAddress),
                gasPriceMinimum, gasLimit);

        minProposalDeposit = superblockClaims.minProposalDeposit().send();
        minChallengeDeposit = superblockClaims.minChallengeDeposit().send();
        queryMerkleRootHashesCost = superblockClaims.queryMerkleRootHashesCost().send();
        queryBlockHeaderCost = superblockClaims.queryBlockHeaderCost().send();
        respondMerkleRootHashesCost = superblockClaims.respondMerkleRootHashesCost().send();
        respondBlockHeaderCost = superblockClaims.respondBlockHeaderCost().send();
        verifySuperblockCost = superblockClaims.verifySuperblockCost().send();
    }

    /**
     * Returns an object describing the addresses of dogethereum contracts from a deployment JSON file.
     *
     * @return Addresses of contracts.
     * @throws Exception
     */
    private ContractAddresses getContractAddresses() throws Exception {
        String path = config.deploymentFile();
        FileReader deploymentJson = new FileReader(path);
        JSONParser parser = new JSONParser();
        JSONObject deployment = (JSONObject) parser.parse(deploymentJson);
        JSONObject contracts = (JSONObject) deployment.get("contracts");

        String dogeTokenAddress = getContractAddress(contracts, "dogeToken");
        String superblockClaimsAddress = getContractAddress(contracts, "superblockClaims");
        String battleManagerAddress = getContractAddress(contracts, "battleManager");
        String superblocksAddress = getContractAddress(contracts, "superblocks");
        String scryptCheckerAddress = getContractAddress(contracts, "scryptChecker");
        return new ContractAddresses(
            superblocksAddress,
            superblockClaimsAddress,
            scryptCheckerAddress,
            dogeTokenAddress,
            battleManagerAddress
        );
    }

    private String getContractAddress(JSONObject contracts, String componentName) {
        JSONObject contract = (JSONObject) contracts.get(componentName);
        return contract.get("address").toString();
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

    public boolean arePendingTransactionsForSendSuperblocksAddress() throws IOException {
        return arePendingTransactionsFor(generalPurposeAndSendSuperblocksAddress);
    }

    public boolean arePendingTransactionsForRelayTxsAddress() throws IOException {
        return arePendingTransactionsFor(relayTxsAddress);
    }

    public boolean arePendingTransactionsForChallengerAddress() throws IOException {
        return arePendingTransactionsFor(dogeSuperblockChallengerAddress);
    }

    public long getLatestBlockTimestamp() throws IOException {
        EthBlock.Block block = web3.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send().getBlock();
        return block.getTimestamp().longValueExact();
    }

    /**
     * Checks if there are pending transactions for a given contract.
     * @param address
     * @return
     * @throws IOException
     */
    private boolean arePendingTransactionsFor(String address) throws IOException {
        BigInteger latest = web3.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send().getTransactionCount();
        BigInteger pending = web3.ethGetTransactionCount(address, DefaultBlockParameterName.PENDING).send().getTransactionCount();
        return pending.compareTo(latest) > 0;
    }

    // TODO: see if this should also set the price for DogeBattleManager
    /**
     * Sets gas prices for all DogeToken, SuperblockClaims and DogeSuperblocks contract instances.
     * @throws IOException
     */
    public void updateContractFacadesGasPrice() throws IOException {
        BigInteger gasPriceSuggestedByEthNode = web3.ethGasPrice().send().getGasPrice();
        BigInteger gasPrice;
        if (gasPriceSuggestedByEthNode.compareTo(gasPriceMinimum) > 0) {
            gasPrice = gasPriceSuggestedByEthNode;
        } else {
            gasPrice = gasPriceMinimum;
        }

        dogeToken.setGasPrice(gasPrice);
        superblockClaims.setGasPrice(gasPrice);
        superblockClaimsForChallenges.setGasPrice(gasPrice);
        superblocks.setGasPrice(gasPrice);
        superblocksForRelayTxs.setGasPrice(gasPrice);
    }

    public String getGeneralPurposeAndSendSuperblocksAddress() {
        return generalPurposeAndSendSuperblocksAddress;
    }

    public String getDogeSuperblockChallengerAddress() {
        return dogeSuperblockChallengerAddress;
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


    /* ---- CONTRACT GETTERS ---- */

    public SuperblockClaimsExtended getSuperblockClaims() {
        return superblockClaims;
    }

    public SuperblockClaimsExtended getSuperblockClaimsForChallenges() {
        return superblockClaimsForChallenges;
    }

    public DogeBattleManagerExtended getBattleManager() {
        return battleManager;
    }

    public DogeBattleManagerExtended getBattleManagerForChallenges() {
        return battleManagerForChallenges;
    }


    /* ---------------------------------- */
    /* - Relay Doge superblocks section - */
    /* ---------------------------------- */

    /**
     * Proposes a superblock to SuperblockClaims in order to keep the Dogethereum contracts updated.
     * @param superblock Oldest superblock that is already stored in the local database,
     *                   but still hasn't been submitted to Dogethereum Contracts.
     * @throws Exception If superblock hash cannot be calculated.
     */
    public void sendStoreSuperblock(Superblock superblock, String account) throws Exception {
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

        // Make any necessary deposits for sending the superblock
        makeDepositIfNeeded(account, superblockClaims, getSuperblockDeposit(superblock.getDogeBlockHashes().size()));

        // The parent is either approved or semi approved. We can send the superblock.
        CompletableFuture<TransactionReceipt> futureReceipt = proposeSuperblock(superblock);
        Keccak256Hash superblockId = superblock.getSuperblockId();
        log.info("Sent superblock {}", superblockId);
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("proposeSuperblock receipt {}", receipt.toString())
        );
        futureReceipt.exceptionally(t -> {
            log.error("Failed to respond to propose superblock {}.", superblockId);
            log.error(t.getMessage(), t);
            return null;
        });
        Thread.sleep(200);
    }

    /**
     * Proposes a superblock to SuperblockClaims. To be called from sendStoreSuperblock.
     * @param superblock Superblock to be proposed.
     * @return
     */
    private CompletableFuture<TransactionReceipt> proposeSuperblock(Superblock superblock) {
        return superblockClaims.proposeSuperblock(superblock.getMerkleRoot().getBytes(),
                superblock.getChainWork(),
                BigInteger.valueOf(superblock.getLastDogeBlockTime()),
                BigInteger.valueOf(superblock.getPreviousToLastDogeBlockTime()),
                superblock.getLastDogeBlockHash().getBytes(),
                BigInteger.valueOf(superblock.getLastDogeBlockBits()),
                superblock.getParentId().getBytes()
        ).sendAsync();
    }

    /**
     * Get 9 ancestors of the contracts' top superblock:
     * ancestor -1 (parent), ancestor -5, ancestor -25, ancestor -125, ...
     * @return List of 9 ancestors where result[i] = ancestor -(5**i).
     * @throws Exception
     */
    public List<byte[]> getSuperblockLocator() throws Exception {
        return superblocks.getSuperblockLocator().send();
    }

    public boolean wasSuperblockAlreadySubmitted(Keccak256Hash superblockId) throws Exception {
        return !superblocks.getSuperblockIndex(superblockId.getBytes()).send().equals(BigInteger.ZERO);
    }

    /**
     * Makes a deposit.
     * @param weiValue Wei to be deposited.
     * @param mySuperblockClaims this.superblockClaims if proposing/defending, this.superblockClaimsForChallenges if challenging.
     * @throws InterruptedException
     */
    private void makeDeposit(SuperblockClaims mySuperblockClaims, BigInteger weiValue) throws InterruptedException {
        CompletableFuture<TransactionReceipt> futureReceipt = mySuperblockClaims.makeDeposit(weiValue).sendAsync();
        log.info("Deposited {} wei.", weiValue);

        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("makeClaimDeposit receipt {}", receipt.toString())
        );
        futureReceipt.exceptionally(t -> {
            log.error("Failed to deposit {} weis.", weiValue);
            log.error(t.getMessage(), t);
            return null;
        });
        Thread.sleep(200); // in case the transaction takes some time to complete
    }

    /**
     * Returns the initial deposit for proposing a superblock, i.e. enough to cover the challenge,
     * all battle steps and a reward for the opponent in case the battle is lost.
     * This deposit only covers one battle and it's meant to optimise the number of transactions performed
     * by the submitter - it's still necessary to make a deposit for each step if another battle is carried out
     * over the same superblock.
     * @param nHashes Number of hashes in the superblock.
     * @return Initial deposit for covering a reward and a single battle.
     * @throws Exception
     */
    private BigInteger getSuperblockDeposit(int nHashes) throws Exception {
        BigInteger result = minProposalDeposit;
        result = result.add(BigInteger.valueOf(nHashes+1).multiply(queryBlockHeaderCost));
        return result.add(queryMerkleRootHashesCost);
    }

    private BigInteger getBondedDeposit(Keccak256Hash claimId) throws Exception {
        return superblockClaims.getBondedDeposit(claimId.getBytes(), generalPurposeAndSendSuperblocksAddress).send();
    }

    private BigInteger getDeposit(String account, SuperblockClaims mySuperblockClaims) throws Exception {
        return mySuperblockClaims.getDeposit(account).send();
    }

    /**
     * Makes the minimum necessary deposit for reaching a given amount.
     * @param account Caller's address.
     * @param mySuperblockClaims this.superblockClaims if proposing/defending, this.superblockClaimsForChallenges if challenging.
     * @param weiValue Deposit to be reached. This should be the caller's total deposit in the end.
     * @throws Exception
     */
    private void makeDepositIfNeeded(String account, SuperblockClaims mySuperblockClaims, BigInteger weiValue)
            throws Exception {
        BigInteger currentDeposit = getDeposit(account, mySuperblockClaims);
        if (currentDeposit.compareTo(weiValue) < 0) {
            BigInteger diff = weiValue.subtract(currentDeposit);
            makeDeposit(mySuperblockClaims, diff);
        }
    }

    private void withdrawDeposit(SuperblockClaims mySuperblockClaims, BigInteger weiValue) throws Exception {
        CompletableFuture<TransactionReceipt> futureReceipt = mySuperblockClaims.withdrawDeposit(weiValue).sendAsync();
        log.info("Withdrew {} wei.", weiValue);
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("withdrawDeposit receipt {}", receipt.toString())
        );
        futureReceipt.exceptionally(t -> {
            log.error("Failed to withdraw {} weis.", weiValue);
            log.error(t.getMessage(), t);
            return null;
        });
    }

    /**
     * Withdraw deposits so that only the maximum amount of funds (as determined by user configuration)
     * is left in the contract.
     * To be called after battles or when a superblock is approved/invalidated.
     * @param account Caller's address.
     * @param mySuperblockClaims this.superblockClaims if proposing/defending, this.superblockClaimsForChallenges if challenging.
     * @throws Exception
     */
    private void withdrawAllFundsExceptLimit(String account, SuperblockClaims mySuperblockClaims) throws Exception {
        BigInteger currentDeposit = getDeposit(account, mySuperblockClaims);
        BigInteger limit = BigInteger.valueOf(config.depositedFundsLimit());
        if (currentDeposit.compareTo(limit) > 0) {
            withdrawDeposit(mySuperblockClaims, currentDeposit.subtract(limit));
        }
    }

    /**
     * Withdraw deposits so that only the maximum amount of funds (as determined by user configuration)
     * is left in the contract.
     * To be called after battles or when a superblock is approved/invalidated.
     * @param account Caller's address.
     * @param isChallenger true if challenging, false if proposing/defending.
     * @throws Exception
     */
    public void withdrawAllFundsExceptLimit(String account, boolean isChallenger) throws Exception {
        SuperblockClaimsExtended mySuperblockClaims;
        if (isChallenger) {
            mySuperblockClaims = superblockClaimsForChallenges;
        } else {
            mySuperblockClaims = superblockClaims;
        }

        withdrawAllFundsExceptLimit(account, mySuperblockClaims);
    }

    /**
     * Marks a superblock as invalid.
     * @param superblockId Superblock to be invalidated.
     * @param validator
     * @throws Exception
     */
    public void invalidate(Keccak256Hash superblockId, String validator) throws Exception {
        CompletableFuture<TransactionReceipt> futureReceipt =
                superblocks.invalidate(superblockId.getBytes(), validator).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("Invalidated superblock {}", superblockId));
        futureReceipt.exceptionally(t -> {
            log.error("Failed to invalidate superblock {}.", superblockId);
            log.error(t.getMessage(), t);
            return null;
        });
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

    public BigInteger getChainHeight() throws Exception {
        return superblocks.getChainHeight().send();
    }

    /**
     * Listens to NewSuperblock events from DogeSuperblocks contract within a given block window
     * and parses web3j-generated instances into easier to manage SuperblockEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All NewSuperblock events from DogeSuperblocks as SuperblockEvent objects.
     * @throws IOException
     */
    public List<SuperblockEvent> getNewSuperblocks(long startBlock, long endBlock) throws IOException {
        List<SuperblockEvent> result = new ArrayList<>();
        List<DogeSuperblocks.NewSuperblockEventResponse> newSuperblockEvents =
                superblocks.getNewSuperblockEvents(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeSuperblocks.NewSuperblockEventResponse response : newSuperblockEvents) {
            SuperblockEvent newSuperblockEvent = new SuperblockEvent();
            newSuperblockEvent.superblockId = Keccak256Hash.wrap(response.superblockHash);
            newSuperblockEvent.who = response.who;
            result.add(newSuperblockEvent);
        }

        return result;
    }

    /**
     * Listens to ApprovedSuperblock events from DogeSuperblocks contract within a given block window
     * and parses web3j-generated instances into easier to manage SuperblockEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All ApprovedSuperblock events from DogeSuperblocks as SuperblockEvent objects.
     * @throws IOException
     */
    public List<SuperblockEvent> getApprovedSuperblocks(long startBlock, long endBlock)
            throws IOException {
        List<SuperblockEvent> result = new ArrayList<>();
        List<DogeSuperblocks.ApprovedSuperblockEventResponse> approvedSuperblockEvents =
                superblocks.getApprovedSuperblockEvents(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeSuperblocks.ApprovedSuperblockEventResponse response : approvedSuperblockEvents) {
            SuperblockEvent approvedSuperblockEvent = new SuperblockEvent();
            approvedSuperblockEvent.superblockId = Keccak256Hash.wrap(response.superblockHash);
            approvedSuperblockEvent.who = response.who;
            result.add(approvedSuperblockEvent);
        }

        return result;
    }

    /**
     * Listens to SemiApprovedSuperblock events from DogeSuperblocks contract within a given block window
     * and parses web3j-generated instances into easier to manage SuperblockEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All SemiApprovedSuperblock events from DogeSuperblocks as SuperblockEvent objects.
     * @throws IOException
     */
    public List<SuperblockEvent> getSemiApprovedSuperblocks(long startBlock, long endBlock)
            throws IOException {
        List<SuperblockEvent> result = new ArrayList<>();
        List<DogeSuperblocks.SemiApprovedSuperblockEventResponse> semiApprovedSuperblockEvents =
                superblocks.getSemiApprovedSuperblockEvents(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeSuperblocks.SemiApprovedSuperblockEventResponse response : semiApprovedSuperblockEvents) {
            SuperblockEvent semiApprovedSuperblockEvent = new SuperblockEvent();
            semiApprovedSuperblockEvent.superblockId = Keccak256Hash.wrap(response.superblockHash);
            semiApprovedSuperblockEvent.who = response.who;
            result.add(semiApprovedSuperblockEvent);
        }

        return result;
    }

    /**
     * Listens to InvalidSuperblock events from DogeSuperblocks contract within a given block window
     * and parses web3j-generated instances into easier to manage SuperblockEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All InvalidSuperblock events from DogeSuperblocks as SuperblockEvent objects.
     * @throws IOException
     */
    public List<SuperblockEvent> getInvalidSuperblocks(long startBlock, long endBlock)
            throws IOException {
        List<SuperblockEvent> result = new ArrayList<>();
        List<DogeSuperblocks.InvalidSuperblockEventResponse> invalidSuperblockEvents =
                superblocks.getInvalidSuperblockEvents(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeSuperblocks.InvalidSuperblockEventResponse response : invalidSuperblockEvents) {
            SuperblockEvent invalidSuperblockEvent = new SuperblockEvent();
            invalidSuperblockEvent.superblockId = Keccak256Hash.wrap(response.superblockHash);
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
        return battleManager.superblockDuration().send();
    }

    public BigInteger getSuperblockDelay() throws Exception {
        return superblockClaims.superblockDelay().send();
    }

    public BigInteger getSuperblockTimeout() throws Exception {
        return superblockClaims.superblockTimeout().send();
    }

    public BigInteger getBattleReward() throws Exception {
        return superblockClaims.battleReward().send();
    }

    public Keccak256Hash getBestSuperblockId() throws Exception {
        return Keccak256Hash.wrap(superblocks.getBestSuperblock().send());
    }

    /**
     * Looks up a superblock's submission time in SuperblockClaims.
     * @param superblockId Superblock hash.
     * @return When the superblock was submitted.
     * @throws Exception
     */
    public long getNewEventTimestamp(Keccak256Hash superblockId) throws Exception {
        return superblockClaims.getNewSuperblockEventTimestamp(superblockId.getBytes()).send().longValueExact();
    }


    /* ---------------------------------- */
    /* ---- SuperblockClaims section ---- */
    /* ---------------------------------- */


    /* ---- CONFIRMING/REJECTING ---- */

    /**
     * Approves, semi-approves or invalidates a superblock depending on its situation.
     * See SuperblockClaims source code for further reference.
     * @param superblockId Superblock to be approved, semi-approved or invalidated.
     * @param account Caller's address.
     * @param isChallenger Whether the caller is challenging. Used to determine
     *                     which SuperblockClaims should be used for withdrawing funds.
     */
    public void checkClaimFinished(Keccak256Hash superblockId, String account, boolean isChallenger)
            throws Exception {
        SuperblockClaimsExtended mySuperblockClaims;
        if (isChallenger) {
            mySuperblockClaims = superblockClaimsForChallenges;
        } else {
            mySuperblockClaims = superblockClaims;
        }

        CompletableFuture<TransactionReceipt> futureReceipt =
                mySuperblockClaims.checkClaimFinished(superblockId.getBytes()).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("checkClaimFinished receipt {}", receipt.toString())
        );
        futureReceipt.exceptionally(t -> {
            log.error("Failed to check claim for superblock {}.", superblockId);
            log.error(t.getMessage(), t);
            return null;
        });
    }

    /**
     * Confirms a semi-approved superblock with a high enough semi-approved descendant;
     * 'high enough' means that superblock.height - descendant.height is greater than or equal
     * to the number of confirmations necessary for appoving a superblock.
     * See SuperblockClaims source code for further reference.
     * @param superblockId Superblock to be confirmed.
     * @param descendantId Its highest semi-approved descendant.
     * @param account Caller's address.
     */
    public void confirmClaim(Keccak256Hash superblockId, Keccak256Hash descendantId, String account) throws Exception {
        CompletableFuture<TransactionReceipt> futureReceipt =
                superblockClaims.confirmClaim(superblockId.getBytes(), descendantId.getBytes()).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("confirmClaim receipt {}", receipt.toString())
        );
    }

    /**
     * Rejects a claim.
     * See SuperblockClaims source code for further reference.
     * @param superblockId ID of superblock to be rejected.
     * @param account Caller's address.
     * @throws Exception
     */
    public void rejectClaim(Keccak256Hash superblockId, String account) throws Exception {
        CompletableFuture<TransactionReceipt> futureReceipt =
                superblockClaims.rejectClaim(superblockId.getBytes()).sendAsync();
        futureReceipt.thenAcceptAsync( (TransactionReceipt receipt) ->
                log.info("rejectClaim receipt {}", receipt.toString())
        );
        futureReceipt.exceptionally(t -> {
            log.error("Failed to reject claim for superblock {}.", superblockId);
            log.error(t.getMessage(), t);
            return null;
        });
    }


    /* ---- BATTLE EVENT RETRIEVAL METHODS AND CLASSES ---- */

    /**
     * Listens to NewBattle events from DogeBattleManager contract within a given block window
     * and parses web3j-generated instances into easier to manage NewBattleEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All NewBattle events from DogeBattleManager as NewBattleEvent objects.
     * @throws IOException
     */
    public List<NewBattleEvent> getNewBattleEvents(long startBlock, long endBlock) throws IOException {
        List<NewBattleEvent> result = new ArrayList<>();
        List<DogeBattleManager.NewBattleEventResponse> newBattleEvents =
                battleManagerForChallenges.getNewBattleEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeBattleManager.NewBattleEventResponse response : newBattleEvents) {
            NewBattleEvent newBattleEvent = new NewBattleEvent();
            newBattleEvent.superblockId = Keccak256Hash.wrap(response.superblockHash);
            newBattleEvent.sessionId = Keccak256Hash.wrap(response.sessionId);
            newBattleEvent.submitter = response.submitter;
            newBattleEvent.challenger = response.challenger;
            result.add(newBattleEvent);
        }

        return result;
    }

    /**
     * Listens to ChallengerConvicted events from a given DogeBattleManager contract within a given block window
     * and parses web3j-generated instances into easier to manage ChallengerConvictedEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @param myBattleManager DogeBattleManager contract that the caller is using to handle its battles.
     * @return All ChallengerConvicted events from DogeBattleManager as ChallengerConvictedEvent objects.
     * @throws IOException
     */
    public List<ChallengerConvictedEvent> getChallengerConvictedEvents(long startBlock, long endBlock,
                                                                       DogeBattleManagerExtended myBattleManager)
            throws IOException {
        List<ChallengerConvictedEvent> result = new ArrayList<>();
        List<DogeBattleManager.ChallengerConvictedEventResponse> challengerConvictedEvents =
                myBattleManager.getChallengerConvictedEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeBattleManager.ChallengerConvictedEventResponse response : challengerConvictedEvents) {
            ChallengerConvictedEvent challengerConvictedEvent = new ChallengerConvictedEvent();
            challengerConvictedEvent.superblockId = Keccak256Hash.wrap(response.superblockHash);
            challengerConvictedEvent.sessionId = Keccak256Hash.wrap(response.sessionId);
            challengerConvictedEvent.challenger = response.challenger;
            result.add(challengerConvictedEvent);
        }

        return result;
    }

    /**
     * Listens to SubmitterConvicted events from a given DogeBattleManager contract within a given block window
     * and parses web3j-generated instances into easier to manage SubmitterConvictedEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @param myBattleManager DogeBattleManager contract that the caller is using to handle its battles.
     * @return All SubmitterConvicted events from DogeBattleManager as SubmitterConvictedEvent objects.
     * @throws IOException
     */
    public List<SubmitterConvictedEvent> getSubmitterConvictedEvents(long startBlock, long endBlock,
                                                                     DogeBattleManagerExtended myBattleManager)
            throws IOException {
        List<SubmitterConvictedEvent> result = new ArrayList<>();
        List<DogeBattleManager.SubmitterConvictedEventResponse> submitterConvictedEvents =
                myBattleManager.getSubmitterConvictedEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeBattleManager.SubmitterConvictedEventResponse response : submitterConvictedEvents) {
            SubmitterConvictedEvent submitterConvictedEvent = new SubmitterConvictedEvent();
            submitterConvictedEvent.superblockId = Keccak256Hash.wrap(response.superblockHash);
            submitterConvictedEvent.sessionId = Keccak256Hash.wrap(response.sessionId);
            submitterConvictedEvent.submitter = response.submitter;
            result.add(submitterConvictedEvent);
        }

        return result;
    }

    // Event wrapper classes

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

    /**
     * Listens to QueryBlockHeader events from DogeBattleManager contract within a given block window
     * and parses web3j-generated instances into easier to manage QueryBlockHeaderEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All QueryBlockHeader events from DogeBattleManager as QueryBlockHeaderEvent objects.
     * @throws IOException
     */
    public List<QueryBlockHeaderEvent> getBlockHeaderQueries(long startBlock, long endBlock)
            throws IOException {
        List<QueryBlockHeaderEvent> result = new ArrayList<>();
        List<DogeBattleManager.QueryBlockHeaderEventResponse> queryBlockHeaderEvents =
                battleManager.getQueryBlockHeaderEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeBattleManager.QueryBlockHeaderEventResponse response : queryBlockHeaderEvents) {
            QueryBlockHeaderEvent queryBlockHeaderEvent = new QueryBlockHeaderEvent();
            queryBlockHeaderEvent.superblockId = Keccak256Hash.wrap(response.superblockHash);
            queryBlockHeaderEvent.sessionId = Keccak256Hash.wrap(response.sessionId);
            queryBlockHeaderEvent.submitter = response.submitter;
            queryBlockHeaderEvent.dogeBlockHash = Sha256Hash.wrap(response.blockSha256Hash);
            result.add(queryBlockHeaderEvent);
        }

        return result;
    }

    /**
     * Listens to QueryMerkleRootHashes events from DogeBattleManager contract within a given block window
     * and parses web3j-generated instances into easier to manage QueryMerkleRootHashesEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All QueryMerkleRootHashes events from DogeBattleManager as QueryMerkleRootHashesEvent objects.
     * @throws IOException
     */
    public List<QueryMerkleRootHashesEvent> getMerkleRootHashesQueries(long startBlock, long endBlock)
            throws IOException {
        List<QueryMerkleRootHashesEvent> result = new ArrayList<>();
        List<DogeBattleManager.QueryMerkleRootHashesEventResponse> queryMerkleRootHashesEvents =
                battleManager.getQueryMerkleRootHashesEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeBattleManager.QueryMerkleRootHashesEventResponse response : queryMerkleRootHashesEvents) {
            QueryMerkleRootHashesEvent queryMerkleRootHashesEvent = new QueryMerkleRootHashesEvent();
            queryMerkleRootHashesEvent.superblockId = Keccak256Hash.wrap(response.superblockHash);
            queryMerkleRootHashesEvent.sessionId = Keccak256Hash.wrap(response.sessionId);
            queryMerkleRootHashesEvent.submitter = response.submitter;
            result.add(queryMerkleRootHashesEvent);
        }

        return result;
    }

    // Event wrapper classes

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

    /**
     * Listens to RespondMerkleRootHashes events from DogeBattleManager contract within a given block window
     * and parses web3j-generated instances into easier to manage RespondMerkleRootHashesEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All RespondMerkleRootHashes events from DogeBattleManager as RespondMerkleRootHashesEvent objects.
     * @throws IOException
     */
    public List<RespondMerkleRootHashesEvent> getRespondMerkleRootHashesEvents(long startBlock, long endBlock)
            throws IOException {
        List<RespondMerkleRootHashesEvent> result = new ArrayList<>();
        List<DogeBattleManager.RespondMerkleRootHashesEventResponse> respondMerkleRootHashesEvents =
                battleManagerForChallenges.getRespondMerkleRootHashesEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeBattleManager.RespondMerkleRootHashesEventResponse response : respondMerkleRootHashesEvents) {
            RespondMerkleRootHashesEvent respondMerkleRootHashesEvent = new RespondMerkleRootHashesEvent();
            respondMerkleRootHashesEvent.superblockId = Keccak256Hash.wrap(response.superblockHash);
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

    /**
     * Listens to RespondBlockHeader events from DogeBattleManager contract within a given block window
     * and parses web3j-generated instances into easier to manage RespondBlockHeaderEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All RespondBlockHeader events from DogeBattleManager as RespondBlockHeaderEvent objects.
     * @throws IOException
     */
    public List<RespondBlockHeaderEvent> getRespondBlockHeaderEvents(long startBlock, long endBlock)
            throws IOException {
        List<RespondBlockHeaderEvent> result = new ArrayList<>();
        List<DogeBattleManager.RespondBlockHeaderEventResponse> respondBlockHeaderEvents =
                battleManagerForChallenges.getRespondBlockHeaderEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeBattleManager.RespondBlockHeaderEventResponse response : respondBlockHeaderEvents) {
            RespondBlockHeaderEvent respondBlockHeaderEvent = new RespondBlockHeaderEvent();
            respondBlockHeaderEvent.superblockId = Keccak256Hash.wrap(response.superblockHash);
            respondBlockHeaderEvent.sessionId = Keccak256Hash.wrap(response.sessionId);
            respondBlockHeaderEvent.challenger = response.challenger;
            respondBlockHeaderEvent.blockScryptHash = response.blockScryptHash;
            respondBlockHeaderEvent.blockHeader = response.blockHeader;
            respondBlockHeaderEvent.powBlockHeader = response.powBlockHeader;
            result.add(respondBlockHeaderEvent);
        }

        return result;
    }

    // Event wrapper classes

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
        public byte[] blockScryptHash; // TODO: see if these three fields should be made immutable
        public byte[] blockHeader;
        public byte[] powBlockHeader;
    }

    public static class SuperblockBattleDecidedEvent {
        public Keccak256Hash sessionId;
        public String winner;
        public String loser;
    }

    public List<ErrorBattleEvent> getErrorBattleEvents(long startBlock, long endBlock) throws IOException {
        List<ErrorBattleEvent> result = new ArrayList<>();
        List<DogeBattleManager.ErrorBattleEventResponse> errorBattleEvents =
                battleManager.getErrorBattleEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeBattleManager.ErrorBattleEventResponse response : errorBattleEvents) {
            ErrorBattleEvent errorBattleEvent = new ErrorBattleEvent();
            errorBattleEvent.sessionId = Keccak256Hash.wrap(response.sessionId);
            errorBattleEvent.err = response.err;
            result.add(errorBattleEvent);
        }

        return result;
    }

    public static class ErrorBattleEvent {
        public Keccak256Hash sessionId;
        public BigInteger err;
    }

    /**
     * Listens to RequestScryptHashValidation events from DogeBattleManager contract within a given block window
     * and parses web3j-generated instances into easier to manage RequestScryptHashValidationEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All RequestScryptHashValidation events from DogeBattleManager as RequestScryptHashValidationEvent objects.
     * @throws IOException
     */
    public List<RequestScryptHashValidationEvent> getRequestScryptHashValidation(long startBlock, long endBlock)
            throws IOException {
        List<RequestScryptHashValidationEvent> result = new ArrayList<>();
        List<DogeBattleManager.RequestScryptHashValidationEventResponse> requestScryptHashValidationEvents =
                battleManager.getRequestScryptHashValidationEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeBattleManager.RequestScryptHashValidationEventResponse response : requestScryptHashValidationEvents) {
            RequestScryptHashValidationEvent requestScryptHashValidationEvent = new RequestScryptHashValidationEvent();
            requestScryptHashValidationEvent.superblockId = Keccak256Hash.wrap(response.superblockHash);
            requestScryptHashValidationEvent.sessionId = Keccak256Hash.wrap(response.sessionId);
            requestScryptHashValidationEvent.blockScryptHash = ScryptHash.wrap(response.blockScryptHash);
            requestScryptHashValidationEvent.blockHeader = response.blockHeader;
            requestScryptHashValidationEvent.proposalId = Keccak256Hash.wrap(response.proposalId);
            requestScryptHashValidationEvent.submitter = response.submitter;
            result.add(requestScryptHashValidationEvent);
        }

        return result;
    }

    public static class RequestScryptHashValidationEvent {
        public Keccak256Hash superblockId;
        public Keccak256Hash sessionId;
        public ScryptHash blockScryptHash;
        public byte[] blockHeader;
        public Keccak256Hash proposalId;
        public String submitter;
    }

    /**
     * Listens to ResolvedScryptHashValidation events from DogeBattleManager contract within a given block window
     * and parses web3j-generated instances into easier to manage ResolvedScryptHashValidationEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All ResolvedScryptHashValidation events from DogeBattleManager
     *         as ResolvedScryptHashValidationEvent objects.
     * @throws IOException
     */
    public List<ResolvedScryptHashValidationEvent> getResolvedScryptHashValidation(long startBlock, long endBlock)
            throws IOException {
        List<ResolvedScryptHashValidationEvent> result = new ArrayList<>();
        List<DogeBattleManager.ResolvedScryptHashValidationEventResponse> resolvedScryptHashValidationEvents =
                battleManagerForChallenges.getResolvedScryptHashValidationEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (DogeBattleManager.ResolvedScryptHashValidationEventResponse response : resolvedScryptHashValidationEvents) {
            ResolvedScryptHashValidationEvent resolvedScryptHashValidationEvent = new ResolvedScryptHashValidationEvent();
            resolvedScryptHashValidationEvent.superblockId = Keccak256Hash.wrap(response.superblockHash);
            resolvedScryptHashValidationEvent.sessionId = Keccak256Hash.wrap(response.sessionId);
            resolvedScryptHashValidationEvent.blockScryptHash = ScryptHash.wrap(response.blockScryptHash);
            resolvedScryptHashValidationEvent.blockSha256Hash = Sha256Hash.wrap(response.blockSha256Hash);
            resolvedScryptHashValidationEvent.proposalId = Keccak256Hash.wrap(response.proposalId);
            resolvedScryptHashValidationEvent.challenger = response.challenger;
            resolvedScryptHashValidationEvent.valid = response.valid;
            result.add(resolvedScryptHashValidationEvent);
        }

        return result;
    }

    public static class ResolvedScryptHashValidationEvent {
        public Keccak256Hash superblockId;
        public Keccak256Hash sessionId;
        public ScryptHash blockScryptHash;
        public Sha256Hash blockSha256Hash;
        public Keccak256Hash proposalId;
        public String challenger;
        public boolean valid;
    }


    /* ---- GETTERS ---- */

    public long getSuperblockConfirmations() throws Exception {
        return superblockClaims.superblockConfirmations().send().longValue();
    }

    // TODO: see if this is necessary later
    public long getSuperblockConfirmationsForChallenges() throws Exception {
        return superblockClaimsForChallenges.superblockConfirmations().send().longValue();
    }


    /* ---------------------------------- */
    /* --------- Battle section --------- */
    /* ---------------------------------- */

    /**
     * Responds to a Doge block header query.
     * @param superblockId Hash of the superblock that the Doge block hash is supposedly in.
     * @param sessionId Battle session ID.
     * @param dogeBlock Doge block whose header was requested.
     * @param account Caller's address.
     */
    public void respondBlockHeader(Keccak256Hash superblockId, Keccak256Hash sessionId,
                                   AltcoinBlock dogeBlock, String account) throws Exception {
        makeDepositIfNeeded(account, superblockClaims, verifySuperblockCost);
        byte[] scryptHashBytes = dogeBlock.getScryptHash().getReversedBytes();
        byte[] blockHeaderBytes = dogeBlock.bitcoinSerialize();
        CompletableFuture<TransactionReceipt> futureReceipt = battleManager.respondBlockHeader(
                superblockId.getBytes(), sessionId.getBytes(), scryptHashBytes, blockHeaderBytes).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("Responded to block header query for Doge block {}, session {}, superblock {}. Receipt: {}",
                        dogeBlock.getHash(), sessionId, superblockId, receipt)
        );
        futureReceipt.exceptionally(t -> {
            log.error("Failed to respond block header for Doge block {}, session {}, superblock {}.",
                    dogeBlock.getHash(), sessionId, superblockId);
            log.error(t.getMessage(), t);
            return null;
        });
    }

    /**
     * Responds to a Merkle root hashes query.
     * @param superblockId Hash of the superblock whose Merkle root hashes were requested.
     * @param sessionId Battle session ID.
     * @param dogeBlockHashes Doge block hashes that are supposedly in the superblock.
     * @param account Caller's address.
     */
    public void respondMerkleRootHashes(Keccak256Hash superblockId, Keccak256Hash sessionId,
                                        List<Sha256Hash> dogeBlockHashes, String account)
            throws Exception {
        List<byte[]> rawHashes = new ArrayList<>();
        makeDepositIfNeeded(account, superblockClaims, verifySuperblockCost);
        for (Sha256Hash dogeBlockHash : dogeBlockHashes)
            rawHashes.add(dogeBlockHash.getBytes());
        CompletableFuture<TransactionReceipt> futureReceipt =
                battleManager.respondMerkleRootHashes(superblockId.getBytes(), sessionId.getBytes(), rawHashes).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("Responded to Merkle root hashes query for session {}, superblock {}. Receipt: {}",
                        sessionId, superblockId, receipt.toString()));
        futureReceipt.exceptionally(t -> {
            log.error("Failed to respond to Merkle root hashes query for session {}, superblock {}.",
                    sessionId, superblockId);
            log.error(t.getMessage(), t);
            return null;
        });
    }

    /**
     * Requests the header of a Doge block in a certain superblock.
     * @param superblockId Hash of the superblock that the Doge block hash is supposedly in.
     * @param sessionId Battle session ID.
     * @param dogeBlockHash Hash of the Doge block whose header is being queried.
     * @param account Caller's address.
     * */
    public void queryBlockHeader(Keccak256Hash superblockId, Keccak256Hash sessionId,
                                 Sha256Hash dogeBlockHash, String account) throws Exception {
        makeDepositIfNeeded(account, superblockClaimsForChallenges, respondBlockHeaderCost);
        CompletableFuture<TransactionReceipt> futureReceipt =
                battleManagerForChallenges.queryBlockHeader(superblockId.getBytes(),
                sessionId.getBytes(), dogeBlockHash.getBytes()).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("Requested Doge block header for block {}, superblock {}", dogeBlockHash, superblockId));
        futureReceipt.exceptionally(t -> {
            log.error("Failed to query block header for Doge block {}, session {}, superblock {}.",
                    dogeBlockHash, sessionId, superblockId);
            log.error(t.getMessage(), t);
            return null;
        });
    }

    // TODO: see if the challenger should know which superblock this is

    /**
     * Verifies a challenged superblock once the battle is done with.
     * @param sessionId Battle session ID.
     * @param myBattleManager DogeBattleManager contract that the caller is using to handle its battles.
     * @throws Exception
     */
    public void verifySuperblock(Keccak256Hash sessionId, DogeBattleManagerExtended myBattleManager) throws Exception {
        CompletableFuture<TransactionReceipt> futureReceipt =
                myBattleManager.verifySuperblock(sessionId.getBytes()).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("Verified superblock for session {}", sessionId));
        futureReceipt.exceptionally(t -> {
            log.error("Failed to verify superblock for session {}.", sessionId);
            log.error(t.getMessage(), t);
            return null;
        });
    }

    /**
     * Calls timeout for a session where a participant hasn't responded in time, thus closing the battle.
     * @param sessionId Battle session ID.
     * @param myBattleManager DogeBattleManager contract that the caller is using to handle its battles.
     * @throws Exception
     */
    public void timeout(Keccak256Hash sessionId, DogeBattleManagerExtended myBattleManager) throws Exception {
        CompletableFuture<TransactionReceipt> futureReceipt = myBattleManager.timeout(sessionId.getBytes()).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("Called timeout for session {}", sessionId));
        futureReceipt.exceptionally(t -> {
            log.error("Failed to timeout session {}.", sessionId);
            log.error(t.getMessage(), t);
            return null;
        });
    }


    /* ---- CHALLENGER ---- */

    /**
     * Challenges a superblock.
     * @param superblockId Hash of superblock to be challenged.
     * @param account Caller's address.
     * @throws InterruptedException
     */
    public void challengeSuperblock(Keccak256Hash superblockId, String account)
            throws InterruptedException, Exception {
        // Make necessary deposit to cover reward
        makeDepositIfNeeded(account, superblockClaimsForChallenges, minChallengeDeposit);

        CompletableFuture<TransactionReceipt> futureReceipt =
                superblockClaimsForChallenges.challengeSuperblock(superblockId.getBytes()).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("challengeSuperblock receipt {}", receipt.toString()));
        futureReceipt.exceptionally(t -> {
            log.error("Failed to challenge superblock {}.", superblockId);
            log.error(t.getMessage(), t);
            return null;
        });
    }

    /**
     * Makes a deposit for challenging a superblock.
     * @param weiValue Wei to be deposited.
     * @throws InterruptedException
     */
    private void makeChallengerDeposit(BigInteger weiValue)
            throws InterruptedException {
        CompletableFuture<TransactionReceipt> futureReceipt = superblockClaimsForChallenges.makeDeposit(weiValue).sendAsync();
        log.info("Challenger deposited {} wei.", weiValue);

        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("makeChallengerDeposit receipt {}", receipt.toString())
        );
        futureReceipt.exceptionally(t -> {
            log.error("Failed to deposit {} weis.", weiValue);
            log.error(t.getMessage(), t);
            return null;
        });
        Thread.sleep(200); // in case the transaction takes some time to complete
    }

    /**
     * Requests a list of all the hashes in a certain superblock.
     * @param superblockId Hash of superblock being challenged.
     * @param sessionId Battle session ID.
     * @param account Caller's address.
     * @throws InterruptedException
     */
    public void queryMerkleRootHashes(Keccak256Hash superblockId, Keccak256Hash sessionId, String account)
            throws InterruptedException, Exception {
        log.info("Querying Merkle root hashes for superblock {}", superblockId);
        makeDepositIfNeeded(account, superblockClaimsForChallenges, respondMerkleRootHashesCost);
        CompletableFuture<TransactionReceipt> futureReceipt = battleManagerForChallenges.queryMerkleRootHashes(
                superblockId.getBytes(), sessionId.getBytes()).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("queryMerkleRootHashes receipt {}", receipt.toString()));
        futureReceipt.exceptionally(t -> {
            log.error("Failed to query Merkle root hashes for session {}, superblock {}.",
                    sessionId, superblockId);
            log.error(t.getMessage(), t);
            return null;
        });
    }

    //TODO: document this and all other scrypt hash validation functions
    public void requestScryptHashValidation(Keccak256Hash superblockId, Keccak256Hash sessionId,
                                            Sha256Hash blockSha256Hash, String account) throws Exception {
        log.info("Requesting scrypt validation for block {} session {} superblock {}",
                blockSha256Hash, sessionId, superblockId);
        makeDepositIfNeeded(account, superblockClaimsForChallenges, respondBlockHeaderCost);
        CompletableFuture<TransactionReceipt> futureReceipt = battleManagerForChallenges.requestScryptHashValidation(
                superblockId.getBytes(), sessionId.getBytes(), blockSha256Hash.getBytes()).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("requestScryptHashValidation receipt {}", receipt.toString()));
        futureReceipt.exceptionally(t -> {
            log.error("Failed to request scrypt hash validation for Doge block {}, session {}, superblock {}.",
                    blockSha256Hash, sessionId, superblockId);
            log.error(t.getMessage(), t);
            return null;
        });
    }


    /* ---- GETTERS ---- */

    public boolean getClaimExists(Keccak256Hash superblockId) throws Exception {
        return superblockClaims.getClaimExists(superblockId.getBytes()).send();
    }

    public String getClaimSubmitter(Keccak256Hash superblockId) throws Exception {
        return superblockClaims.getClaimSubmitter(superblockId.getBytes()).send();
    }

    public boolean getClaimDecided(Keccak256Hash superblockId) throws Exception {
        return superblockClaims.getClaimDecided(superblockId.getBytes()).send();
    }

    public boolean getClaimInvalid(Keccak256Hash superblockId) throws Exception {
        return superblockClaims.getClaimInvalid(superblockId.getBytes()).send();
    }

    public boolean getClaimVerificationOngoing(Keccak256Hash superblockId) throws Exception {
        return superblockClaims.getClaimVerificationOngoing(superblockId.getBytes()).send();
    }

    public BigInteger getClaimChallengeTimeoutBigInteger(Keccak256Hash superblockId) throws Exception {
        return superblockClaims.getClaimChallengeTimeout(superblockId.getBytes()).send();
    }

    public Date getClaimChallengeTimeoutDate(Keccak256Hash superblockId) throws Exception {
        return new Date(getClaimChallengeTimeoutBigInteger(superblockId).longValue() * 1000);
    }

    public int getClaimRemainingChallengers(Keccak256Hash superblockId) throws Exception {
        return superblockClaims.getClaimRemainingChallengers(superblockId.getBytes()).send().intValue();
    }

    public boolean getInBattleAndSemiApprovable(Keccak256Hash superblockId) throws Exception {
        return superblockClaims.getInBattleAndSemiApprovable(superblockId.getBytes()).send();
    }

    public List<String> getClaimChallengers(Keccak256Hash superblockId) throws Exception {
        return superblockClaims.getClaimChallengers(superblockId.getBytes()).send();
    }

    public boolean getChallengerHitTimeout(Keccak256Hash sessionId) throws Exception {
        return battleManager.getChallengerHitTimeout(sessionId.getBytes()).send();
    }

    public boolean getSubmitterHitTimeout(Keccak256Hash sessionId) throws Exception {
        return battleManagerForChallenges.getSubmitterHitTimeout(sessionId.getBytes()).send();
    }

    public List<Sha256Hash> getDogeBlockHashes(Keccak256Hash sessionId) throws Exception {
        List<Sha256Hash> result = new ArrayList<>();
        List<byte[]> rawHashes = battleManager.getDogeBlockHashes(sessionId.getBytes()).send();
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

    /**
     * Relays a Dogecoin transaction to Dogethereum contracts.
     * @param tx Transaction to be relayed.
     * @param operatorPublicKeyHash
     * @param block Dogecoin block that the transaction is in.
     * @param superblock Superblock that the Dogecoin block is in.
     * @param txPMT Partial Merkle tree for constructing an SPV proof of the transaction's existence in the Doge block.
     * @param superblockPMT Partial Merkle tree for constructing an SPV proof
     *                      of the Doge block's existence in the superblock.
     * @throws Exception
     */
    public void sendRelayTx(org.bitcoinj.core.Transaction tx, byte[] operatorPublicKeyHash, AltcoinBlock block,
                            Superblock superblock, PartialMerkleTree txPMT, PartialMerkleTree superblockPMT, boolean isLockTx)
            throws Exception {
        byte[] dogeBlockHeader = Arrays.copyOfRange(block.bitcoinSerialize(), 0, 80);
        Sha256Hash dogeBlockHash = block.getHash();
        log.info("About to send to the bridge doge tx hash {}. Block hash {}", tx.getTxId(), dogeBlockHash);

        byte[] txSerialized = tx.bitcoinSerialize();

        // Construct SPV proof for transaction
        BigInteger txIndex = BigInteger.valueOf(txPMT.getTransactionIndex(tx.getTxId()));
        List<Sha256Hash> txSiblingsSha256Hash = txPMT.getTransactionPath(tx.getTxId());
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

        CompletableFuture<TransactionReceipt> futureReceipt = null;
        if (isLockTx) {
            futureReceipt = superblocksForRelayTxs.relayLockTx(txSerialized,
                    operatorPublicKeyHash, txIndex, txSiblingsBigInteger, dogeBlockHeader, dogeBlockIndex,
                    dogeBlockSiblingsBigInteger, superblock.getSuperblockId().getBytes(), targetContract).sendAsync();
        } else {
            BigInteger unlockIndex = findUnlock(tx, operatorPublicKeyHash);
            futureReceipt = superblocksForRelayTxs.relayUnlockTx(txSerialized,
                    operatorPublicKeyHash, txIndex, txSiblingsBigInteger, dogeBlockHeader, dogeBlockIndex,
                    dogeBlockSiblingsBigInteger, superblock.getSuperblockId().getBytes(), targetContract, unlockIndex).sendAsync();
        }
        log.info("Relayed Tx {}", tx.getTxId());
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("Relay tx receipt {}.", receipt.toString())
        );
        futureReceipt.exceptionally(t -> {
            log.error("Failed to relay tx {}.", tx.getTxId());
            log.error(t.getMessage(), t);
            return null;
        });
    }

    /**
     * Finds the unlock index that matches the unlock performed by a tx.
     * This assumes the unlock tx is well-formed.
     * @param tx Unlock tx.
     * @param operatorPublicKeyHash PKH of the operator performing the unlock.
     * @return The unlock index that matches this tx.
     */
    private BigInteger findUnlock(org.bitcoinj.core.Transaction tx, byte[] operatorPublicKeyHash) throws Exception {
        // Iterate Unlocks beginning with the most recent one and find the matching unlock request.
        BigInteger totalUnlocks = dogeToken.unlockIdx().send();
        for (BigInteger unlockIndex = totalUnlocks.subtract(BigInteger.ONE);
             unlockIndex.compareTo(BigInteger.ZERO) >= 0;
             unlockIndex = unlockIndex.subtract(BigInteger.ONE))
        {
            Unlock unlock = getUnlock(unlockIndex);
            TransactionOutPoint firstOutpoint = tx.getInput(0).getOutpoint();
            UTXO firstSelectedUtxo = unlock.selectedUtxos.get(0);
            if (firstOutpoint.getHash().compareTo(firstSelectedUtxo.getHash()) == 0 &&
                firstOutpoint.getIndex() == firstSelectedUtxo.getIndex()) {
                return unlockIndex;
            }
        }

        throw new RuntimeException("Didn't find a pending unlock that matches tx " + tx.getTxId());
    }


    /* ---------------------------------- */
    /* --------- Unlock section --------- */
    /* ---------------------------------- */

    //TODO: learn more about unlock and document all of these

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
        return getUnlock(BigInteger.valueOf(unlockRequestId));
    }

    public Unlock getUnlock(BigInteger unlockRequestId) throws Exception {
        Tuple9<String, byte[], BigInteger, BigInteger, BigInteger, BigInteger, List<BigInteger>, byte[], Boolean> tuple =
                dogeToken.getUnlock(unlockRequestId).send();
        Unlock unlock = new Unlock();
        unlock.from = tuple.component1();
        unlock.dogeAddress = tuple.component2();
        unlock.valueToUser = tuple.component3().longValue();
        unlock.operatorChange = tuple.component4().longValue();
        unlock.timestamp = tuple.component5().longValue();
        unlock.superblockHeight = tuple.component6().longValue();
        unlock.operatorPublicKeyHash = tuple.component8();
        unlock.completed = tuple.component9();

        List<BigInteger> selectedUtxosIndexes = tuple.component7();
        List<UTXO> selectedUtxosOutpoints = new ArrayList<>();
        for (BigInteger selectedUtxo : selectedUtxosIndexes) {
            Tuple3<BigInteger, BigInteger, BigInteger> utxo = dogeToken.getUtxo(unlock.operatorPublicKeyHash, selectedUtxo).send();
            long value = utxo.component1().longValue();
            Sha256Hash txHash = Sha256Hash.wrap(bigIntegerToHexStringPad64(utxo.component2()));
            long outputIndex = utxo.component3().longValue();
            selectedUtxosOutpoints.add(new UTXO(txHash, outputIndex, Coin.valueOf(value), 0, false, null));
        }
        unlock.selectedUtxos = selectedUtxosOutpoints;
        return unlock;
    }

    // TODO: replace with immutable data types, e.g. Sha256Hash
    public static class Unlock {
        /**
         * User's Ethereum address
         */
        public String from;
        /**
         * User's Dogecoin public key hash.
         * Dogecoins should be sent to this address in the unlock tx.
         */
        public byte[] dogeAddress;
        /**
         * Dogecoin value sent to the user in terms of satoshis.
         */
        public long valueToUser;
        /**
         * Dogecoin change kept by the operator in terms of satoshis.
         */
        public long operatorChange;
        /**
         * Ethereum timestamp of the unlock request.
         */
        public long timestamp;
        /**
         * Superblockchain height at the time of the unlock request.
         */
        public long superblockHeight;
        /**
         * Selected UTXOs for the unlock.
         */
        public List<UTXO> selectedUtxos;
        /**
         * Dogecoin public key hash of the operator.
         */
        public byte[] operatorPublicKeyHash;
        /**
         * Dogecoin public key hash of the operator.
         */
        public boolean completed;
    }


    /* ---------------------------------- */
    /* --------- Scrypt verifier -------- */
    /* ---------------------------------- */

    /**
     * Sends a scrypt hash to be verified.
     * @param sessionId Battle session ID.
     * @param superblockId Hash of the superblock containing the block whose scrypt hash is going to be verified.
     * @param proposalId // TODO: see if this parameter is necessary at all
     * @param data
     * @param blockScryptHash Scrypt hash of Doge block.
     */
    public void checkScrypt(Keccak256Hash sessionId, Keccak256Hash superblockId, Keccak256Hash proposalId,
                            byte[] data, ScryptHash blockScryptHash) {
        log.info("Open scrypt hash verification session {}, superblock {}", sessionId, superblockId);
        // TODO: parametrize weiValue when checking scrypt
        CompletableFuture<TransactionReceipt> futureReceipt = scryptVerifier.checkScrypt(
                data, blockScryptHash.getBytes(), proposalId.getBytes(), battleManager.getContractAddress(), BigInteger.ZERO).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("checkScrypt receipt {}", receipt.toString()));
        futureReceipt.exceptionally(t -> {
            log.error("Failed to open scrypt hash verification session Doge block {}, session {}, superblock {}.",
                    blockScryptHash, sessionId, superblockId);
            log.error(t.getMessage(), t);
            return null;
        });
    }

    @Value
    private static class ContractAddresses {
        String superblocks;
        String superblockClaims;
        String scryptChecker;
        String dogeToken;
        String battleManager;
    }

}