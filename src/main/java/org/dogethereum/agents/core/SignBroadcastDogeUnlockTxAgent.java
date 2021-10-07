package org.dogethereum.agents.core;


import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
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
@Slf4j(topic = "SignBroadcastDogeUnlockTxAgent")
public class SignBroadcastDogeUnlockTxAgent extends PersistentFileStore {

    @Autowired
    private EthWrapper ethWrapper;

    @Autowired
    private DogecoinWrapper dogecoinWrapper;

    @Autowired
    private OperatorKeyHandler operatorKeyHandler;

    public SignBroadcastDogeUnlockTxAgent() {}


    @PostConstruct
    public void setup() throws Exception {
        super.setup();
        if (isEnabled()) {
            new Timer("SignBroadcastDogeUnlockTxAgent").scheduleAtFixedRate(new SignBroadcastDogeUnlockTxAgentTimerTask(), getFirstExecutionDate(), config.getAgentConstants().getSignBroadcastDogeUnlockTxAgentTimerTaskPeriod());
        }
    }

    private Date getFirstExecutionDate() {
        Calendar firstExecution = Calendar.getInstance();
        firstExecution.add(Calendar.SECOND, 25);
        return firstExecution.getTime();
    }

    private class SignBroadcastDogeUnlockTxAgentTimerTask extends TimerTask {
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
                    log.warn("SignBroadcastDogeUnlockTxAgentTimerTask skipped because the eth node is syncing blocks");
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
        tx.addOutput(Coin.valueOf(unlock.valueToUser), LegacyAddress.fromPubKeyHash(params, unlock.dogeAddress));
        long change = unlock.operatorChange;
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
    protected boolean isEnabled() {
        return config.isOperatorEnabled();
    }


    @Override
    protected String getLatestEthBlockProcessedFilename() {
        return "SignBroadcastDogeUnlockTxAgentLatestEthBlockProcessed.dat";
    }

}

