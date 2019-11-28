package org.sysethereum.agents.core.bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.sysethereum.agents.constants.AgentRole;
import org.sysethereum.agents.constants.EthAddresses;
import org.sysethereum.agents.constants.SystemProperties;
import org.sysethereum.agents.contract.SyscoinClaimManager;
import org.sysethereum.agents.contract.SyscoinClaimManagerExtended;
import org.sysethereum.agents.core.bridge.battle.SuperblockFailedEvent;
import org.sysethereum.agents.core.bridge.battle.SuperblockSuccessfulEvent;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;
import org.sysethereum.agents.core.syscoin.SuperblockUtils;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toList;
import static org.sysethereum.agents.constants.AgentRole.CHALLENGER;
import static org.sysethereum.agents.constants.AgentRole.SUBMITTER;

@Service
public class ClaimContractApi {

    private static final Logger logger = LoggerFactory.getLogger("ClaimContractApi");

    private final SystemProperties config;
    private final EthAddresses ethAddresses;
    private final BigInteger minProposalDeposit;
    private final BigInteger superblockTimeout;
    private final SyscoinClaimManagerExtended claimManager;
    private final SyscoinClaimManagerExtended claimManagerForChallenges;

    public ClaimContractApi(
            SystemProperties config,
            EthAddresses ethAddresses,
            BigInteger minProposalDeposit,
            BigInteger superblockTimeout,
            SyscoinClaimManagerExtended claimManager,
            SyscoinClaimManagerExtended claimManagerForChallenges
    ) {
        this.config = config;
        this.ethAddresses = ethAddresses;
        this.minProposalDeposit = minProposalDeposit;
        this.superblockTimeout = superblockTimeout;
        this.claimManager = claimManager;
        this.claimManagerForChallenges = claimManagerForChallenges;
    }

    public void updateGasPrice(BigInteger gasPriceMinimum) {
        //noinspection deprecation
        claimManager.setGasPrice(gasPriceMinimum);
        //noinspection deprecation
        claimManagerForChallenges.setGasPrice(gasPriceMinimum);
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

    public boolean submittedTimeoutPassed(Keccak256Hash superblockId) throws Exception {
        Date timeoutDate = SuperblockUtils.getNSecondsAgo(superblockTimeout.intValue());

        return getNewEventTimestampDate(superblockId).before(timeoutDate);
    }

    /**
     * Looks up a superblock's submission time in SyscoinClaimManager.
     * @param superblockId Superblock hash.
     * @return When the superblock was submitted.
     * @throws Exception
     */
    public BigInteger getNewEventTimestampBigInteger(Keccak256Hash superblockId) throws Exception {
        return claimManager.getNewSuperblockEventTimestamp(new Bytes32(superblockId.getBytes())).send().getValue();
    }


    public long getSuperblockConfirmations() throws Exception {
        return claimManager.superblockConfirmations().send().getValue().longValue();
    }

    public Address getClaimChallenger(Keccak256Hash superblockId) throws Exception {
        return new Address(claimManager.getClaimChallenger(new Bytes32(superblockId.getBytes())).send().getValue());
    }

    public boolean getClaimExists(Keccak256Hash superblockId) throws Exception {
        return claimManager.getClaimExists(new Bytes32(superblockId.getBytes())).send().getValue();
    }

    public String getClaimSubmitter(Keccak256Hash superblockId) throws Exception {
        return claimManager.getClaimSubmitter(new Bytes32(superblockId.getBytes())).send().getValue();
    }

    public boolean getClaimDecided(Keccak256Hash superblockId) throws Exception {
        return claimManager.getClaimDecided(new Bytes32(superblockId.getBytes())).send().getValue();
    }

    public boolean getClaimInvalid(Keccak256Hash superblockId) throws Exception {
        return claimManager.getClaimInvalid(new Bytes32(superblockId.getBytes())).send().getValue();
    }

    public boolean getInBattleAndSemiApprovable(Keccak256Hash superblockId) throws Exception {
        return claimManager.getInBattleAndSemiApprovable(new Bytes32(superblockId.getBytes())).send().getValue();
    }

    private BigInteger getDeposit(String account, SyscoinClaimManager myClaimManager) throws Exception {
        return myClaimManager.getDeposit(new Address(account)).send().getValue();
    }
    /**
     * Gets in process count of superblocks, this cannot be greator than 10 as submitters will not be allowed to submit, to keep
     * the amount of bonded deposits capped to 10 per every 10 minutes.
     *
     * @return BigInteger value representing the current count
     * @throws Exception
     */
    public BigInteger getProcessCounter() throws Exception {
        return claimManager.inProcessCounter().send().getValue();
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
    private void withdrawAllFundsExceptLimit(String account, SyscoinClaimManager myClaimManager) throws Exception {
        BigInteger currentDeposit = getDeposit(account, myClaimManager);
        BigInteger limit = BigInteger.valueOf(config.depositedFundsLimit());
        if (currentDeposit.compareTo(limit) > 0) {
            withdrawDeposit(myClaimManager, currentDeposit.subtract(limit));
        }
    }

    /**
     * Withdraw deposits so that only the maximum amount of funds (as determined by user configuration)
     * is left in the contract.
     * To be called after battles or when a superblock is approved/invalidated.
     * @param agentRole Agent role
     * @param account Caller's address.
     * @throws Exception
     */
    public void withdrawAllFundsExceptLimit(AgentRole agentRole, String account) throws Exception {
        SyscoinClaimManager myClaimManager;
        if (agentRole == CHALLENGER) {
            myClaimManager = claimManagerForChallenges;
        } else {
            myClaimManager = claimManager;
        }

        withdrawAllFundsExceptLimit(account, myClaimManager);
    }

    /**
     * Makes the minimum necessary deposit for reaching a given amount.
     * @param account Caller's address.
     * @param weiValue Deposit to be reached. This should be the caller's total deposit in the end.
     * @throws Exception
     */
    public void makeDepositIfNeeded(AgentRole agentRole, String account, BigInteger weiValue) throws Exception {

        SyscoinClaimManager myClaimManager = (agentRole == SUBMITTER) ? claimManager : claimManagerForChallenges;
        BigInteger currentDeposit = getDeposit(account, myClaimManager);
        if (currentDeposit.compareTo(weiValue) < 0) {
            BigInteger diff = weiValue.subtract(currentDeposit);
            makeDeposit(myClaimManager, diff);
        }
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
     * Proposes a superblock to SyscoinClaimManager. To be called from sendStoreSuperblock.
     * @param superblock Superblock to be proposed.
     * @return
     */
    public CompletableFuture<TransactionReceipt> proposeSuperblock(Superblock superblock) {
        return claimManager.proposeSuperblock(
                new Bytes32(superblock.getMerkleRoot().getBytes()),
                new Uint256(superblock.getLastSyscoinBlockTime()),
                new Uint256(superblock.getLastSyscoinBlockMedianTime()),
                new Bytes32(superblock.getLastSyscoinBlockHash().getBytes()),
                new Uint32(superblock.getlastSyscoinBlockBits()),
                new Bytes32(superblock.getParentId().getBytes())
        ).sendAsync();
    }


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
                claimManagerForChallenges.rejectClaim(new Bytes32(superblockId.getBytes())).sendAsync();
        futureReceipt.thenAcceptAsync( (TransactionReceipt receipt) ->
                logger.info("rejectClaim receipt {}", receipt.toString())
        );
    }

    /**
     * Challenges a superblock.
     * @param superblockId Hash of superblock to be challenged.
     * @throws Exception
     */
    public void challengeSuperblock(Keccak256Hash superblockId) throws Exception {
        if(!getClaimExists(superblockId) || getClaimDecided(superblockId) || getClaimInvalid(superblockId)) {
            logger.info("Superblock has already been decided upon or claim doesn't exist, skipping...{}", superblockId.toString());
            return;
        }

        if(getClaimSubmitter(superblockId).equalsIgnoreCase(ethAddresses.challengerAddress)){
            logger.info("You cannot challenge a superblock you have submitted yourself, skipping...{}", superblockId.toString());
            return;
        }

        // Make necessary deposit to cover reward
        // Note: initial deposit for challenging a superblock, just a best guess based on
        //       60 requests max for block headers and the final verify superblock cost
        makeDepositIfNeeded(CHALLENGER, ethAddresses.challengerAddress, minProposalDeposit);

        CompletableFuture<TransactionReceipt> futureReceipt =
                claimManagerForChallenges.challengeSuperblock(new Bytes32(superblockId.getBytes())).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                logger.info("challengeSuperblock receipt {}", receipt.toString()));
    }

    /**
     * Listens to SuperblockClaimSuccessful events from a given SyscoinClaimManager contract within a given block window
     * and parses web3j-generated instances into easier to manage SuperblockSuccessfulEvent objects.
     *
     * @param startBlock First Ethereum block to poll.
     * @param endBlock   Last Ethereum block to poll.
     * @return All SuperblockClaimSuccessful events from SyscoinClaimManager as SuperblockSuccessfulEvent objects.
     * @throws IOException
     */
    public List<SuperblockSuccessfulEvent> getSuperblockClaimSuccessfulEvents(long startBlock, long endBlock) throws IOException {
        List<SyscoinClaimManager.SuperblockClaimSuccessfulEventResponse> superblockSuccessfulEvents =
                claimManager.getSuperblockClaimSuccessfulEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        return superblockSuccessfulEvents.stream().map(response ->
                new SuperblockSuccessfulEvent(
                        Keccak256Hash.wrap(response.superblockHash.getValue()),
                        response.submitter.getValue(),
                        response.processCounter
                )
        ).collect(toList());
    }

    /**
     * Listens to SuperblockClaimFailed events from a given SyscoinClaimManager contract within a given block window
     * and parses web3j-generated instances into easier to manage SuperblockSuccessfulEvent objects.
     *
     * @param startBlock First Ethereum block to poll.
     * @param endBlock   Last Ethereum block to poll.
     * @return All SuperblockClaimFailed events from SyscoinClaimManager as SuperblockFailedEvent objects.
     * @throws IOException
     */
    public List<SuperblockFailedEvent> getSuperblockClaimFailedEvents(long startBlock, long endBlock) throws IOException {
        List<SyscoinClaimManager.SuperblockClaimFailedEventResponse> superblockFailedEvents =
                claimManagerForChallenges.getSuperblockClaimFailedEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        return superblockFailedEvents.stream().map(response ->
                new SuperblockFailedEvent(
                        Keccak256Hash.wrap(response.superblockHash.getValue()),
                        response.challenger.getValue(),
                        response.processCounter
                )
        ).collect(toList());
    }

}
