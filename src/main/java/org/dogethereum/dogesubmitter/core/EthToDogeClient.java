package org.dogethereum.dogesubmitter.core;


import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.dogethereum.dogesubmitter.constants.BridgeConstants;
import org.dogethereum.dogesubmitter.constants.SystemProperties;
import org.dogethereum.dogesubmitter.util.OperatorKeyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.InetAddress;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Sends release txs on the doge network
 * @author Oscar Guindzberg
 */
@Service
@Slf4j(topic = "EthToDogeClient")
public class EthToDogeClient {

    @Autowired
    private FederatorSupport federatorSupport;

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
        if (config.isEthToDogeEnabled()) {
            this.dataDirectory = new File(config.dataDirectory());
            this.latestEthBlockProcessedFile = new File(dataDirectory.getAbsolutePath() + "/EthToDogeClientLatestEthBlockProcessedFile.dat");
            restoreLatestEthBlockProcessed();

            BridgeConstants bridgeConstants = config.getBridgeConstants();
            Context dogeContext = new Context(bridgeConstants.getDogeParams());
            peerGroup = new PeerGroup(dogeContext);
//        if (federatorSupport.getBitcoinPeerAddresses().size()>0) {
//            for (PeerAddress peerAddress : federatorSupport.getBitcoinPeerAddresses()) {
//                peerGroup.addAddress(peerAddress);
//            }
//            peerGroup.setMaxConnections(federatorSupport.getBitcoinPeerAddresses().size());
//        }
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
                if (!federatorSupport.isEthNodeSyncing()) {
                    long fromBlock = latestEthBlockProcessed + 1;
                    long toBlock = federatorSupport.getEthBlockCount() - config.getBridgeConstants().getEth2DogeMinimumAcceptableConfirmations();
                    // Ignore execution if nothing to process
                    if (fromBlock > toBlock) return;
                    List<Long> newUnlockRequestIds = federatorSupport.getNewUnlockRequestIds(fromBlock, toBlock);
                    for (Long unlockRequestId : newUnlockRequestIds) {
                        FederatorSupport.Unlock unlock = federatorSupport.getUnlock(unlockRequestId);
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

    private Transaction buildDogeTransaction(FederatorSupport.Unlock unlock) {
        ECKey operatorPrivateKey = keyHandler.getPrivateKey();

        NetworkParameters params = config.getBridgeConstants().getDogeParams();
        Transaction tx = new Transaction(params);
        long totalInputValue = 0;
        for (UTXO utxo : unlock.selectedUtxos) {
            totalInputValue += utxo.getValue().getValue();
        }
        tx.addOutput(Coin.valueOf(unlock.value), Address.fromBase58(params, unlock.dogeAddress));
        long change = totalInputValue - unlock.value - unlock.fee;
        if (change > 0) {
            tx.addOutput(Coin.valueOf(change), operatorPrivateKey.toAddress(params));
        }
        for (UTXO utxo : unlock.selectedUtxos) {
            TransactionOutPoint outPoint = new TransactionOutPoint(params, utxo.getIndex(), utxo.getHash());
            tx.addSignedInput(outPoint, keyHandler.getOutputScript(),  operatorPrivateKey);
        }
        return tx;
    }

    private boolean isMine(FederatorSupport.Unlock unlock) {
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

