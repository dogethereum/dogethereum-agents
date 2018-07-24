package org.dogethereum.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.AltcoinBlock;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.store.BlockStoreException;
import org.dogethereum.agents.core.dogecoin.*;
import org.dogethereum.agents.core.eth.EthWrapper;
import org.spongycastle.util.encoders.Hex;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

/**
 * Monitors the Ethereum blockchain for superblock-related events
 * and defends/confirms the ones submitted by the agent.
 * @author Catalina Juarros
 */

@Service
@Slf4j(topic = "SuperblockDefenderClient")
public class SuperblockDefenderClient extends SuperblockBaseClient {

    private static long ETH_REQUIRED_CONFIRMATIONS = 5;

    public SuperblockDefenderClient() {
        super("Superblock defender client");
    }

    @Override
    protected void setupClient() {
        myAddress = ethWrapper.getGeneralPurposeAndSendSuperblocksAddress();
    }

    @Override
    public void reactToEvents(long fromBlock, long toBlock) {
        try {
            respondToBlockHeaderQueries(fromBlock, toBlock);
            respondToMerkleRootHashesQueries(fromBlock, toBlock);

            latestEthBlockProcessed = toBlock;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    protected void reactToElapsedTime() {
        try {
            confirmEarliestApprovableSuperblock();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /* ---- STATUS SETTERS ---- */
    // TODO: these might not be necessary if all status checks are handled through EthWrapper and extra databases

    private void setNewSuperblocks(long fromBlock, long toBlock) throws IOException {
        List<EthWrapper.SuperblockEvent> newSuperblockEvents =
                ethWrapper.getNewSuperblocks(fromBlock, toBlock);

        for (EthWrapper.SuperblockEvent newSuperblock : newSuperblockEvents) {
            if (isMine(newSuperblock)) { // todo: thing with 'who' field
                log.info("Updating info for new superblock {}.", Hex.toHexString(newSuperblock.superblockId));
                superblockChain.setStatus(newSuperblock.superblockId, SuperblockUtils.STATUS_NEW);
            }
        }
    }

    private void setApprovedSuperblocks(long fromBlock, long toBlock) throws IOException {
        List<EthWrapper.SuperblockEvent> approvedSuperblockEvents =
                ethWrapper.getApprovedSuperblocks(fromBlock, toBlock);

        for (EthWrapper.SuperblockEvent approvedSuperblock : approvedSuperblockEvents) {
            if (isMine(approvedSuperblock)) {
                log.info("Updating info for approved superblock {}.", Hex.toHexString(approvedSuperblock.superblockId));
                superblockChain.setStatus(approvedSuperblock.superblockId, SuperblockUtils.STATUS_NEW);
            }
        }
    }

    private void setSemiApprovedSuperblocks(long fromBlock, long toBlock) throws IOException {
        List<EthWrapper.SuperblockEvent> semiApprovedSuperblockEvents =
                ethWrapper.getSemiApprovedSuperblocks(fromBlock, toBlock);

        for (EthWrapper.SuperblockEvent semiApprovedSuperblock : semiApprovedSuperblockEvents) {
            if (isMine(semiApprovedSuperblock)) {
                log.info("Updating info for approved superblock {}.",
                        Hex.toHexString(semiApprovedSuperblock.superblockId));
                superblockChain.setStatus(semiApprovedSuperblock.superblockId, SuperblockUtils.STATUS_NEW);
            }
        }
    }


    /* ---- CONFIRMING/DEFENDING ---- */

    /**
     * Find earliest superblock that's unchallenged and stored locally,
     * but not confirmed in Dogethereum Contracts, and confirm it if its timeout has passed
     * and it either received no challenges or won all battles.
     * If the superblock is indeed confirmed, its status in Dogethereum Contracts
     * will be set to Approved if it received no challenges and SemiApproved otherwise.
     * @throws Exception
     */
    private void confirmEarliestApprovableSuperblock() throws Exception {
        byte[] bestSuperblockId = ethWrapper.getBestSuperblockId();
        Superblock chainHead = superblockChain.getChainHead();

        if (Arrays.equals(chainHead.getSuperblockId(), bestSuperblockId)) {
            // Contract and local db best superblocks are the same, do nothing.
            return;
        }

        Superblock toConfirm = superblockChain.getFirstDescendant(bestSuperblockId);

        if (toConfirm == null) {
            // TODO: see if this should raise an exception, because it's a pretty bad state
            log.info("Best superblock from contracts, {}, not found in local database. Stopping.",
                    Hex.toHexString(bestSuperblockId));
        } else {
            byte[] toConfirmId = toConfirm.getSuperblockId();

            if (newAndTimeoutPassed(toConfirm) || inBattleAndSemiApprovable(toConfirm)) {
                log.info("Confirming superblock {}", Hex.toHexString(toConfirmId));
                ethWrapper.checkClaimFinished(toConfirmId);
            } else if (semiApprovedAndApprovable(toConfirm)) {
                byte[] descendantId = superblockChain.getFirstDescendant(toConfirmId).getSuperblockId();
                log.info("Confirming semi-approved superblock {}", Hex.toHexString(toConfirmId));
                ethWrapper.confirmClaim(toConfirmId, descendantId);
            }
        }
    }

    private void respondToBlockHeaderQueries(long fromBlock, long toBlock)
            throws IOException, BlockStoreException, Exception {
        List<EthWrapper.QueryBlockHeaderEvent> queryBlockHeaderEvents =
                ethWrapper.getBlockHeaderQueries(fromBlock, toBlock);

        for (EthWrapper.QueryBlockHeaderEvent queryBlockHeader : queryBlockHeaderEvents) {
            if (isMine(queryBlockHeader)) {
                log.info("Header requested for session {}. Responding now.",
                        Hex.toHexString(queryBlockHeader.sessionId));

                StoredBlock dogeBlock = dogecoinWrapper.getBlock(Sha256Hash.wrap(queryBlockHeader.dogeBlockHash));
                ethWrapper.respondBlockHeader(queryBlockHeader.sessionId, (AltcoinBlock) dogeBlock.getHeader());
                ethWrapper.verifySuperblock(queryBlockHeader.sessionId);
            }
        }
    }

    private void respondToMerkleRootHashesQueries(long fromBlock, long toBlock) throws IOException, Exception {
        List<EthWrapper.QueryMerkleRootHashesEvent> queryMerkleRootHashesEvents =
                ethWrapper.getMerkleRootHashesQueries(fromBlock, toBlock);

        for (EthWrapper.QueryMerkleRootHashesEvent queryMerkleRootHashes : queryMerkleRootHashesEvents) {
            if (isMine(queryMerkleRootHashes)) {
                log.info("Merkle root hashes requested for session {}. Responding now.",
                        Hex.toHexString(queryMerkleRootHashes.sessionId));

                Superblock superblock = superblockChain.getSuperblock(queryMerkleRootHashes.superblockId);
                ethWrapper.respondMerkleRootHashes(queryMerkleRootHashes.sessionId, superblock.getDogeBlockHashes());
                ethWrapper.verifySuperblock(queryMerkleRootHashes.sessionId);
            }
        }
    }


    /* ---- HELPER METHODS ---- */

    private boolean isMine(EthWrapper.SuperblockEvent superblockEvent) {
        return superblockEvent.who.equals(myAddress);
    }

    private boolean isMine(EthWrapper.QueryEvent queryEvent) {
        return queryEvent.claimant.equals(myAddress);
    }

    private boolean isMine(EthWrapper.QueryBlockHeaderEvent queryBlockHeader) {
        return queryBlockHeader.submitter.equals(myAddress);
    }

    private boolean isMine(EthWrapper.QueryMerkleRootHashesEvent queryMerkleRootHashes) {
        return queryMerkleRootHashes.submitter.equals(myAddress);
    }

    private boolean submittedTimeoutPassed(byte[] superblockId) throws Exception {
        return ethWrapper.getNewEventTimestampDate(superblockId).before(getTimeoutDate());
    }

    private Date getTimeoutDate() throws Exception {
        int superblockTimeout = ethWrapper.getSuperblockTimeout().intValue();
        return SuperblockUtils.getNSecondsAgo(superblockTimeout);
    }

    private boolean challengeTimeoutPassed(byte[] superblockId) throws Exception {
        return ethWrapper.getClaimChallengeTimeoutDate(superblockId).before(getTimeoutDate());
    }

    private boolean newAndTimeoutPassed(Superblock superblock) throws Exception {
        byte[] superblockId = superblock.getSuperblockId();
        return (ethWrapper.isSuperblockNew(superblockId) && submittedTimeoutPassed(superblockId));
    }

    /**
     * Check if a given superblock is in battle and meets the necessary and sufficient conditions
     * for being semi-approved when calling checkClaimFinished.
     * @param superblock Superblock to be confirmed.
     * @return True if the superblock can be safely semi-approved, false otherwise.
     * @throws Exception
     */
    private boolean inBattleAndSemiApprovable(Superblock superblock) throws Exception {
        byte[] superblockId = superblock.getSuperblockId();
        if (!ethWrapper.isSuperblockInBattle(superblockId))
            return false;
        if (ethWrapper.getClaimInvalid(superblockId))
            return false;
        if (ethWrapper.getClaimVerificationOngoing(superblockId))
            return false;
        if (!challengeTimeoutPassed(superblockId))
            return false;
        if (ethWrapper.getClaimRemainingChallengers(superblockId) > 0)
            return false;
        return true;
    }

    /**
     * Check if a superblock is semi-approved and has a semi-approved descendant.
     * @param superblock Superblock to be confirmed.
     * @return True if the superblock can be safely approved, false otherwise.
     * @throws Exception
     */
    private boolean semiApprovedAndApprovable(Superblock superblock) throws Exception {
        byte[] superblockId = superblock.getSuperblockId();
        byte[] descendantId = superblockChain.getFirstDescendant(superblockId).getSuperblockId();
        return (descendantId != null && ethWrapper.isSuperblockSemiApproved(descendantId)
                && ethWrapper.isSuperblockSemiApproved(superblockId));
    }


    /* ---- OVERRRIDE ABSTRACT METHODS ---- */

    @Override
    protected Boolean isEnabled() {
        return config.isDogeBlockSubmitterEnabled();
    }

    @Override
    protected String getLastEthBlockProcessedFilename() {
        return "SuperblockDefenderLatestEthBlockProcessedFile.dat";
    }
}
