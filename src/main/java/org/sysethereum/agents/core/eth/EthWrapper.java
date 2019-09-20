package org.sysethereum.agents.core.eth;



import com.google.common.primitives.Bytes;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.bitcoinj.store.BlockStoreException;
import org.sysethereum.agents.constants.SystemProperties;
import org.sysethereum.agents.contract.*;
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
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;

import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.ipc.UnixIpcService;
import org.web3j.tx.ClientTransactionManager;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 * Helps the agent communication with the Eth blockchain.
 * @author Oscar Guindzberg
 * @author Catalina Juarros
 */
@Component
@Slf4j(topic = "EthWrapper")
public class EthWrapper implements SuperblockConstantProvider {
    private static final Logger logger = LoggerFactory.getLogger("EthWrapper");
    private final Web3j web3;
    private final Web3j web3Secondary;
    public enum ChallengeState {
        Unchallenged,             // Unchallenged submission
        Challenged               // Claims was challenged
    }
    // Extensions of contracts generated automatically by web3j
    private final SyscoinClaimManagerExtended claimManager;
    private final SyscoinClaimManagerExtended claimManagerGetter;
    private final SyscoinClaimManagerExtended claimManagerForChallenges;
    private final SyscoinClaimManagerExtended claimManagerForChallengesGetter;
    private final SyscoinBattleManagerExtended battleManager;
    private final SyscoinBattleManagerExtended battleManagerForChallenges;
    private final SyscoinBattleManagerExtended battleManagerGetter;
    private final SyscoinBattleManagerExtended battleManagerForChallengesGetter;
    private final SyscoinSuperblocksExtended superblocks;
    private final SyscoinSuperblocksExtended superblocksGetter;

    private final SystemProperties config;
    private BigInteger gasPriceMinimum;
    private BigInteger gasPriceMaximum;

    private final String generalPurposeAndSendSuperblocksAddress;
    private final String syscoinSuperblockChallengerAddress;

    private final BigInteger minProposalDeposit;
    private final SuperblockChain superblockChain;
    private final SyscoinWrapper syscoinWrapper;
    private int randomizationCounter;

    /* ---------------------------------- */
    /* ------ General code section ------ */
    /* ---------------------------------- */

    @Autowired
    public EthWrapper(
            SuperblockChain superblockChain,
            SyscoinWrapper syscoinWrapper
    ) throws Exception {
        this.superblockChain = superblockChain;
        this.syscoinWrapper = syscoinWrapper;

        setRandomizationCounter();
        config = SystemProperties.CONFIG;
        String path = config.dataDirectory() + "/geth/geth.ipc";
        String secondaryURL = config.secondaryURL();

        web3Secondary = Web3j.build(new HttpService(secondaryURL));
        Admin admin = Admin.build(new UnixIpcService(path));
        String generalAddress = config.generalPurposeAndSendSuperblocksAddress();
        if(generalAddress.length() > 0){
            PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount(generalAddress, config.generalPurposeAndSendSuperblocksUnlockPW(), BigInteger.ZERO).send();
            if (personalUnlockAccount != null && personalUnlockAccount.accountUnlocked()) {
                logger.info("general.purpose.and.send.superblocks.address is unlocked and ready to use!");
            }
            else{
                logger.warn("general.purpose.and.send.superblocks.address could not be unlocked, please check the password you set in the configuration file");
            }
        }
        String challengerAddress = config.syscoinSuperblockChallengerAddress();
        if(challengerAddress.length() > 0 && !generalAddress.equals(challengerAddress)){
            PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount(challengerAddress, config.syscoinSuperblockChallengerUnlockPW(), BigInteger.ZERO).send();
            if (personalUnlockAccount != null && personalUnlockAccount.accountUnlocked()) {
                logger.info("syscoin.superblock.challenger.address is unlocked and ready to use!");
            }
            else{
                logger.warn("syscoin.superblock.challenger.address could not be unlocked, please check the password you set in the configuration file");
            }
        }
        admin.shutdown();
        web3 = Web3j.build(new UnixIpcService(path));
        String claimManagerContractAddress;
        String battleManagerContractAddress;
        String superblocksContractAddress;

        if (config.isGanache()) {
            String networkId = config.getAgentConstants().getNetworkId();
            claimManagerContractAddress = SyscoinClaimManagerExtended.getAddress(networkId);
            battleManagerContractAddress = SyscoinBattleManagerExtended.getAddress(networkId);
            superblocksContractAddress = SyscoinSuperblocksExtended.getAddress(networkId);
            List<String> accounts = web3.ethAccounts().send().getAccounts();
            generalPurposeAndSendSuperblocksAddress = accounts.get(0);
            syscoinSuperblockChallengerAddress = accounts.get(1);
        } else {
            String networkId = config.getAgentConstants().getNetworkId();
            claimManagerContractAddress = SyscoinClaimManagerExtended.getAddress(networkId);
            battleManagerContractAddress = SyscoinBattleManagerExtended.getAddress(networkId);
            superblocksContractAddress = SyscoinSuperblocksExtended.getAddress(networkId);
            generalPurposeAndSendSuperblocksAddress = config.generalPurposeAndSendSuperblocksAddress();
            syscoinSuperblockChallengerAddress = config.syscoinSuperblockChallengerAddress();
        }

        gasPriceMinimum = BigInteger.valueOf(config.gasPriceMinimum());
        gasPriceMaximum = BigInteger.valueOf(config.gasPriceMaximum());
        BigInteger gasLimit = BigInteger.valueOf(config.gasLimit());
        updateContractFacadesGasPrice();

        claimManager = SyscoinClaimManagerExtended.load(claimManagerContractAddress, web3,
                new ClientTransactionManager(web3, generalPurposeAndSendSuperblocksAddress),
                gasPriceMinimum, gasLimit);
        assert claimManager.isValid();
        claimManagerGetter = SyscoinClaimManagerExtended.load(claimManagerContractAddress, web3Secondary,
                new ClientTransactionManager(web3Secondary, generalPurposeAndSendSuperblocksAddress),
                gasPriceMinimum, gasLimit);
        assert claimManagerGetter.isValid();
        claimManagerForChallenges = SyscoinClaimManagerExtended.load(claimManagerContractAddress, web3,
                new ClientTransactionManager(web3, syscoinSuperblockChallengerAddress),
                gasPriceMinimum, gasLimit);
        assert claimManagerForChallenges.isValid();
        claimManagerForChallengesGetter = SyscoinClaimManagerExtended.load(claimManagerContractAddress, web3Secondary,
                new ClientTransactionManager(web3Secondary, syscoinSuperblockChallengerAddress),
                gasPriceMinimum, gasLimit);
        assert claimManagerForChallengesGetter.isValid();
        battleManager = SyscoinBattleManagerExtended.load(battleManagerContractAddress, web3,
                new ClientTransactionManager(web3, generalPurposeAndSendSuperblocksAddress),
                gasPriceMinimum, gasLimit);
        assert battleManager.isValid();
        battleManagerForChallenges = SyscoinBattleManagerExtended.load(battleManagerContractAddress, web3,
                new ClientTransactionManager(web3, syscoinSuperblockChallengerAddress),
                gasPriceMinimum, gasLimit);
        assert battleManagerForChallenges.isValid();
        battleManagerGetter = SyscoinBattleManagerExtended.load(battleManagerContractAddress, web3Secondary,
                new ClientTransactionManager(web3Secondary, generalPurposeAndSendSuperblocksAddress),
                gasPriceMinimum, gasLimit);
        assert battleManagerGetter.isValid();
        battleManagerForChallengesGetter = SyscoinBattleManagerExtended.load(battleManagerContractAddress, web3Secondary,
                new ClientTransactionManager(web3Secondary, syscoinSuperblockChallengerAddress),
                gasPriceMinimum, gasLimit);
        assert battleManagerForChallengesGetter.isValid();
        superblocks = SyscoinSuperblocksExtended.load(superblocksContractAddress, web3,
                new ClientTransactionManager(web3, generalPurposeAndSendSuperblocksAddress),
                gasPriceMinimum, gasLimit);
        assert superblocks.isValid();

        superblocksGetter = SyscoinSuperblocksExtended.load(superblocksContractAddress, web3Secondary,
                new ClientTransactionManager(web3Secondary, generalPurposeAndSendSuperblocksAddress),
                gasPriceMinimum, gasLimit);
        assert superblocksGetter.isValid();

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
        return arePendingTransactionsFor(generalPurposeAndSendSuperblocksAddress);
    }

    public boolean arePendingTransactionsForChallengerAddress() throws InterruptedException, IOException {
        return arePendingTransactionsFor(syscoinSuperblockChallengerAddress);
    }

    /**
     * Checks if there are pending transactions for a given contract.
     * @param address
     * @return
     * @throws IOException
     */
    private boolean arePendingTransactionsFor(String address) throws InterruptedException, IOException {
        BigInteger latest = web3Secondary.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send().getTransactionCount();
        BigInteger pending = BigInteger.ZERO;
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
                if (claimManager != null)
                    claimManager.setGasPrice(gasPriceMinimum);
                if (claimManagerForChallenges != null)
                    claimManagerForChallenges.setGasPrice(gasPriceMinimum);
                if (superblocks != null)
                    superblocks.setGasPrice(gasPriceMinimum);
                if (battleManager != null)
                    battleManager.setGasPrice(gasPriceMinimum);
                if (claimManagerGetter != null)
                    claimManagerGetter.setGasPrice(gasPriceMinimum);
                if (claimManagerForChallengesGetter != null)
                    claimManagerForChallengesGetter.setGasPrice(gasPriceMinimum);
                if (superblocksGetter != null)
                    superblocksGetter.setGasPrice(gasPriceMinimum);
                if (battleManagerGetter != null)
                    battleManagerGetter.setGasPrice(gasPriceMinimum);
            }
        }
    }

    public String getGeneralPurposeAndSendSuperblocksAddress() {
        return generalPurposeAndSendSuperblocksAddress;
    }
    public boolean getAbilityToProposeNextSuperblock() throws Exception{
        return claimManagerGetter.getAbilityToProposeNextSuperblock(new Uint256(System.currentTimeMillis()/1000)).send().getValue();
    }
    public String getSyscoinSuperblockChallengerAddress() {
        return syscoinSuperblockChallengerAddress;
    }



    /* ---- CONTRACT GETTERS ---- */

    public SyscoinBattleManagerExtended getBattleManagerForChallenges() {
        return battleManagerForChallenges;
    }
    public SyscoinBattleManagerExtended getBattleManagerGetter() {
        return battleManagerGetter;
    }

    public SyscoinBattleManagerExtended getBattleManagerForChallengesGetter() {
        return battleManagerForChallengesGetter;
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
                !getInBattleAndSemiApprovable(currentSuperblock.getSuperblockId()) &&
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
            logger.info("Superblock {} is above the tip of the chain. Returning from getHighestSemiApprovedOrApprovedDescendant.", superblockId);
            return null;
        }
        Superblock currentSuperblock = superblockChain.getChainHead();
        while (currentSuperblock != null && !currentSuperblock.getSuperblockId().equals(superblockId) && !isSuperblockSemiApproved(currentSuperblock.getSuperblockId()) && !isSuperblockApproved(currentSuperblock.getSuperblockId())) {
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
        if (!(isSuperblockApproved(parentId) || isSuperblockSemiApproved(parentId))) {
            logger.info("Superblock {} not sent because its parent was neither approved nor semi approved.",
                    superblock.getSuperblockId());
            return false;
        }
        // if claim exists we check to ensure the superblock chain isn't "stuck" and can be re-approved to be built even if it exists
        if (getClaimExists(superblock.getSuperblockId())){
            boolean allowed = getClaimInvalid(superblock.getSuperblockId()) && getClaimDecided(superblock.getSuperblockId()) && !getClaimSubmitter(superblock.getSuperblockId()).equals(account);
            if(allowed){
                if(isSuperblockApproved(parentId)){
                    allowed = getBestSuperblockId().equals(parentId);
                }
                else allowed = isSuperblockSemiApproved(parentId);
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
        makeDepositIfNeeded(account, claimManager, claimManagerGetter, getSuperblockDeposit());


        // The parent is either approved or semi approved. We can send the superblock.
        CompletableFuture<TransactionReceipt> futureReceipt = proposeSuperblock(superblock);
        logger.info("Sent superblock {}", superblock.getSuperblockId());
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                logger.info("proposeSuperblock receipt {}", receipt.toString())
        );
        Thread.sleep(200);
        return true;
    }

    /**
     * Proposes a superblock to SyscoinClaimManager. To be called from sendStoreSuperblock.
     * @param superblock Superblock to be proposed.
     * @return
     */
    private CompletableFuture<TransactionReceipt> proposeSuperblock(Superblock superblock) {
        return claimManager.proposeSuperblock(new Bytes32(superblock.getMerkleRoot().getBytes()),
                new Uint256(superblock.getChainWork()),
                new Uint256(superblock.getLastSyscoinBlockTime()),
                new Bytes32(superblock.getLastSyscoinBlockHash().getBytes()),
                new Uint32(superblock.getlastSyscoinBlockBits()),
                new Bytes32(superblock.getParentId().getBytes())).sendAsync();
    }


    /**
     * Makes a deposit.
     * @param weiValue Wei to be deposited.
     * @param myClaimManager this.claimManager if proposing/defending, this.claimManagerForChallenges if challenging.
     * @throws InterruptedException
     */
    private void makeDeposit(SyscoinClaimManager myClaimManager, BigInteger weiValue) throws InterruptedException {
        CompletableFuture<TransactionReceipt> futureReceipt = myClaimManager.makeDeposit(weiValue).sendAsync();
        logger.info("Deposited {} wei.", weiValue);

        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                logger.info("makeClaimDeposit receipt {}", receipt.toString())
        );
        Thread.sleep(200); // in case the transaction takes some time to complete
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
    private BigInteger getChallengeDesposit() {
        return minProposalDeposit;
    }

    private BigInteger getDeposit(String account, SyscoinClaimManagerExtended myClaimManager) throws Exception {
        return myClaimManager.getDeposit(new org.web3j.abi.datatypes.Address(account)).send().getValue();
    }

    /**
     * Makes the minimum necessary deposit for reaching a given amount.
     * @param account Caller's address.
     * @param myClaimManager this.claimManager if proposing/defending, this.claimManagerForChallenges if challenging.
     * @param weiValue Deposit to be reached. This should be the caller's total deposit in the end.
     * @throws Exception
     */
    private void makeDepositIfNeeded(String account, SyscoinClaimManager myClaimManager, SyscoinClaimManagerExtended myClaimManagerGetter, BigInteger weiValue)
            throws Exception {
        BigInteger currentDeposit = getDeposit(account, myClaimManagerGetter);
        if (currentDeposit.compareTo(weiValue) < 0) {
            BigInteger diff = weiValue.subtract(currentDeposit);
            makeDeposit(myClaimManager, diff);
        }
    }

    private void withdrawDeposit(SyscoinClaimManager myClaimManager, BigInteger weiValue) {
        CompletableFuture<TransactionReceipt> futureReceipt = myClaimManager.withdrawDeposit(new Uint256(weiValue)).sendAsync();
        logger.info("Withdrew {} wei.", weiValue);
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                logger.info("withdrawDeposit receipt {}", receipt.toString())
        );
    }

    /**
     * Withdraw deposits so that only the maximum amount of funds (as determined by user configuration)
     * is left in the contract.
     * To be called after battles or when a superblock is approved/invalidated.
     * @param account Caller's address.
     * @param myClaimManager this.claimManager if proposing/defending, this.claimManagerForChallenges if challenging.
     * @throws Exception
     */
    private void withdrawAllFundsExceptLimit(String account, SyscoinClaimManager myClaimManager, SyscoinClaimManagerExtended myClaimManagerGetter) throws Exception {
        BigInteger currentDeposit = getDeposit(account, myClaimManagerGetter);
        BigInteger limit = BigInteger.valueOf(config.depositedFundsLimit());
        if (currentDeposit.compareTo(limit) > 0) {
            withdrawDeposit(myClaimManager, currentDeposit.subtract(limit));
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
        SyscoinClaimManager myClaimManager;
        SyscoinClaimManagerExtended myClaimManagerGetter;
        if (isChallenger) {
            myClaimManager = claimManagerForChallenges;
            myClaimManagerGetter = claimManagerForChallengesGetter;
        } else {
            myClaimManager = claimManager;
            myClaimManagerGetter = claimManagerGetter;
        }

        withdrawAllFundsExceptLimit(account, myClaimManager, myClaimManagerGetter);
    }



    /* ---- SUPERBLOCK STATUS CHECKS ---- */

    private BigInteger getSuperblockStatus(Keccak256Hash superblockId) throws Exception {
        return superblocksGetter.getSuperblockStatus(new Bytes32(superblockId.getBytes())).send().getValue();
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

    public BigInteger getSuperblockHeight(Keccak256Hash superblockId) throws Exception {
        return superblocksGetter.getSuperblockHeight(new Bytes32(superblockId.getBytes())).send().getValue();
    }
    public BigInteger getChainHeight() throws Exception {
        return superblocksGetter.getChainHeight().send().getValue();
    }

    /**
     * Listens to NewSuperblock events from SyscoinSuperblocks contract within a given block window
     * and parses web3j-generated instances into easier to manage SuperblockEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All NewSuperblock events from SyscoinSuperblocks as SuperblockEvent objects.
     * @throws IOException
     */
    public List<SuperblockEvent> getNewSuperblocks(long startBlock, long endBlock) throws IOException {
        List<SuperblockEvent> result = new ArrayList<>();
        List<SyscoinSuperblocks.NewSuperblockEventResponse> newSuperblockEvents =
                superblocksGetter.getNewSuperblockEvents(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (SyscoinSuperblocks.NewSuperblockEventResponse response : newSuperblockEvents) {
            SuperblockEvent newSuperblockEvent = new SuperblockEvent();
            newSuperblockEvent.superblockId = Keccak256Hash.wrap(response.superblockHash.getValue());
            newSuperblockEvent.who = response.who.getValue();
            result.add(newSuperblockEvent);
        }

        return result;
    }

    /**
     * Listens to ApprovedSuperblock events from SyscoinSuperblocks contract within a given block window
     * and parses web3j-generated instances into easier to manage SuperblockEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All ApprovedSuperblock events from SyscoinSuperblocks as SuperblockEvent objects.
     * @throws IOException
     */
    public List<SuperblockEvent> getApprovedSuperblocks(long startBlock, long endBlock)
            throws IOException {
        List<SuperblockEvent> result = new ArrayList<>();
        List<SyscoinSuperblocks.ApprovedSuperblockEventResponse> approvedSuperblockEvents =
                superblocksGetter.getApprovedSuperblockEvents(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (SyscoinSuperblocks.ApprovedSuperblockEventResponse response : approvedSuperblockEvents) {
            SuperblockEvent approvedSuperblockEvent = new SuperblockEvent();
            approvedSuperblockEvent.superblockId = Keccak256Hash.wrap(response.superblockHash.getValue());
            approvedSuperblockEvent.who = response.who.getValue();
            result.add(approvedSuperblockEvent);
        }

        return result;
    }

    /**
     * Listens to SemiApprovedSuperblock events from SyscoinSuperblocks contract within a given block window
     * and parses web3j-generated instances into easier to manage SuperblockEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All SemiApprovedSuperblock events from SyscoinSuperblocks as SuperblockEvent objects.
     * @throws IOException
     */
    public List<SuperblockEvent> getSemiApprovedSuperblocks(long startBlock, long endBlock)
            throws IOException {
        List<SuperblockEvent> result = new ArrayList<>();
        List<SyscoinSuperblocks.SemiApprovedSuperblockEventResponse> semiApprovedSuperblockEvents =
                superblocksGetter.getSemiApprovedSuperblockEvents(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (SyscoinSuperblocks.SemiApprovedSuperblockEventResponse response : semiApprovedSuperblockEvents) {
            SuperblockEvent semiApprovedSuperblockEvent = new SuperblockEvent();
            semiApprovedSuperblockEvent.superblockId = Keccak256Hash.wrap(response.superblockHash.getValue());
            semiApprovedSuperblockEvent.who = response.who.getValue();
            result.add(semiApprovedSuperblockEvent);
        }

        return result;
    }

    /**
     * Listens to InvalidSuperblock events from SyscoinSuperblocks contract within a given block window
     * and parses web3j-generated instances into easier to manage SuperblockEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All InvalidSuperblock events from SyscoinSuperblocks as SuperblockEvent objects.
     * @throws IOException
     */
    public List<SuperblockEvent> getInvalidSuperblocks(long startBlock, long endBlock)
            throws IOException {
        List<SuperblockEvent> result = new ArrayList<>();
        List<SyscoinSuperblocks.InvalidSuperblockEventResponse> invalidSuperblockEvents =
                superblocksGetter.getInvalidSuperblockEvents(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (SyscoinSuperblocks.InvalidSuperblockEventResponse response : invalidSuperblockEvents) {
            SuperblockEvent invalidSuperblockEvent = new SuperblockEvent();
            invalidSuperblockEvent.superblockId = Keccak256Hash.wrap(response.superblockHash.getValue());
            invalidSuperblockEvent.who = response.who.getValue();
            result.add(invalidSuperblockEvent);
        }

        return result;
    }

    public static class SuperblockEvent {

        public Keccak256Hash superblockId;
        public String who;
    }


    /* ---- GETTERS ---- */

    public BigInteger getSuperblockDuration() throws Exception {
        return battleManagerGetter.superblockDuration().send().getValue();
    }

    public BigInteger getSuperblockDelay() throws Exception {
        return claimManagerGetter.superblockDelay().send().getValue();
    }

    public BigInteger getSuperblockTimeout() throws Exception {
        return claimManagerGetter.superblockTimeout().send().getValue();
    }

    public Keccak256Hash getBestSuperblockId() throws Exception {
        return Keccak256Hash.wrap(superblocksGetter.getBestSuperblock().send().getValue());
    }

    /**
     * Looks up a superblock's submission time in SyscoinClaimManager.
     * @param superblockId Superblock hash.
     * @return When the superblock was submitted.
     * @throws Exception
     */
    public BigInteger getNewEventTimestampBigInteger(Keccak256Hash superblockId) throws Exception {
        return claimManagerGetter.getNewSuperblockEventTimestamp(new Bytes32(superblockId.getBytes())).send().getValue();
    }

    /**
     * Looks up a superblock's submission time in SyscoinClaimManager.
     * @param superblockId Superblock hash.
     * @return When the superblock was submitted.
     * @throws Exception
     */
    public Date getNewEventTimestampDate(Keccak256Hash superblockId) throws Exception {
        return new Date(getNewEventTimestampBigInteger(superblockId).longValue() * 1000);
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
     * @param account Caller's address.
     */
    public void confirmClaim(Keccak256Hash superblockId, Keccak256Hash descendantId, String account) {
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
     * @param account Caller's address.
     */
    public void rejectClaim(Keccak256Hash superblockId, String account) {
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


    /* ---- GETTERS ---- */

    public long getSuperblockConfirmations() throws Exception {
        return claimManagerGetter.superblockConfirmations().send().getValue().longValue();
    }

    public org.web3j.abi.datatypes.Address getClaimChallenger(Keccak256Hash superblockId) throws Exception {
        return new org.web3j.abi.datatypes.Address(claimManagerGetter.getClaimChallenger(new Bytes32(superblockId.getBytes())).send().getValue());
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
        if(!getSuperblockDuration().equals(listHashes.size()))
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
    public boolean challengeSuperblock(Keccak256Hash superblockId, String account) throws Exception {
        if(!getClaimExists(superblockId) || getClaimDecided(superblockId)) {
            logger.info("superblock has already been decided upon or claim doesn't exist, skipping...{}", superblockId.toString());
            return false;
        }
        if(getClaimSubmitter(superblockId).equals(getSyscoinSuperblockChallengerAddress())){
            logger.info("You cannot challenge a superblock you have submitted yourself, skipping...{}", superblockId.toString());
            return false;
        }

        // Make necessary deposit to cover reward
        makeDepositIfNeeded(account, claimManagerForChallenges, claimManagerForChallengesGetter, getChallengeDesposit());

        CompletableFuture<TransactionReceipt> futureReceipt =
                claimManagerForChallenges.challengeSuperblock(new Bytes32(superblockId.getBytes())).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                logger.info("challengeSuperblock receipt {}", receipt.toString()));
        return true;
    }



    /* ---- GETTERS ---- */

    public boolean getClaimExists(Keccak256Hash superblockId) throws Exception {
        return claimManagerGetter.getClaimExists(new Bytes32(superblockId.getBytes())).send().getValue();
    }

    public String getClaimSubmitter(Keccak256Hash superblockId) throws Exception {
        return claimManagerGetter.getClaimSubmitter(new Bytes32(superblockId.getBytes())).send().getValue();
    }

    public boolean getClaimDecided(Keccak256Hash superblockId) throws Exception {
        return claimManagerGetter.getClaimDecided(new Bytes32(superblockId.getBytes())).send().getValue();
    }

    public boolean getClaimInvalid(Keccak256Hash superblockId) throws Exception {
        return claimManagerGetter.getClaimInvalid(new Bytes32(superblockId.getBytes())).send().getValue();
    }



    public boolean getInBattleAndSemiApprovable(Keccak256Hash superblockId) throws Exception {
        return claimManagerGetter.getInBattleAndSemiApprovable(new Bytes32(superblockId.getBytes())).send().getValue();
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
                getSuperblockConfirmations() &&
                isSuperblockSemiApproved(descendantId) &&
                isSuperblockSemiApproved(superblockId));
    }
    private Date getTimeoutDate() throws Exception {
        int superblockTimeout = getSuperblockTimeout().intValue();
        return SuperblockUtils.getNSecondsAgo(superblockTimeout);
    }
    private boolean submittedTimeoutPassed(Keccak256Hash superblockId) throws Exception {
        return getNewEventTimestampDate(superblockId).before(getTimeoutDate());
    }
    public boolean newAndTimeoutPassed(Keccak256Hash superblockId) throws Exception {
        return (isSuperblockNew(superblockId) && submittedTimeoutPassed(superblockId));
    }

    public boolean getSubmitterHitTimeout(Keccak256Hash sessionId) throws Exception {
        return battleManagerForChallengesGetter.getSubmitterHitTimeout(new Bytes32(sessionId.getBytes())).send().getValue();
    }

    public int getNumMerkleHashesBySession(Keccak256Hash sessionId) throws Exception {
        BigInteger ret = battleManagerGetter.getNumMerkleHashesBySession(new Bytes32(sessionId.getBytes())).send().getValue();
        return ret.intValue();
    }

    public ChallengeState getSessionChallengeState(Keccak256Hash sessionId) throws Exception {
        BigInteger ret = battleManagerGetter.getSessionChallengeState(new Bytes32(sessionId.getBytes())).send().getValue();
        return ChallengeState.values()[ret.intValue()];
    }

    public int getRandomizationCounter(){
        return randomizationCounter;
    }
    public void setRandomizationCounter(){
        double randomDouble = Math.random();
        randomDouble = randomDouble * 100 + 1;
        if(randomDouble < 10)
            randomDouble = 10;
        randomizationCounter = (int)randomDouble;
    }



    /* ---------------------------------- */
    /* ----- Relay Syscoin tx section ------ */
    /* ---------------------------------- */

    private class SPVProof {
        public int index;
        List<String> merklePath;
        String superBlock;
        public SPVProof(int indexIn, List<String> merklePathIn, String superBlockIn) {
            this.index = indexIn;
            this.merklePath = merklePathIn;
            this.superBlock = superBlockIn;
        }
    }
    /**
     * Returns an SPV Proof to the superblock for a Syscoin transaction to Sysethereum contracts.
     * @param block Syscoin block that the transaction is in.
     * @param superblockPMT Partial Merkle tree for constructing an SPV proof
     *                      of the Syscoin block's existence in the superblock.
     * @throws Exception
     */
    public String getSuperblockSPVProof( AltcoinBlock block,
                            Superblock superblock, SuperblockPartialMerkleTree superblockPMT)
            throws Exception {
        Sha256Hash syscoinBlockHash = block.getHash();

        // Construct SPV proof for block
        int syscoinBlockIndex = superblockPMT.getTransactionIndex(syscoinBlockHash);
        List<Sha256Hash> syscoinBlockSiblingsSha256Hash = superblockPMT.getTransactionPath(syscoinBlockHash);
        List<String> syscoinBlockSiblingsBigInteger = new ArrayList<>();
        for (Sha256Hash sha256Hash : syscoinBlockSiblingsSha256Hash)
            syscoinBlockSiblingsBigInteger.add(sha256Hash.toString());

        SPVProof spvProof = new SPVProof(syscoinBlockIndex, syscoinBlockSiblingsBigInteger, superblock.getSuperblockId().toString());
        Gson g = new Gson();
        return g.toJson(spvProof);

    }



}