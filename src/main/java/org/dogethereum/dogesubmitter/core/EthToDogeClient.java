package org.dogethereum.dogesubmitter.core;


import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.dogethereum.dogesubmitter.constants.AgentConstants;
import org.dogethereum.dogesubmitter.constants.SystemProperties;
import org.dogethereum.dogesubmitter.util.OperatorKeyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Signs and broadcasts unlock txs on the doge network
 * @author Oscar Guindzberg
 */
@Service
@Slf4j(topic = "EthToDogeClient")
public class EthToDogeClient {

    @Autowired
    private AgentSupport agentSupport;

    private SystemProperties config;

    private static long ETH_REQUIRED_CONFIRMATIONS = 5;

    private long latestEthBlockProcessed;
    private File dataDirectory;
    private File latestEthBlockProcessedFile;

    private PeerGroup peerGroup;

    @Autowired
    private OperatorKeyHandler keyHandler;

    public EthToDogeClient() {}


    @PostConstruct
    public void setup() throws Exception {
        config = SystemProperties.CONFIG;
        if (config.isOperatorEnabled()) {
            // Set latestEthBlockProcessed to eth genesis block or eth checkpoint,
            // then read the latestEthBlockProcessed from file and overwrite it.
            this.latestEthBlockProcessed = config.getAgentConstants().getEthInitialCheckpoint();
            this.dataDirectory = new File(config.dataDirectory());
            this.latestEthBlockProcessedFile = new File(dataDirectory.getAbsolutePath() + "/EthToDogeClientLatestEthBlockProcessedFile.dat");
            restoreLatestEthBlockProcessed();

            AgentConstants agentConstants = config.getAgentConstants();
            Context dogeContext = new Context(agentConstants.getDogeParams());
            peerGroup = new PeerGroup(dogeContext);
//          TODO: Make the dogecoin peer list configurable
//          if (agentSupport.getDogecoinPeerAddresses().size()>0) {
//            for (PeerAddress peerAddress : agentSupport.getDogecoinPeerAddresses()) {
//                peerGroup.addAddress(peerAddress);
//            }
//            peerGroup.setMaxConnections(agentSupport.getDogecoinPeerAddresses().size());
//          }
            final InetAddress localHost = InetAddress.getLocalHost();
            peerGroup.addAddress(localHost);
            peerGroup.setMaxConnections(1);
            peerGroup.start();

            new Timer("Eth to Doge client").scheduleAtFixedRate(new UpdateEthToDogeTimerTask(), Calendar.getInstance().getTime(), 30 * 1000);
        }
    }

    private class UpdateEthToDogeTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (!agentSupport.isEthNodeSyncing()) {
                    long fromBlock = latestEthBlockProcessed + 1;
                    long toBlock = agentSupport.getEthBlockCount() - config.getAgentConstants().getEth2DogeMinimumAcceptableConfirmations();
                    // Ignore execution if nothing to process
                    if (fromBlock > toBlock) return;
                    List<Long> newUnlockRequestIds = agentSupport.getNewUnlockRequestIds(fromBlock, toBlock);
                    for (Long unlockRequestId : newUnlockRequestIds) {
                        AgentSupport.Unlock unlock = agentSupport.getUnlock(unlockRequestId);
                        if (isMine(unlock)) {
                            Transaction tx = buildDogeTransaction(unlock);
                            broadcastDogeTransaction(tx);
                        }
                    }
                    latestEthBlockProcessed = toBlock;
                    flushLatestEthBlockProcessed();
                } else {
                    log.warn("UpdateEthToDogeTimerTask skipped because the eth node is syncing blocks");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void broadcastDogeTransaction(Transaction tx) {
        peerGroup.broadcastTransaction(tx);
    }

    private Transaction buildDogeTransaction(AgentSupport.Unlock unlock) {
        ECKey operatorPrivateKey = keyHandler.getPrivateKey();

        NetworkParameters params = config.getAgentConstants().getDogeParams();
        Transaction tx = new Transaction(params);
        long totalInputValue = 0;
        for (UTXO utxo : unlock.selectedUtxos) {
            totalInputValue += utxo.getValue().getValue();
        }
        tx.addOutput(Coin.valueOf(unlock.value - unlock.fee), Address.fromBase58(params, unlock.dogeAddress));
        long change = totalInputValue - unlock.value;
        if (change > 0) {
            tx.addOutput(Coin.valueOf(change), operatorPrivateKey.toAddress(params));
        }
        for (UTXO utxo : unlock.selectedUtxos) {
            TransactionOutPoint outPoint = new TransactionOutPoint(params, utxo.getIndex(), utxo.getHash());
            tx.addSignedInput(outPoint, keyHandler.getOutputScript(),  operatorPrivateKey);
        }
        return tx;
    }

    private boolean isMine(AgentSupport.Unlock unlock) {
        return true;
    }

    private void restoreLatestEthBlockProcessed() throws IOException {
        if (latestEthBlockProcessedFile.exists()) {
            synchronized (this) {
                try (
                    FileInputStream latestEthBlockProcessedFileIs = new FileInputStream(latestEthBlockProcessedFile);
                    ObjectInputStream latestEthBlockProcessedObjectIs = new ObjectInputStream(latestEthBlockProcessedFileIs);
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

