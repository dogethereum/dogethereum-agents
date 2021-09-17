package org.dogethereum.agents.core;


import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.dogethereum.agents.constants.SystemProperties;
import org.dogethereum.agents.core.dogecoin.DogecoinWrapper;
import org.dogethereum.agents.core.eth.EthWrapper;
import org.dogethereum.agents.util.OperatorKeyHandler;
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

    @Autowired
    private EthWrapper ethWrapper;

    private SystemProperties config;

    private static long ETH_REQUIRED_CONFIRMATIONS = 5;

    private long latestEthBlockProcessed;
    private File latestEthBlockProcessedFile;

    @Autowired
    private DogecoinWrapper dogecoinWrapper;

    @Autowired
    private OperatorKeyHandler operatorKeyHandler;

    public EthToDogeClient() {}


    @PostConstruct
    public void setup() throws Exception {
        config = SystemProperties.CONFIG;
        if (config.isOperatorEnabled()) {
            // Set latestEthBlockProcessed to eth genesis block or eth checkpoint,
            // then read the latestEthBlockProcessed from file and overwrite it.
            this.latestEthBlockProcessed = config.getAgentConstants().getEthInitialCheckpoint();
            this.dataDirectory = new File(config.dataDirectory());
            setupFiles();
            restore(latestEthBlockProcessed, latestEthBlockProcessedFile);

            new Timer("Eth to Doge client").scheduleAtFixedRate(new UpdateEthToDogeTimerTask(), getFirstExecutionDate(), config.getAgentConstants().getEthToDogeTimerTaskPeriod());
        }
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
                        if (isMine(unlockRequestEvent)) {
                            EthWrapper.Unlock unlock = ethWrapper.getUnlock(unlockRequestEvent.id);
                            Transaction tx = buildDogeTransaction(unlock);
                            dogecoinWrapper.broadcastDogecoinTransaction(tx);
                        }
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

    private Transaction buildDogeTransaction(EthWrapper.Unlock unlock) {
        ECKey operatorPrivateKey = operatorKeyHandler.getPrivateKey();

        NetworkParameters params = config.getAgentConstants().getDogeParams();
        Transaction tx = new Transaction(params);
        long totalInputValue = 0;
        for (UTXO utxo : unlock.selectedUtxos) {
            totalInputValue += utxo.getValue().getValue();
        }
        long unlockValue = unlock.value - unlock.operatorFee;
        long userValue = unlockValue - unlock.dogeTxFee;
        tx.addOutput(Coin.valueOf(userValue), LegacyAddress.fromPubKeyHash(params, unlock.dogeAddress));
        long change = totalInputValue - unlockValue;
        if (change > 0) {
            tx.addOutput(Coin.valueOf(change), operatorKeyHandler.getAddress());
        }
        for (UTXO utxo : unlock.selectedUtxos) {
            TransactionOutPoint outPoint = new TransactionOutPoint(params, utxo.getIndex(), utxo.getHash());
            // ANYONECANPAY is used as a hack because we sign inputs as we add them.
            // TODO: Add all the inputs and then sign them and remove ANYONECANPAY usage
            tx.addSignedInput(outPoint, operatorKeyHandler.getOutputScript(), operatorPrivateKey, Transaction.SigHash.ALL, true);
        }
        return tx;
    }

    // Is the unlock request for this operator?
    private boolean isMine(EthWrapper.UnlockRequestEvent unlockRequestEvent) {
        return Arrays.equals(unlockRequestEvent.operatorPublicKeyHash, operatorKeyHandler.getPublicKeyHash());
    }

    @Override
    void setupFiles() {
        this.latestEthBlockProcessedFile =
                new File(dataDirectory.getAbsolutePath() + "/EthToDogeClientLatestEthBlockProcessedFile.dat");
    }

}

