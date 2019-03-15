package org.dogethereum.agents.core;


import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.dogethereum.agents.constants.SystemProperties;
import org.dogethereum.agents.core.dogecoin.DogecoinWrapper;
import org.dogethereum.agents.core.eth.EthWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

/**
 * Signs and broadcasts unlock txs on the doge network
 * @author Oscar Guindzberg
 * @author Catalina Juarros
 */
@Service
@Slf4j(topic = "EthToDogeClient")
public class EthToDogeClient extends PersistentFileStore {
    private static final Logger log = LoggerFactory.getLogger("LocalAgentConstants");
    @Autowired
    private EthWrapper ethWrapper;

    private SystemProperties config;

    private static long ETH_REQUIRED_CONFIRMATIONS = 5;

    private long latestEthBlockProcessed;
    private File latestEthBlockProcessedFile;

    @Autowired
    private DogecoinWrapper dogecoinWrapper;


    public EthToDogeClient() {}


    @PostConstruct
    public void setup() throws Exception {
        config = SystemProperties.CONFIG;
        // Set latestEthBlockProcessed to eth genesis block or eth checkpoint,
        // then read the latestEthBlockProcessed from file and overwrite it.
        this.latestEthBlockProcessed = config.getAgentConstants().getEthInitialCheckpoint();
        this.dataDirectory = new File(config.dataDirectory());
        setupFiles();
        restore(latestEthBlockProcessed, latestEthBlockProcessedFile);

        new Timer("Eth to Doge client").scheduleAtFixedRate(new UpdateEthToDogeTimerTask(), getFirstExecutionDate(), config.getAgentConstants().getEthToDogeTimerTaskPeriod());

    }

    private Date getFirstExecutionDate() {
        Calendar firstExecution = Calendar.getInstance();
        firstExecution.add(Calendar.SECOND, 25);
        return firstExecution.getTime();
    }

    private class UpdateEthToDogeTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (!ethWrapper.isEthNodeSyncing()) {
                    long fromBlock = latestEthBlockProcessed + 1;
                    long toBlock = ethWrapper.getEthBlockCount() - config.getAgentConstants().getUnlockConfirmations() + 1;
                    // Ignore execution if nothing to process
                    if (fromBlock > toBlock) return;
                    List<EthWrapper.UnlockRequestEvent> newUnlockRequestEvents = ethWrapper.getNewUnlockRequests(fromBlock, toBlock);
                    for (EthWrapper.UnlockRequestEvent unlockRequestEvent : newUnlockRequestEvents) {
                        /*if (isMine(unlockRequestEvent)) {
                            EthWrapper.Unlock unlock = ethWrapper.getUnlock(unlockRequestEvent.id);
                            Transaction tx = buildDogeTransaction(unlock);
                            dogecoinWrapper.broadcastDogecoinTransaction(tx);
                        }*/
                    }
                    latestEthBlockProcessed = toBlock;
                    flush(latestEthBlockProcessed, latestEthBlockProcessedFile);
                } else {
                    log.warn("UpdateEthToDogeTimerTask skipped because the eth node is syncing blocks");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }


    @Override
    void setupFiles() {
        this.latestEthBlockProcessedFile =
                new File(dataDirectory.getAbsolutePath() + "/EthToDogeClientLatestEthBlockProcessedFile.dat");
    }

}

