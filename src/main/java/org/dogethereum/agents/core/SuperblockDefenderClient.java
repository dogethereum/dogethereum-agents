package org.dogethereum.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.dogethereum.agents.constants.SystemProperties;
import org.dogethereum.agents.core.dogecoin.*;
import org.dogethereum.agents.core.eth.EthWrapper;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

/**
 * Monitors the Ethereum blockchain for superblock-related events
 * and defends/confirms the ones submitted by the agent.
 * @author Catalina Juarros
 */

@Service
@Slf4j(topic = "SuperblockDefenderClient")
public class SuperblockDefenderClient {

    @Autowired
    private DogecoinWrapper dogecoinWrapper;

    @Autowired
    private EthWrapper ethWrapper;

    @Autowired
    private SuperblockChain superblockChain;

    private SystemProperties config;

    private static long ETH_REQUIRED_CONFIRMATIONS = 5;

    private long latestEthBlockProcessed;
    private File dataDirectory;
    private File latestEthBlockProcessedFile;

    public SuperblockDefenderClient() {}

    @PostConstruct
    public void setup() throws Exception {
        this.config = SystemProperties.CONFIG;
        if (config.isDogeBlockSubmitterEnabled()) {
            this.latestEthBlockProcessed = config.getAgentConstants().getEthInitialCheckpoint();
            this.dataDirectory = new File(config.dataDirectory());
            this.latestEthBlockProcessedFile = new File(dataDirectory.getAbsolutePath() +
                    "/SuperblockDefenderLatestEthBlockProcessedFile.dat");
            restoreLatestEthBlockProcessed();

            new Timer("Superblock defender client").scheduleAtFixedRate(new DefendSuperblocksTimerTask(),
                    Calendar.getInstance().getTime(), 15 * 1000);
        }
    }

    private class DefendSuperblocksTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (!ethWrapper.isEthNodeSyncing()) {
                    ethWrapper.updateContractFacadesGasPrice();
                    long fromBlock = latestEthBlockProcessed + 1;
                    long toBlock = ethWrapper.getEthBlockCount() -
                            config.getAgentConstants().getEth2DogeMinimumAcceptableConfirmations();

                    // Ignore execution if nothing to process
                    if (fromBlock > toBlock) return;

                    confirmEarliestApprovableSuperblock();

                    List<EthWrapper.SuperblockEvent> challengedSuperblockEvents =
                            ethWrapper.getChallengedSuperblocks(fromBlock, toBlock);

                    for (EthWrapper.SuperblockEvent challengedSuperblock : challengedSuperblockEvents) {
                        if (isMine(challengedSuperblock)) {
                            log.info("Superblock {} has been challenged. Defending it now.",
                                    Hex.toHexString(challengedSuperblock.superblockId));
                        }
                    }

                    latestEthBlockProcessed = toBlock;
                    flushLatestEthBlockProcessed();
                } else {
                    log.warn("DefendSuperblocksTimerTask skipped because the eth node is syncing blocks");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
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

    private void respondToBlockHeaderQueries(long fromBlock, long toBlock) throws IOException {
        List<EthWrapper.QueryBlockHeaderEvent> queryBlockHeaderEvents =
                ethWrapper.getBlockHeaderQueries(fromBlock, toBlock);

        for (EthWrapper.QueryBlockHeaderEvent queryBlockHeader : queryBlockHeaderEvents) {
            if (isMine(queryBlockHeader)) {
                log.info("Header requested for superblock {}. Responding now.",
                        Hex.toHexString(queryBlockHeader.sessionId));

                ethWrapper.respondBlockHeader(queryBlockHeader.sessionId, null);
            }

            // Call respondBlockHeader
        }
    }


    /* ---- HELPER METHODS ---- */

    // TODO: figure out what 'who' field should be compared to
    private boolean isMine(EthWrapper.SuperblockEvent superblockEvent) {
        return true;
    }

    private boolean isMine(EthWrapper.QueryEvent queryEvent) {
        return true;
    }

    private boolean isMine(EthWrapper.QueryBlockHeaderEvent queryBlockHeader) {
        return true;
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
        if (descendantId == null || !ethWrapper.isSuperblockSemiApproved(descendantId)) {
            return false;
        } else {
            return ethWrapper.isSuperblockApproved(superblock.getSuperblockId());
        }
    }


    /* ---- DATABASE METHODS ---- */

    // TODO: see if these should be moved to another file to avoid repeated code
    private void restoreLatestEthBlockProcessed() throws IOException {
        if (latestEthBlockProcessedFile.exists()) {
            synchronized (this) {
                try (
                    FileInputStream latestEthBlockProcessedFileIs = new FileInputStream(latestEthBlockProcessedFile);
                    ObjectInputStream latestEthBlockProcessedObjectIs =
                            new ObjectInputStream(latestEthBlockProcessedFileIs);
                ) {
                    latestEthBlockProcessed = latestEthBlockProcessedObjectIs.readLong();
                }
            }
        }
    }

    private void flushLatestEthBlockProcessed() throws IOException {
        if (!dataDirectory.exists()) {
            if (!dataDirectory.mkdirs()) {
                throw new IOException("Could not create directory " + dataDirectory.getAbsolutePath());
            }
        }
        try (
            FileOutputStream latestEthBlockProcessedFileOs = new FileOutputStream(latestEthBlockProcessedFile);
            ObjectOutputStream latestEthBlockProcessedObjectOs = new ObjectOutputStream(latestEthBlockProcessedFileOs);
        ) {
            latestEthBlockProcessedObjectOs.writeLong(latestEthBlockProcessed);
        }
    }
}

// TODO: move to another class