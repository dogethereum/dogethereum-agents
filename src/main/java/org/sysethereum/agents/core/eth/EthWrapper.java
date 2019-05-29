package org.sysethereum.agents.core.eth;


import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.sysethereum.agents.constants.SystemProperties;
import org.sysethereum.agents.contract.*;
import org.sysethereum.agents.core.SuperblockChallengerClient;
import org.sysethereum.agents.core.syscoin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.ipc.UnixIpcService;
import org.web3j.tx.ClientTransactionManager;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private static final Logger log = LoggerFactory.getLogger("LocalAgentConstants");
    private Web3j web3;
    private Web3j web3Secondary;

    // Extensions of contracts generated automatically by web3j
    private SyscoinClaimManagerExtended claimManager;
    private SyscoinClaimManagerExtended claimManagerGetter;
    private SyscoinClaimManagerExtended claimManagerForChallenges;
    private SyscoinClaimManagerExtended claimManagerForChallengesGetter;
    private SyscoinBattleManagerExtended battleManager;
    private SyscoinBattleManagerExtended battleManagerForChallenges;
    private SyscoinBattleManagerExtended battleManagerGetter;
    private SyscoinBattleManagerExtended battleManagerForChallengesGetter;
    private SyscoinSuperblocksExtended superblocks;
    private SyscoinSuperblocksExtended superblocksGetter;

    private SystemProperties config;
    private BigInteger gasPriceMinimum;

    private String generalPurposeAndSendSuperblocksAddress;
    private String syscoinSuperblockChallengerAddress;

    private BigInteger minProposalDeposit;
    private BigInteger minChallengeDeposit;
    private BigInteger respondMerkleRootHashesCost;
    private BigInteger respondBlockHeaderCost;
    private BigInteger verifySuperblockCost;
    @Autowired
    private SuperblockChain superblockChain;

    private int randomizationCounter;

    /* ---------------------------------- */
    /* ------ General code section ------ */
    /* ---------------------------------- */

    @Autowired
    public EthWrapper() throws Exception {
        setRandomizationCounter();
        config = SystemProperties.CONFIG;
        String path = config.dataDirectory() + "/geth/geth.ipc";
        String secondaryURL = config.secondaryURL();
        
        web3Secondary = Web3j.build(new HttpService(secondaryURL));
        Admin admin = Admin.build(new UnixIpcService(path));
        String generalAddress = config.generalPurposeAndSendSuperblocksAddress();
        if(generalAddress.length() > 0){
            PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount(generalAddress, config.generalPurposeAndSendSuperblocksUnlockPW(), BigInteger.ZERO).send();
            if (personalUnlockAccount.accountUnlocked()) {
                log.info("general.purpose.and.send.superblocks.address is unlocked and ready to use!");
            }
            else{
                log.warn("general.purpose.and.send.superblocks.address could not be unlocked, please check the password you set in the configuration file");
            }
        }
        String challengerAddress = config.syscoinSuperblockChallengerAddress();
        if(challengerAddress.length() > 0 && !generalAddress.equals(challengerAddress)){
            PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount(challengerAddress, config.syscoinSuperblockChallengerUnlockPW(), BigInteger.ZERO).send();
            if (personalUnlockAccount.accountUnlocked()) {
                log.info("syscoin.superblock.challenger.address is unlocked and ready to use!");
            }
            else{
                log.warn("syscoin.superblock.challenger.address could not be unlocked, please check the password you set in the configuration file");
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
        minChallengeDeposit = claimManagerGetter.minChallengeDeposit().send().getValue();
        respondMerkleRootHashesCost = claimManagerGetter.respondMerkleRootHashesCost().send().getValue();
        respondBlockHeaderCost = claimManagerGetter.respondBlockHeaderCost().send().getValue();
        verifySuperblockCost = claimManagerGetter.verifySuperblockCost().send().getValue();
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

    public boolean arePendingTransactionsForChallengerAddress() throws IOException {
        return arePendingTransactionsFor(syscoinSuperblockChallengerAddress);
    }

    /**
     * Checks if there are pending transactions for a given contract.
     * @param address
     * @return
     * @throws IOException
     */
    private boolean arePendingTransactionsFor(String address) throws IOException {
        BigInteger latest = web3Secondary.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send().getTransactionCount();
        BigInteger pending = BigInteger.ZERO;
        try{
            pending = web3.ethGetTransactionCount(address, DefaultBlockParameterName.PENDING).send().getTransactionCount();
        }
        catch(Exception e){
            // suppress the throw but put it in pending status so dependent code waits
            return true;
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
            gasPriceMinimum = gasPriceSuggestedByEthNode;
            log.info("setting new min gas price to " + gasPriceMinimum);
            if(claimManager != null)
                claimManager.setGasPrice(gasPriceMinimum);
            if(claimManagerForChallenges != null)
                claimManagerForChallenges.setGasPrice(gasPriceMinimum);
            if(superblocks != null)
                superblocks.setGasPrice(gasPriceMinimum);
            if(battleManager != null)
                battleManager.setGasPrice(gasPriceMinimum);
        }
    }

    public String getGeneralPurposeAndSendSuperblocksAddress() {
        return generalPurposeAndSendSuperblocksAddress;
    }

    public String getSyscoinSuperblockChallengerAddress() {
        return syscoinSuperblockChallengerAddress;
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

    public SyscoinClaimManagerExtended getClaimManager() {
        return claimManager;
    }

    public SyscoinClaimManagerExtended getClaimManagerForChallenges() {
        return claimManagerForChallenges;
    }

    public SyscoinBattleManagerExtended getBattleManager() {
        return battleManager;
    }

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
    public Keccak256Hash getLatestSuperblock(boolean bAllowSemiApprovedOrConfirmed) throws Exception{
        BigInteger superblockIndex = this.getIndexNextSuperblock();
        Superblock latestSuperblock = superblockChain.getSuperblockByHeight(superblockIndex.longValue()-1);
        if(latestSuperblock == null)
            return null;
        Keccak256Hash latestSuperblockId = latestSuperblock.getSuperblockId();
        // we are about semi approved in some cases where a semi approver goes offline and we have a similar chain so we can keep building on it instead of waiting
        if(isSuperblockNew(latestSuperblockId) || (bAllowSemiApprovedOrConfirmed && (isSuperblockSemiApproved(latestSuperblockId) || isSuperblockApproved(latestSuperblockId))))
            return latestSuperblockId;
        return null;
    }
    public Keccak256Hash getSuperblockParentId(Keccak256Hash superBlockId) {
        try {
            return Keccak256Hash.wrap(superblocksGetter.getSuperblockParentId(new Bytes32(superBlockId.getBytes())).send().getValue());
        }
        catch(Exception e){
            return null;
        }
    }
    /**
     * Proposes a superblock to SyscoinClaimManager in order to keep the Sysethereum contracts updated.
     * @param superblock Oldest superblock that is already stored in the local database,
     *                   but still hasn't been submitted to Sysethereum Contracts.
     * @throws Exception If superblock hash cannot be calculated.
     */
    public boolean  sendStoreSuperblock(Superblock superblock, String account) throws Exception {


        // Check if the parent has been approved before sending this superblock.
        Keccak256Hash parentId = superblock.getParentId();
        if (!(isSuperblockApproved(parentId) || isSuperblockSemiApproved(parentId))) {
            log.info("Superblock {} not sent because its parent was neither approved nor semi approved.",
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
                else if(isSuperblockSemiApproved(parentId)){
                    allowed = true;
                }
                else{
                    allowed = false;
                }
            }
           if(!allowed){
               log.info("Superblock {} has already been sent. Returning.", superblock.getSuperblockId());
               return false;
            }
        }


        log.info("About to send superblock {} to the bridge.", superblock.getSuperblockId());

        // Make any necessary deposits for sending the superblock
        makeDepositIfNeeded(account, claimManager, claimManagerGetter, getSuperblockDeposit(superblock.getSyscoinBlockHashes().size()));


        // The parent is either approved or semi approved. We can send the superblock.
        CompletableFuture<TransactionReceipt> futureReceipt = proposeSuperblock(superblock);
        log.info("Sent superblock {}", superblock.getSuperblockId());
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("proposeSuperblock receipt {}", receipt.toString())
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
                new Uint256(superblock.getpreviousSyscoinBlockTime()),
                new Bytes32(superblock.getLastSyscoinBlockHash().getBytes()),
                new Uint32(superblock.getpreviousSyscoinBlockBits()),
                new Bytes32(superblock.getParentId().getBytes()),
                new Uint32(superblock.getBlockHeight())).sendAsync();
    }


    public boolean wasSuperblockAlreadySubmitted(Keccak256Hash superblockId) throws Exception {
        return !superblocksGetter.getSuperblockIndex(new Bytes32(superblockId.getBytes())).send().equals(new Uint32(BigInteger.ZERO));
    }

    /**
     * Makes a deposit.
     * @param weiValue Wei to be deposited.
     * @param myClaimManager this.claimManager if proposing/defending, this.claimManagerForChallenges if challenging.
     * @throws InterruptedException
     */
    private void makeDeposit(SyscoinClaimManager myClaimManager, BigInteger weiValue) throws InterruptedException {
        CompletableFuture<TransactionReceipt> futureReceipt = myClaimManager.makeDeposit(weiValue).sendAsync();
        log.info("Deposited {} wei.", weiValue);

        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("makeClaimDeposit receipt {}", receipt.toString())
        );
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
        result = result.add(BigInteger.valueOf(nHashes+1).multiply(respondBlockHeaderCost));
        return result.add(respondMerkleRootHashesCost);
    }

    /**
     * Returns the initial deposit for challenging a superblock, just a best guess based on
     * 60 requests max for block headers and the final verify superblock cost
     * @return Initial deposit for covering single battle during a challenge.
     * @throws Exception
     */
    private BigInteger getChallengeDesposit() throws Exception {
        BigInteger result = minChallengeDeposit;
        return result.add(respondMerkleRootHashesCost).add(verifySuperblockCost);
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

    private void withdrawDeposit(SyscoinClaimManager myClaimManager, BigInteger weiValue) throws Exception {
        CompletableFuture<TransactionReceipt> futureReceipt = myClaimManager.withdrawDeposit(new Uint256(weiValue)).sendAsync();
        log.info("Withdrew {} wei.", weiValue);
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("withdrawDeposit receipt {}", receipt.toString())
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

    /**
     * Marks a superblock as invalid.
     * @param superblockId Superblock to be invalidated.
     * @param validator
     * @throws Exception
     */
    public void invalidate(Keccak256Hash superblockId, String validator) throws Exception {
        CompletableFuture<TransactionReceipt> futureReceipt =
                superblocks.invalidate(new Bytes32(superblockId.getBytes()), new org.web3j.abi.datatypes.Address(validator)).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("Invalidated superblock {}", superblockId));
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
    private BigInteger getIndexNextSuperblock() throws Exception {
        return superblocksGetter.getIndexNextSuperblock().send().getValue();
    }
    public BigInteger getSuperblockHeight(Keccak256Hash superblockId) throws Exception {
        return superblocksGetter.getSuperblockHeight(new Bytes32(superblockId.getBytes())).send().getValue();
    }
    public byte[] getSuperblockAt(Uint256 height) throws Exception {
        return superblocksGetter.getSuperblockAt(height).send().getValue();
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


    /* ---- LOG PROCESSING METHODS ---- */

    public BigInteger getEthTimestampRaw(Log eventLog) throws InterruptedException, ExecutionException {
        String ethBlockHash = eventLog.getBlockHash();
        CompletableFuture<EthBlock> ethBlockCompletableFuture =
                web3Secondary.ethGetBlockByHash(ethBlockHash, true).sendAsync();
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
        return battleManagerGetter.superblockDuration().send().getValue();
    }

    public BigInteger getSuperblockDelay() throws Exception {
        return claimManagerGetter.superblockDelay().send().getValue();
    }

    public BigInteger getSuperblockTimeout() throws Exception {
        return claimManagerGetter.superblockTimeout().send().getValue();
    }

    public BigInteger getBattleReward() throws Exception {
        return claimManagerGetter.battleReward().send().getValue();
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
    public void checkClaimFinished(Keccak256Hash superblockId, boolean isChallenger)
            throws Exception {
        SyscoinClaimManagerExtended myClaimManager;
        if (isChallenger) {
            myClaimManager = claimManagerForChallenges;
        } else {
            myClaimManager = claimManager;
        }

        CompletableFuture<TransactionReceipt> futureReceipt =
                myClaimManager.checkClaimFinished(new Bytes32(superblockId.getBytes())).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("checkClaimFinished receipt {}", receipt.toString())
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
    public void confirmClaim(Keccak256Hash superblockId, Keccak256Hash descendantId, String account) throws Exception {
        CompletableFuture<TransactionReceipt> futureReceipt =
                claimManager.confirmClaim(new Bytes32(superblockId.getBytes()), new Bytes32(descendantId.getBytes())).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("confirmClaim receipt {}", receipt.toString())
        );
    }

    /**
     * Rejects a claim.
     * See SyscoinClaimManager source code for further reference.
     * @param superblockId ID of superblock to be rejected.
     * @param account Caller's address.
     * @throws Exception
     */
    public void rejectClaim(Keccak256Hash superblockId, String account) throws Exception {
        CompletableFuture<TransactionReceipt> futureReceipt =
                claimManager.rejectClaim(new Bytes32(superblockId.getBytes())).sendAsync();
        futureReceipt.thenAcceptAsync( (TransactionReceipt receipt) ->
                log.info("rejectClaim receipt {}", receipt.toString())
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
            newBattleEvent.superblockId = Keccak256Hash.wrap(response.superblockHash.getValue());
            newBattleEvent.sessionId = Keccak256Hash.wrap(response.sessionId.getValue());
            newBattleEvent.submitter = response.submitter.getValue();
            newBattleEvent.challenger = response.challenger.getValue();
            result.add(newBattleEvent);
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
            challengerConvictedEvent.superblockId = Keccak256Hash.wrap(response.superblockHash.getValue());
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
            submitterConvictedEvent.superblockId = Keccak256Hash.wrap(response.superblockHash.getValue());
            submitterConvictedEvent.sessionId = Keccak256Hash.wrap(response.sessionId.getValue());
            submitterConvictedEvent.submitter = response.submitter.getValue();
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
     * Listens to QueryBlockHeader events from SyscoinBattleManager contract within a given block window
     * and parses web3j-generated instances into easier to manage QueryBlockHeaderEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All QueryBlockHeader events from SyscoinBattleManager as QueryBlockHeaderEvent objects.
     * @throws IOException
     */
    public List<QueryBlockHeaderEvent> getBlockHeaderQueries(long startBlock, long endBlock)
            throws IOException {
        List<QueryBlockHeaderEvent> result = new ArrayList<>();
        List<SyscoinBattleManager.QueryBlockHeaderEventResponse> queryBlockHeaderEvents =
                battleManagerGetter.getQueryBlockHeaderEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (SyscoinBattleManager.QueryBlockHeaderEventResponse response : queryBlockHeaderEvents) {
            QueryBlockHeaderEvent queryBlockHeaderEvent = new QueryBlockHeaderEvent();
            queryBlockHeaderEvent.superblockId = Keccak256Hash.wrap(response.superblockHash.getValue());
            queryBlockHeaderEvent.sessionId = Keccak256Hash.wrap(response.sessionId.getValue());
            queryBlockHeaderEvent.submitter = response.submitter.getValue();
            queryBlockHeaderEvent.syscoinBlockHash = Sha256Hash.wrap(response.blockSha256Hash.getValue());
            result.add(queryBlockHeaderEvent);
        }

        return result;
    }

    /**
     * Listens to QueryMerkleRootHashes events from SyscoinBattleManager contract within a given block window
     * and parses web3j-generated instances into easier to manage QueryMerkleRootHashesEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All QueryMerkleRootHashes events from SyscoinBattleManager as QueryMerkleRootHashesEvent objects.
     * @throws IOException
     */
    public List<QueryMerkleRootHashesEvent> getMerkleRootHashesQueries(long startBlock, long endBlock)
            throws IOException {
        List<QueryMerkleRootHashesEvent> result = new ArrayList<>();
        List<SyscoinBattleManager.QueryMerkleRootHashesEventResponse> queryMerkleRootHashesEvents =
                battleManagerGetter.getQueryMerkleRootHashesEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (SyscoinBattleManager.QueryMerkleRootHashesEventResponse response : queryMerkleRootHashesEvents) {
            QueryMerkleRootHashesEvent queryMerkleRootHashesEvent = new QueryMerkleRootHashesEvent();
            queryMerkleRootHashesEvent.superblockId = Keccak256Hash.wrap(response.superblockHash.getValue());
            queryMerkleRootHashesEvent.sessionId = Keccak256Hash.wrap(response.sessionId.getValue());
            queryMerkleRootHashesEvent.submitter = response.submitter.getValue();
            result.add(queryMerkleRootHashesEvent);
        }

        return result;
    }

    // Event wrapper classes

    public static class QueryBlockHeaderEvent {
        public Keccak256Hash superblockId;
        public Keccak256Hash sessionId;
        public String submitter;
        public Sha256Hash syscoinBlockHash;
    }

    public static class QueryMerkleRootHashesEvent {
        public Keccak256Hash superblockId;
        public Keccak256Hash sessionId;
        public String submitter;
    }

    /**
     * Listens to RespondMerkleRootHashes events from SyscoinBattleManager contract within a given block window
     * and parses web3j-generated instances into easier to manage RespondMerkleRootHashesEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All RespondMerkleRootHashes events from SyscoinBattleManager as RespondMerkleRootHashesEvent objects.
     * @throws IOException
     */
    public List<RespondMerkleRootHashesEvent> getRespondMerkleRootHashesEvents(long startBlock, long endBlock)
            throws IOException {
        List<RespondMerkleRootHashesEvent> result = new ArrayList<>();
        List<SyscoinBattleManager.RespondMerkleRootHashesEventResponse> respondMerkleRootHashesEvents =
                battleManagerForChallengesGetter.getRespondMerkleRootHashesEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (SyscoinBattleManager.RespondMerkleRootHashesEventResponse response : respondMerkleRootHashesEvents) {
            RespondMerkleRootHashesEvent respondMerkleRootHashesEvent = new RespondMerkleRootHashesEvent();
            respondMerkleRootHashesEvent.superblockId = Keccak256Hash.wrap(response.superblockHash.getValue());
            respondMerkleRootHashesEvent.sessionId = Keccak256Hash.wrap(response.sessionId.getValue());
            respondMerkleRootHashesEvent.challenger = response.challenger.getValue();
            respondMerkleRootHashesEvent.blockHashes = new ArrayList<Sha256Hash>();
            for (Bytes32 rawSyscoinBlockHash : response.blockHashes.getValue()) {
                respondMerkleRootHashesEvent.blockHashes.add(Sha256Hash.wrap(rawSyscoinBlockHash.getValue()));
            }
            result.add(respondMerkleRootHashesEvent);
        }

        return result;
    }

    /**
     * Listens to RespondBlockHeader events from SyscoinBattleManager contract within a given block window
     * and parses web3j-generated instances into easier to manage RespondBlockHeaderEvent objects.
     * @param startBlock First Ethereum block to poll.
     * @param endBlock Last Ethereum block to poll.
     * @return All RespondBlockHeader events from SyscoinBattleManager as RespondBlockHeaderEvent objects.
     * @throws IOException
     */
    public List<RespondBlockHeaderEvent> getRespondBlockHeaderEvents(long startBlock, long endBlock)
            throws IOException {
        List<RespondBlockHeaderEvent> result = new ArrayList<>();
        List<SyscoinBattleManager.RespondBlockHeaderEventResponse> respondBlockHeaderEvents =
                battleManagerForChallengesGetter.getRespondBlockHeaderEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (SyscoinBattleManager.RespondBlockHeaderEventResponse response : respondBlockHeaderEvents) {
            RespondBlockHeaderEvent respondBlockHeaderEvent = new RespondBlockHeaderEvent();
            respondBlockHeaderEvent.superblockId = Keccak256Hash.wrap(response.superblockHash.getValue());
            respondBlockHeaderEvent.sessionId = Keccak256Hash.wrap(response.sessionId.getValue());
            respondBlockHeaderEvent.challenger = response.challenger.getValue();
            respondBlockHeaderEvent.blockHeader = response.blockHeader.getValue();
            respondBlockHeaderEvent.powBlockHeader = response.powBlockHeader.getValue();
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
        List<SyscoinBattleManager.ErrorBattleEventResponse> errorBattleEvents =
                battleManagerGetter.getErrorBattleEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (SyscoinBattleManager.ErrorBattleEventResponse response : errorBattleEvents) {
            ErrorBattleEvent errorBattleEvent = new ErrorBattleEvent();
            errorBattleEvent.sessionId = Keccak256Hash.wrap(response.sessionId.getValue());
            errorBattleEvent.err = response.err.getValue();
            result.add(errorBattleEvent);
        }

        return result;
    }

    public static class ErrorBattleEvent {
        public Keccak256Hash sessionId;
        public BigInteger err;
    }





    /* ---- GETTERS ---- */

    public long getSuperblockConfirmations() throws Exception {
        return claimManagerGetter.superblockConfirmations().send().getValue().longValue();
    }

    // TODO: see if this is necessary later
    public long getSuperblockConfirmationsForChallenges() throws Exception {
        return claimManagerForChallenges.superblockConfirmations().send().getValue().longValue();
    }


    /* ---------------------------------- */
    /* --------- Battle section --------- */
    /* ---------------------------------- */

    /**
     * Responds to a Syscoin block header query.
     * @param superblockId Hash of the superblock that the Syscoin block hash is supposedly in.
     * @param sessionId Battle session ID.
     * @param syscoinBlock Syscoin block whose header was requested.
     * @param account Caller's address.
     */
    public void respondBlockHeader(Keccak256Hash superblockId, Keccak256Hash sessionId,
                                   AltcoinBlock syscoinBlock, String account) throws Exception {
        makeDepositIfNeeded(account, claimManager, claimManagerGetter, respondBlockHeaderCost);
        byte[] blockHeaderBytes = syscoinBlock.bitcoinSerialize();
        CompletableFuture<TransactionReceipt> futureReceipt = battleManager.respondBlockHeader(
                new Bytes32(superblockId.getBytes()), new Bytes32(sessionId.getBytes()), new DynamicBytes(blockHeaderBytes)).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("Responded to block header query for Syscoin block {}, session {}, superblock {}. Receipt: {}",
                        syscoinBlock.getHash(), sessionId, superblockId, receipt)
        );
    }

    /**
     * Responds to a Merkle root hashes query.
     * @param superblockId Hash of the superblock whose Merkle root hashes were requested.
     * @param sessionId Battle session ID.
     * @param syscoinBlockHashes Syscoin block hashes that are supposedly in the superblock.
     * @param account Caller's address.
     */
    public void respondMerkleRootHashes(Keccak256Hash superblockId, Keccak256Hash sessionId,
                                        List<Sha256Hash> syscoinBlockHashes, String account)
            throws Exception {
        List<Bytes32> rawHashes = new ArrayList<>();
        makeDepositIfNeeded(account, claimManager, claimManagerGetter, verifySuperblockCost.add(respondBlockHeaderCost.multiply(BigInteger.valueOf(syscoinBlockHashes.size()+1))));
        for (Sha256Hash syscoinBlockHash : syscoinBlockHashes)
            rawHashes.add(new Bytes32(syscoinBlockHash.getBytes()));
        CompletableFuture<TransactionReceipt> futureReceipt =
                battleManager.respondMerkleRootHashes(new Bytes32(superblockId.getBytes()), new Bytes32(sessionId.getBytes()), new DynamicArray<Bytes32>(rawHashes)).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("Responded to Merkle root hashes query for session {}, superblock {}. Receipt: {}",
                        sessionId, superblockId, receipt.toString()));
    }

    /**
     * Requests the header of a Syscoin block in a certain superblock.
     * @param superblockId Hash of the superblock that the Syscoin block hash is supposedly in.
     * @param sessionId Battle session ID.
     * @param syscoinBlockHash Hash of the Syscoin block whose header is being queried.
     * @param account Caller's address.
     * */
    public void queryBlockHeader(Keccak256Hash superblockId, Keccak256Hash sessionId,
                                 Sha256Hash syscoinBlockHash, String account) throws Exception {
        makeDepositIfNeeded(account, claimManagerForChallenges, claimManagerForChallengesGetter, respondBlockHeaderCost);
        CompletableFuture<TransactionReceipt> futureReceipt =
                battleManagerForChallenges.queryBlockHeader(new Bytes32(superblockId.getBytes()),
                new Bytes32(sessionId.getBytes()), new Bytes32(syscoinBlockHash.getBytes())).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("Requested Syscoin block header for block {}, superblock {}", syscoinBlockHash, superblockId));
    }

    // TODO: see if the challenger should know which superblock this is

    /**
     * Verifies a challenged superblock once the battle is done with.
     * @param sessionId Battle session ID.
     * @param myBattleManager SyscoinBattleManager contract that the caller is using to handle its battles.
     * @throws Exception
     */
    public void verifySuperblock(Keccak256Hash sessionId, SyscoinBattleManagerExtended myBattleManager) throws Exception {
        CompletableFuture<TransactionReceipt> futureReceipt =
                myBattleManager.verifySuperblock(new Bytes32(sessionId.getBytes())).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("Verified superblock for session {}", sessionId));
    }

    /**
     * Calls timeout for a session where a participant hasn't responded in time, thus closing the battle.
     * @param sessionId Battle session ID.
     * @param myBattleManager SyscoinBattleManager contract that the caller is using to handle its battles.
     * @throws Exception
     */
    public void timeout(Keccak256Hash sessionId, SyscoinBattleManagerExtended myBattleManager) throws Exception {
        CompletableFuture<TransactionReceipt> futureReceipt = myBattleManager.timeout(new Bytes32(sessionId.getBytes())).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("Called timeout for session {}", sessionId));
    }


    /* ---- CHALLENGER ---- */

    /**
     * Challenges a superblock.
     * @param superblockId Hash of superblock to be challenged.
     * @param account Caller's address.
     * @throws InterruptedException
     */
    public boolean challengeSuperblock(Keccak256Hash superblockId, String account)
            throws InterruptedException, Exception {
        if(!getClaimExists(superblockId) || getClaimDecided(superblockId)) {
            log.info("superblock has already been decided upon or claim doesn't exist, skipping...{}", superblockId.toString());
            return false;
        }
        if(getClaimSubmitter(superblockId).equals(getSyscoinSuperblockChallengerAddress())){
            log.info("You cannot challenge a superblock you have submitted yourself, skipping...{}", superblockId.toString());
            return false;
        }

        // Make necessary deposit to cover reward
        makeDepositIfNeeded(account, claimManagerForChallenges, claimManagerForChallengesGetter, getChallengeDesposit());

        CompletableFuture<TransactionReceipt> futureReceipt =
                claimManagerForChallenges.challengeSuperblock(new Bytes32(superblockId.getBytes())).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("challengeSuperblock receipt {}", receipt.toString()));
        return true;
    }

    /**
     * Makes a deposit for challenging a superblock.
     * @param weiValue Wei to be deposited.
     * @throws InterruptedException
     */
    private void makeChallengerDeposit(BigInteger weiValue)
            throws InterruptedException {
        CompletableFuture<TransactionReceipt> futureReceipt = claimManagerForChallenges.makeDeposit(weiValue).sendAsync();
        log.info("Challenger deposited {} wei.", weiValue);

        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("makeChallengerDeposit receipt {}", receipt.toString())
        );
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
        makeDepositIfNeeded(account, claimManagerForChallenges, claimManagerForChallengesGetter, respondMerkleRootHashesCost);
        CompletableFuture<TransactionReceipt> futureReceipt = battleManagerForChallenges.queryMerkleRootHashes(
                new Bytes32(superblockId.getBytes()), new Bytes32(sessionId.getBytes())).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                log.info("queryMerkleRootHashes receipt {}", receipt.toString()));
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

    public boolean getClaimVerificationOngoing(Keccak256Hash superblockId) throws Exception {
        return claimManagerGetter.getClaimVerificationOngoing(new Bytes32(superblockId.getBytes())).send().getValue();
    }

    public BigInteger getClaimChallengeTimeoutBigInteger(Keccak256Hash superblockId) throws Exception {
        return claimManagerGetter.getClaimChallengeTimeout(new Bytes32(superblockId.getBytes())).send().getValue();
    }

    public Date getClaimChallengeTimeoutDate(Keccak256Hash superblockId) throws Exception {
        return new Date(getClaimChallengeTimeoutBigInteger(superblockId).longValue() * 1000);
    }

    public int getClaimRemainingChallengers(Keccak256Hash superblockId) throws Exception {
        return claimManagerGetter.getClaimRemainingChallengers(new Bytes32(superblockId.getBytes())).send().getValue().intValue();
    }

    public boolean getInBattleAndSemiApprovable(Keccak256Hash superblockId) throws Exception {
        return claimManagerGetter.getInBattleAndSemiApprovable(new Bytes32(superblockId.getBytes())).send().getValue();
    }

    public List<org.web3j.abi.datatypes.Address> getClaimChallengers(Keccak256Hash superblockId) throws Exception {
        return claimManagerGetter.getClaimChallengers(new Bytes32(superblockId.getBytes())).send().getValue();
    }

    public boolean getChallengerHitTimeout(Keccak256Hash sessionId) throws Exception {
        return battleManagerGetter.getChallengerHitTimeout(new Bytes32(sessionId.getBytes())).send().getValue();
    }

    public boolean getSubmitterHitTimeout(Keccak256Hash sessionId) throws Exception {
        return battleManagerForChallengesGetter.getSubmitterHitTimeout(new Bytes32(sessionId.getBytes())).send().getValue();
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
    public List<Sha256Hash> getSyscoinBlockHashes(Keccak256Hash sessionId) throws Exception {
        List<Sha256Hash> result = new ArrayList<>();
        List<Bytes32> rawHashes = battleManagerGetter.getSyscoinBlockHashes(new Bytes32(sessionId.getBytes())).send().getValue();
        for (Bytes32 rawHash : rawHashes)
            result.add(Sha256Hash.wrap(rawHash.getValue())); // TODO: check endianness
        return result;
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