package org.dogethereum.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Sha256Hash;
import org.dogethereum.agents.constants.SystemProperties;
import org.dogethereum.agents.core.dogecoin.*;
import org.dogethereum.agents.core.eth.EthWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

/**
 * Base class to monitor the Ethereum blockchain for superblock-related events
 * @author Catalina Juarros
 * @author Ismael Bejarano
 */

@Slf4j(topic = "SuperblockDefenderClient")
public abstract class SuperblockClientBase {

    @Autowired
    protected DogecoinWrapper dogecoinWrapper;

    @Autowired
    protected EthWrapper ethWrapper;

    @Autowired
    protected SuperblockChain superblockChain;

    protected SystemProperties config;

    protected String clientName;

    protected long latestEthBlockProcessed;
    protected File dataDirectory;
    protected File latestEthBlockProcessedFile;

    public SuperblockClientBase(String clientName) {
        this.clientName = clientName;
        this.config = SystemProperties.CONFIG;
    }

    protected void setup() throws Exception {
        if (isEnabled()) {
            setupLatestEthBlockProcessed();

            restoreLatestEthBlockProcessed();

            setupTimer();
        }
    }

    private void setupTimer() {
       new Timer(clientName).scheduleAtFixedRate(new SuperblocksClientBaseTimerTask(),
                    Calendar.getInstance().getTime(), 15 * 1000);
    }

    private class SuperblocksClientBaseTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (!ethWrapper.isEthNodeSyncing()) {
                    ethWrapper.updateContractFacadesGasPrice();

                    task();

                    flushLatestEthBlockProcessed();
                } else {
                    log.warn("SuperblocksClientBaseTimerTask skipped because the eth node is syncing blocks");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public abstract void task();

    protected abstract Boolean isEnabled();

    protected abstract String getLastEthBlockProcessedFilename();

    /* ---- DATABASE METHODS ---- */

    // TODO: see if these should be moved to another file to avoid repeated code

    private void setupLatestEthBlockProcessed() throws IOException {
        this.latestEthBlockProcessed = config.getAgentConstants().getEthInitialCheckpoint();
        this.dataDirectory = new File(config.dataDirectory());
        this.latestEthBlockProcessedFile = new File(dataDirectory.getAbsolutePath() +
                "/" + getLastEthBlockProcessedFilename());
    }

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
