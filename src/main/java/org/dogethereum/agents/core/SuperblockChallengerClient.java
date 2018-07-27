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

    private void startBlockHeaderQueries(EthWrapper.RespondMerkleRootHashesEvent defenderResponse) throws Exception {
        Keccak256Hash superblockId = defenderResponse.superblockId;
        List<Sha256Hash> dogeBlockHashes = defenderResponse.blockHashes;
        log.info("Starting block header requests for superblock {}", superblockId);

        // TODO: what if the list is empty?
        ethWrapper.queryBlockHeader(superblockId, defenderResponse.sessionId, dogeBlockHashes.get(0));
    }

//    private void respondToBlockHeaderEventResponses(long fromBlock, long toBlock) {
//    }


    /* ---- HELPER METHODS ---- */

    private boolean isMine(EthWrapper.RespondMerkleRootHashesEvent respondMerkleRootHashesEvent) {
        return respondMerkleRootHashesEvent.challenger.equals(myAddress);
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
