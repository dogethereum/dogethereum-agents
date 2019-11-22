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

import org.sysethereum.agents.util.JsonGasRanges;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.generated.Bytes32;

import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import javax.annotation.Nullable;
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

    // Extensions of contracts generated automatically by web3j
    private final SyscoinBattleManagerExtended battleManager;
    private final SyscoinBattleManagerExtended battleManagerForChallenges;

    private BigInteger gasPriceMinimum;
    private final BigInteger gasPriceMaximum;

    private final SuperblockContractApi superblockContractApi;
    private final BattleContractApi battleContractApi;
    private final ClaimContractApi claimContractApi;
    private final BigInteger superblockDuration;

    private final BigInteger minProposalDeposit;
    private final SuperblockChain localSuperblockChain;
    private final SyscoinWrapper syscoinWrapper;
    private final JsonGasRanges jsonGasRanges;
    private final Context syscoinContext;

    @Autowired
    public EthWrapper(
            Context syscoinContext,
            SystemProperties config,
            SuperblockChain superblockChain,
            SyscoinWrapper syscoinWrapper,
            Web3j web3,
            Web3j web3Secondary,
            EthAddresses ethAddresses,
            SyscoinBattleManagerExtended battleManager,
            SyscoinBattleManagerExtended battleManagerForChallenges,
            SuperblockContractApi superblockContractApi,
            BattleContractApi battleContractApi,
            ClaimContractApi claimContractApi,
            BigInteger superblockDuration,
            BigInteger minProposalDeposit,
            JsonGasRanges jsonGasRanges
    ) throws Exception {
        this.syscoinContext = syscoinContext;
        this.localSuperblockChain = superblockChain;
        this.syscoinWrapper = syscoinWrapper;
        this.web3 = web3;
        this.web3Secondary = web3Secondary;
        this.ethAddresses = ethAddresses;
        this.battleManager = battleManager;
        this.battleManagerForChallenges = battleManagerForChallenges;
        this.superblockContractApi = superblockContractApi;
        this.battleContractApi = battleContractApi;
        this.claimContractApi = claimContractApi;
        this.superblockDuration = superblockDuration;
        this.minProposalDeposit = minProposalDeposit;

        this.gasPriceMinimum = BigInteger.valueOf(config.gasPriceMinimum());
        this.gasPriceMaximum = config.gasPriceMaximum() == 0? BigInteger.ZERO: BigInteger.valueOf(config.gasPriceMaximum());

        this.jsonGasRanges = jsonGasRanges;

        updateContractFacadesGasPrice();
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
        return arePendingTransactionsFor(ethAddresses.generalPurposeAddress);
    }

    public boolean arePendingTransactionsForChallengerAddress() throws InterruptedException, IOException {
        return arePendingTransactionsFor(ethAddresses.challengerAddress);
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
        BigInteger jsonGasPrice = jsonGasRanges.gasPrice();
        BigInteger suggestedGasPrice = jsonGasPrice.equals(BigInteger.ZERO)? web3.ethGasPrice().send().getGasPrice(): jsonGasPrice;
        if (suggestedGasPrice.compareTo(gasPriceMinimum) > 0) {
            if (!gasPriceMaximum.equals(BigInteger.ZERO) && suggestedGasPrice.compareTo(gasPriceMaximum) > 0) {
                suggestedGasPrice = gasPriceMaximum;
            }
            if(!gasPriceMinimum.equals(suggestedGasPrice)) {
                gasPriceMinimum = suggestedGasPrice;
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
     *
     * @param toConfirm Superblock to be confirmed.
     * @param bestContractSuperblockId Hash of the best superblock from contract.
     * @return Highest superblock in main chain that's newer than the given superblock
     *         if such a superblock exists, null otherwise (i.e. given superblock isn't in main chain
     *         or has no semi-approved descendants).
     * @throws BlockStoreException
     * @throws IOException
     * @throws Exception
     */
    @Nullable
    public Superblock getHighestApprovableOrNewDescendant(Superblock toConfirm, Keccak256Hash bestContractSuperblockId)
            throws BlockStoreException, IOException, Exception {
        if (localSuperblockChain.getByHash(bestContractSuperblockId) == null) {
            // The superblock isn't in the main chain.
            logger.info("Superblock {} is not in the main chain. Returning from getHighestApprovableOrNewDescendant.", bestContractSuperblockId);
            return null;
        }

        //noinspection ConstantConditions
        if (localSuperblockChain.getByHash(bestContractSuperblockId).getHeight() == localSuperblockChain.getChainHeight()) {
            // There's nothing above the tip of the chain.
            logger.info("Superblock {} is above the tip of the chain. Returning from getHighestApprovableOrNewDescendant.", bestContractSuperblockId);
            return null;
        }
        Superblock sb = localSuperblockChain.getChainHead();
        while (sb != null &&
                !sb.getHash().equals(bestContractSuperblockId) &&
                !newAndTimeoutPassed(sb.getHash()) &&
                !claimContractApi.getInBattleAndSemiApprovable(sb.getHash()) &&
                !semiApprovedAndApprovable(toConfirm, sb)) {
            sb = localSuperblockChain.getByHash(sb.getParentId());
        }
        return sb;
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
        if (localSuperblockChain.getByHash(superblockId) == null) {
            // The superblock isn't in the main chain.
            logger.info("Superblock {} is not in the main chain. Returning from getHighestSemiApprovedOrApprovedDescendant.", superblockId);
            return null;
        }

        //noinspection ConstantConditions
        if (localSuperblockChain.getByHash(superblockId).getHeight() == localSuperblockChain.getChainHeight()) {
            // There's nothing above the tip of the chain.
            logger.info("Superblock {} is the tip of the superblock chain, no descendant exists. Returning from getHighestSemiApprovedOrApprovedDescendant.", superblockId);
            return null;
        }

        Superblock head = localSuperblockChain.getChainHead();
        while (head != null
                && !head.getHash().equals(superblockId)
                && !superblockContractApi.isSemiApproved(head.getHash())
                && !superblockContractApi.isApproved(head.getHash())) {
            head = localSuperblockChain.getByHash(head.getParentId());
        }

        return head;
    }
    /**
     * Proposes a superblock to SyscoinClaimManager in order to keep the Sysethereum contracts updated.
     * @param superblock Oldest superblock that is already stored in the local database,
     *                   but still hasn't been submitted to Sysethereum Contracts.
     * @throws Exception If superblock hash cannot be calculated.
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean sendStoreSuperblock(Superblock superblock, String account) throws Exception {

        // Check if the parent has been approved before sending this superblock.
        Keccak256Hash parentId = superblock.getParentId();
        if (!(superblockContractApi.isApproved(parentId) || superblockContractApi.isSemiApproved(parentId))) {
            logger.info("Superblock {} not sent because its parent was neither approved nor semi approved.", superblock.getHash());
            return false;
        }
        // if claim exists we check to ensure the superblock chain isn't "stuck" and can be re-approved to be built even if it exists
        if (claimContractApi.getClaimExists(superblock.getHash())) {
            boolean allowed = claimContractApi.getClaimInvalid(superblock.getHash())
                    && claimContractApi.getClaimDecided(superblock.getHash())
                    && !claimContractApi.getClaimSubmitter(superblock.getHash()).equalsIgnoreCase(account);

            if (!allowed) {
                logger.info("Superblock {} has already been sent. Returning.", superblock.getHash());
                return false;
            }

            if (superblockContractApi.isApproved(parentId)) {
                if (!superblockContractApi.getBestSuperblockId().equals(parentId)) {
                    logger.info("Superblock {} parent is approved but not best. Returning.", superblock.getHash());
                    return false;
                }
            } else {
                if (!superblockContractApi.isSemiApproved(parentId)) {
                    logger.info("Superblock {} parent is neither approved nor semi-approved. Returning.", superblock.getHash());
                    return false;
                }
            }
        }


        logger.info("About to send superblock {} to the bridge.", superblock.getHash());
        Thread.sleep(500);
        if (arePendingTransactionsForSendSuperblocksAddress()) {
            logger.debug("Skipping sending superblocks, there are pending transaction for the sender address.");
            return false;
        }

        // Make any necessary deposits for sending the superblock
        claimContractApi.makeDepositIfNeeded(AgentRole.SUBMITTER, account, getSuperblockDeposit());

        // The parent is either approved or semi approved. We can send the superblock.
        CompletableFuture<TransactionReceipt> futureReceipt = claimContractApi.proposeSuperblock(superblock);

        logger.info("Sent superblock {}", superblock.getHash());
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
                battleManagerForChallenges.getNewBlockHeadersEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (SyscoinBattleManager.RespondBlockHeadersEventResponse response : newBattleEvents) {
            RespondHeadersEvent newRespondHeadersEvent = new RespondHeadersEvent();
            newRespondHeadersEvent.superblockHash = Keccak256Hash.wrap(response.superblockHash.getValue());
            newRespondHeadersEvent.merkleHashCount = response.merkleHashCount.getValue().intValue();
            newRespondHeadersEvent.submitter = response.submitter.getValue();
            result.add(newRespondHeadersEvent);
        }

        return result;
    }

    public static class RespondHeadersEvent {
        public Keccak256Hash superblockHash;
        public int merkleHashCount;
        public String submitter;
    }


    /* ---------------------------------- */
    /* --------- Battle section --------- */
    /* ---------------------------------- */
    /**
     * Responds to a challenge with all block headers
     * @param superblockId Battle session superblock.
     */
    public void respondBlockHeaders(Keccak256Hash superblockId, int merkleHashCount) throws Exception {
        Context.propagate(syscoinContext);
        Thread.sleep(500); // in case the transaction takes some time to complete
        if (arePendingTransactionsForSendSuperblocksAddress()) {
            throw new Exception("Skipping respondBlockHeader, there are pending transaction for the sender address.");
        }
        int numHashesRequired = merkleHashCount < 3? 16: 12;
        int startIndex = merkleHashCount*16;
        int endIndex = startIndex + numHashesRequired;
        if(startIndex > 48)
            throw new Exception("Skipping respondBlockHeader, startIndex cannot be >48.");
        Superblock superblock = localSuperblockChain.getByHash(superblockId);
        assert superblock != null;
        List<Sha256Hash> listHashes = superblock.getSyscoinBlockHashes();
        if(!superblockDuration.equals(BigInteger.valueOf(listHashes.size())))
            throw new Exception("Skipping respondBlockHeader, superblock hash array list is incorrect length.");

        byte[] blockHeaderBytes = null;
        for(int i = startIndex;i<endIndex;i++){
            Block altBlock = syscoinWrapper.getBlock(listHashes.get(i)).getHeader().cloneAsHeader();
            byte[] serializedBytes = altBlock.bitcoinSerialize();
            if(blockHeaderBytes == null)
                blockHeaderBytes = serializedBytes;
            else
                blockHeaderBytes = Bytes.concat(blockHeaderBytes, serializedBytes);
        }

        CompletableFuture<TransactionReceipt> futureReceipt = battleManager.respondBlockHeaders(
                new Bytes32(superblockId.getBytes()), new DynamicBytes(blockHeaderBytes), new Uint256(numHashesRequired)).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                logger.info("Responded to last block header query for Syscoin superblock {}, Receipt: {}",
                        superblockId, receipt)
        );


    }
    // TODO: see if the challenger should know which superblock this is


    /**
     * Calls timeout for a session where a participant hasn't responded in time, thus closing the battle.
     * @param superblockId Battle session ID.
     * @param myBattleManager SyscoinBattleManager contract that the caller is using to handle its battles.
     */
    public void timeout(Keccak256Hash superblockId, SyscoinBattleManagerExtended myBattleManager) {
        CompletableFuture<TransactionReceipt> futureReceipt = myBattleManager.timeout(new Bytes32(superblockId.getBytes())).sendAsync();
        futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                logger.info("Called timeout for superblock {}", superblockId));
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
        return descendant.getHeight() - superblock.getHeight() >= claimContractApi.getSuperblockConfirmations()
                && superblockContractApi.isSemiApproved(descendant.getHash())
                && superblockContractApi.isSemiApproved(superblock.getHash());
    }


    public boolean newAndTimeoutPassed(Keccak256Hash superblockId) throws Exception {
        return superblockContractApi.isNew(superblockId) && claimContractApi.submittedTimeoutPassed(superblockId);
    }


}