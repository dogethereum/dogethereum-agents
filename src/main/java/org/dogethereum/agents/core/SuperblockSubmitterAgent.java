/*
 * Copyright (C) 2017 RSK Labs Ltd.
 * Copyright (C) 2018 Coinfabrik and Oscar Guindzberg.
 */
package org.dogethereum.agents.core;


import org.dogethereum.agents.core.dogecoin.*;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.store.BlockStoreException;
import org.dogethereum.agents.constants.AgentConstants;
import org.dogethereum.agents.constants.SystemProperties;
import org.dogethereum.agents.core.eth.EthWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Sends superblocks from the local superblockchain to the Dogethereum Contracts
 * @author Oscar Guindzberg
 * @author Catalina Juarros
 */
@Service
@Slf4j(topic = "SuperblockSubmitterAgent")
public class SuperblockSubmitterAgent {

    @Autowired
    private EthWrapper ethWrapper;

    private SystemProperties config;

    private AgentConstants agentConstants;

    @Autowired
    private Superblockchain superblockchain;

    public SuperblockSubmitterAgent() {}


    @PostConstruct
    public void setup() throws Exception {
        config = SystemProperties.CONFIG;
        if (config.isDogeSuperblockSubmitterEnabled()) {
            agentConstants = config.getAgentConstants();

            new Timer("SuperblockSubmitterAgent").scheduleAtFixedRate(new SuperblockSubmitterAgentTimerTask(),
                    getFirstExecutionDate(), agentConstants.getSuperblockSubmitterAgentTimerTaskPeriod());

        }
    }


    private Date getFirstExecutionDate() {
        Calendar firstExecution = Calendar.getInstance();
        return firstExecution.getTime();
    }


    @SuppressWarnings("unused")
    private class SuperblockSubmitterAgentTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (!ethWrapper.isEthNodeSyncing()) {
                    log.debug("SuperblockSubmitterAgentTimerTask");
                    ethWrapper.updateContractFacadesGasPrice();
                    updateBridgeSuperblockchain();
                } else {
                    log.warn("SuperblockSubmitterAgentTimerTask skipped because the eth node is syncing blocks");
                }
            } catch (Exception e) {

                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Updates bridge with all the superblocks that the agent has but the bridge doesn't.
     * @return Number of superblocks sent to the bridge.
     * @throws Exception
     */
    public long updateBridgeSuperblockchain() throws Exception {
        if (ethWrapper.arePendingTransactionsForSendSuperblocksAddress()) {
            log.debug("Skipping sending superblocks, there are pending transaction for the sender address.");
            return 0;
        }

        // Get the best superblock from the relay that is also in the main chain.
        List<byte[]> superblockLocator = ethWrapper.getSuperblockLocator();
        Superblock matchedSuperblock = getEarliestMatchingSuperblock(superblockLocator);

        checkNotNull(matchedSuperblock, "No best chain superblock found");
        log.debug("Matched superblock {}.", matchedSuperblock.getSuperblockId());

        // We found the superblock in the agent's best chain. Send the earliest superblock that the relay is missing.
        Superblock toSend = superblockchain.getFirstDescendant(matchedSuperblock.getSuperblockId());

        if (toSend == null) {
            log.debug("Bridge was just updated, no new superblocks to send. matchedSuperblock: {}.",
                    matchedSuperblock.getSuperblockId());
            return 0;
        }

        if (!superblockchain.sendingTimePassed(toSend)) {
            log.debug("Too early to send superblock {}, will try again in a few seconds.",
                    toSend.getSuperblockId());
            return 0;
        }

        if (ethWrapper.wasSuperblockAlreadySubmitted(toSend.getSuperblockId())) {
            log.debug("The contract already knows about the superblock, it won't be sent again: {}.",
                      toSend.getSuperblockId());
            return 0;
        }

        log.debug("First superblock missing in the bridge: {}.", toSend.getSuperblockId());
        ethWrapper.sendStoreSuperblock(toSend, ethWrapper.getGeneralPurposeAndSendSuperblocksAddress());
        log.debug("Invoked sendStoreSuperblocks with superblock {}.", toSend.getSuperblockId());

        return toSend.getSuperblockHeight();
    }

    /**
     * Helper method for updateBridgeSuperblockchain().
     * Gets the earliest superblock from the bridge's superblock locator
     * that was also found in the agent's main chain.
     * @param superblockLocator List of ancestors provided by the bridge.
     * @return Earliest matched block if it is found,
     *         null otherwise.
     * @throws BlockStoreException
     * @throws IOException
     */
    private Superblock getEarliestMatchingSuperblock(List<byte[]> superblockLocator)
            throws BlockStoreException, IOException {
        Superblock matchedSuperblock = null;

        for (int i = 0; i < superblockLocator.size(); i++) {
            Keccak256Hash superblockBridgeHash = Keccak256Hash.wrap(superblockLocator.get(i));
            Superblock bridgeSuperblock = superblockchain.getSuperblock(superblockBridgeHash);

            if (bridgeSuperblock == null)
                continue;

            Superblock bestRelaySuperblockInLocalChain =
                    superblockchain.getSuperblockByHeight(bridgeSuperblock.getSuperblockHeight());

            if (bridgeSuperblock.getSuperblockId().equals(bestRelaySuperblockInLocalChain.getSuperblockId())) {
                matchedSuperblock = bestRelaySuperblockInLocalChain;
                break;
            }
        }

        return matchedSuperblock;
    }

}

