package org.sysethereum.agents.core.bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.sysethereum.agents.constants.AgentRole;
import org.sysethereum.agents.constants.SystemProperties;
import org.sysethereum.agents.contract.SyscoinClaimManager;
import org.sysethereum.agents.contract.SyscoinClaimManagerExtended;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;
import org.sysethereum.agents.core.syscoin.SuperblockUtils;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import static org.sysethereum.agents.constants.AgentRole.SUBMITTER;

@Service
public class ClaimContractApi {

    private static final Logger logger = LoggerFactory.getLogger("ClaimContractApi");

    private final SystemProperties config;
    private final BigInteger superblockTimeout;
    private final SyscoinClaimManagerExtended claimManager;
    private final SyscoinClaimManagerExtended claimManagerGetter;
    private final SyscoinClaimManagerExtended claimManagerForChallenges;
    private final SyscoinClaimManagerExtended claimManagerForChallengesGetter;

    public ClaimContractApi(
            SystemProperties config,
            BigInteger superblockTimeout,
            SyscoinClaimManagerExtended claimManager,
            SyscoinClaimManagerExtended claimManagerGetter,
            SyscoinClaimManagerExtended claimManagerForChallenges,
            SyscoinClaimManagerExtended claimManagerForChallengesGetter
    ) {
        this.config = config;
        this.superblockTimeout = superblockTimeout;
        this.claimManager = claimManager;
        this.claimManagerGetter = claimManagerGetter;
        this.claimManagerForChallenges = claimManagerForChallenges;
        this.claimManagerForChallengesGetter = claimManagerForChallengesGetter;
    }

    public void updateGasPrice(BigInteger gasPriceMinimum) {
        //noinspection deprecation
        claimManager.setGasPrice(gasPriceMinimum);
        //noinspection deprecation
        claimManagerForChallenges.setGasPrice(gasPriceMinimum);
        //noinspection deprecation
        claimManagerGetter.setGasPrice(gasPriceMinimum);
        //noinspection deprecation
        claimManagerForChallengesGetter.setGasPrice(gasPriceMinimum);
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

    public boolean getAbilityToProposeNextSuperblock() throws Exception {
        return claimManagerGetter.getAbilityToProposeNextSuperblock(new Uint256(System.currentTimeMillis()/1000)).send().getValue();
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
        return claimManagerGetter.getNewSuperblockEventTimestamp(new Bytes32(superblockId.getBytes())).send().getValue();
    }


    public long getSuperblockConfirmations() throws Exception {
        return claimManagerGetter.superblockConfirmations().send().getValue().longValue();
    }

    public Address getClaimChallenger(Keccak256Hash superblockId) throws Exception {
        return new Address(claimManagerGetter.getClaimChallenger(new Bytes32(superblockId.getBytes())).send().getValue());
    }

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

    private BigInteger getDeposit(String account, SyscoinClaimManagerExtended myClaimManager) throws Exception {
        return myClaimManager.getDeposit(new Address(account)).send().getValue();
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

    /**
     * Makes the minimum necessary deposit for reaching a given amount.
     * @param account Caller's address.
     * @param weiValue Deposit to be reached. This should be the caller's total deposit in the end.
     * @throws Exception
     */
    public void makeDepositIfNeeded(AgentRole agentRole, String account, BigInteger weiValue) throws Exception {

        SyscoinClaimManager myClaimManager = (agentRole == SUBMITTER) ? claimManager : claimManagerForChallenges;
        SyscoinClaimManagerExtended myClaimManagerGetter = (agentRole == SUBMITTER) ? claimManagerGetter : claimManagerForChallengesGetter;

        BigInteger currentDeposit = getDeposit(account, myClaimManagerGetter);
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
                new Uint256(superblock.getChainWork()),
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
                claimManager.rejectClaim(new Bytes32(superblockId.getBytes())).sendAsync();
        futureReceipt.thenAcceptAsync( (TransactionReceipt receipt) ->
                logger.info("rejectClaim receipt {}", receipt.toString())
        );
    }
}
