package org.dogethereum.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.AltcoinBlock;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.store.BlockStoreException;
import org.dogethereum.agents.core.dogecoin.SuperblockUtils;
import org.dogethereum.agents.core.eth.EthWrapper;
import org.dogethereum.agents.core.dogecoin.Keccak256Hash;
import org.dogethereum.agents.core.dogecoin.Superblock;
import org.dogethereum.agents.core.eth.EthWrapper;
import org.spongycastle.util.encoders.Hex;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import org.web3j.crypto.Hash;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

/**
 * Monitors the Ethereum blockchain for superblock-related events
 * and challenges invalid submissions.
 * @author Catalina Juarros
 * @author Ismael Bejarano
 */

@Service
@Slf4j(topic = "SuperblockChallengerClient")
public class SuperblockChallengerClient extends SuperblockBaseClient {

    public SuperblockChallengerClient() {
        super("Superblock challenger client");
    }

    @Override
    protected void setupClient() {
        myAddress = ethWrapper.getDogeBlockChallengerAddress();
    }

    @Override
    public long reactToEvents(long fromBlock, long toBlock) {
        try {
            respondToNewBattle(fromBlock, toBlock);
            validateNewSuperblocks(fromBlock, toBlock);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return latestEthBlockProcessed;
        }
        return toBlock;
    }

    @Override
    protected void reactToElapsedTime() {
        try {
            callBattleTimeouts();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /* ---- CHALLENGING ---- */

    /**
     * Start challenges for all new superblocks that aren't in the challenger's local chain.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    private void validateNewSuperblocks(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.SuperblockEvent> newSuperblockEvents = ethWrapper.getNewSuperblocks(fromBlock, toBlock);

        List<Keccak256Hash> toChallenge = new ArrayList<>();
        for (EthWrapper.SuperblockEvent newSuperblock : newSuperblockEvents) {
            log.info("NewSuperblock {}. Validating...", newSuperblock.superblockId);

            Superblock superblock = superblockChain.getSuperblock(newSuperblock.superblockId);
            if (superblock == null) {
                BigInteger height = ethWrapper.getSuperblockHeight(newSuperblock.superblockId);
                Superblock localSuperblock = superblockChain.getSuperblockByHeight(height.longValue());
                if (localSuperblock == null) {
                    //FIXME: Local superbockchain might be out of sync
                    log.info("Superblock {} not present in our superblock chain", newSuperblock.superblockId);
                } else {
                    log.info("Superblock {} at height {} is replaced by {} in our superblock chain",
                            newSuperblock.superblockId,
                            height,
                            localSuperblock.getSuperblockId());
                    toChallenge.add(newSuperblock.superblockId);
                }
            } else {
                log.info("... superblock present in our superblock chain");
            }
        }

        for (Keccak256Hash superblockId : toChallenge) {
            CompletableFuture<TransactionReceipt> futureReceipt = ethWrapper.challengeSuperblock(superblockId);
            futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                    log.info("challengeSuperblock receipt {}", receipt.toString()));
        }
    }

    /**
     * Query block hashes for all new battle events that the challenger is taking part in.
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    private void respondToNewBattle(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.NewBattleEvent> newBattleEvents = ethWrapper.getNewBattleEvents(fromBlock, toBlock);

        List<EthWrapper.NewBattleEvent> toQuery = new ArrayList<>();
        for (EthWrapper.NewBattleEvent newBattleEvent : newBattleEvents) {
            if (isMine(newBattleEvent)) {
                toQuery.add(newBattleEvent);
            }
        }

        for (EthWrapper.NewBattleEvent newBattleEvent : toQuery) {
            CompletableFuture<TransactionReceipt> futureReceipt = ethWrapper.queryMerkleRootHashes(
                    newBattleEvent.superblockId,
                    newBattleEvent.sessionId);
            futureReceipt.thenAcceptAsync((TransactionReceipt receipt) ->
                    log.info("queryMerkleRootHashes receipt {}", receipt.toString()));
        }
    }


    private void respondToMerkleRootHashesEventResponses(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.RespondMerkleRootHashesEvent> defenderResponses =
                ethWrapper.getRespondMerkleRootHashesEvents(fromBlock, toBlock);

        for (EthWrapper.RespondMerkleRootHashesEvent defenderResponse : defenderResponses) {
            if (isMine(defenderResponse)) {
                startBlockHeaderQueries(defenderResponse);
            }
        }
    }

    private void respondToBlockHeaderEventResponses(long fromBlock, long toBlock) throws Exception {
        List<EthWrapper.RespondBlockHeaderEvent> defenderResponses =
                ethWrapper.getRespondBlockHeaderEvents(fromBlock, toBlock);

        for (EthWrapper.RespondBlockHeaderEvent defenderResponse : defenderResponses) {
            if (isMine(defenderResponse)) {
                reactToBlockHeaderResponse(defenderResponse);
            }
        }
    }

    private void startBlockHeaderQueries(EthWrapper.RespondMerkleRootHashesEvent defenderResponse) throws Exception {
        Keccak256Hash superblockId = defenderResponse.superblockId;
        List<Sha256Hash> dogeBlockHashes = defenderResponse.blockHashes;
        log.info("Starting block header queries for superblock {}", superblockId);

        if (!dogeBlockHashes.isEmpty()) {
            ethWrapper.queryBlockHeader(superblockId, defenderResponse.sessionId, dogeBlockHashes.get(0));
        } else {
            ethWrapper.verifySuperblock(defenderResponse.sessionId);
        }
    }

    private void reactToBlockHeaderResponse(EthWrapper.RespondBlockHeaderEvent defenderResponse) throws Exception {
        Sha256Hash dogeBlockHash = Sha256Hash.twiceOf(defenderResponse.blockHeader);
        Keccak256Hash superblockId = defenderResponse.superblockId;
        Superblock superblock = superblockChain.getSuperblock(superblockId);

        int idx = superblock.getDogeBlockLeafIndex(dogeBlockHash) + 1; // next block to respond to
        if (idx < superblock.getDogeBlockHashes().size()) {
            // not last hash
            Sha256Hash nextDogeBlockHash = superblock.getDogeBlockHashes().get(idx);
            log.info("Querying block header {}", nextDogeBlockHash);
            ethWrapper.queryBlockHeader(superblockId, defenderResponse.sessionId, nextDogeBlockHash);
        } else {
            // last hash; end battle
            log.info("All block hashes for superblock {} have been received. Verifying it now.", superblockId);
            ethWrapper.verifySuperblock(defenderResponse.sessionId);
        }
    }


    /* ---- HELPER METHODS ---- */

    private boolean isMine(EthWrapper.RespondMerkleRootHashesEvent respondMerkleRootHashesEvent) {
        return respondMerkleRootHashesEvent.challenger.equals(myAddress);
    }

    private boolean isMine(EthWrapper.RespondBlockHeaderEvent respondBlockHeaderEvent) {
        return respondBlockHeaderEvent.challenger.equals(myAddress);
    }


    /* ---- OVERRIDE ABSTRACT METHODS ---- */

    @Override
    protected boolean isEnabled() {
        return config.isDogeBlockChallengerEnabled();
    }

    @Override
    protected String getLastEthBlockProcessedFilename() {
        return "SuperblockChallengerLatestEthBlockProcessedFile.dat";
    }

    @Override
    protected String getBattleSetFilename() {
        return "SuperblockChallengerBattleSet.dat";
    }

    @Override
    protected boolean isMine(EthWrapper.NewBattleEvent newBattleEvent) {
        return newBattleEvent.challenger.equals(myAddress);
    }

    @Override
    protected long getConfirmations() {
        //FIXME: Move to a new a configuration property?
        return config.getAgentConstants().getEth2DogeMinimumAcceptableConfirmations();
    }

    @Override
    protected void callBattleTimeouts() throws Exception {
        for (Keccak256Hash sessionId : battleSet) {
            if (ethWrapper.getSubmitterHitTimeout(sessionId)) {
                log.info("Submitter hit timeout on session {}. Calling timeout.", sessionId);
                ethWrapper.timeout(sessionId);
            }
        }
    }
}
