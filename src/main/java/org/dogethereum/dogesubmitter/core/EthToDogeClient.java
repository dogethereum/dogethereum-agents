package org.dogethereum.dogesubmitter.core;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.dogethereum.dogesubmitter.constants.BridgeConstants;
import org.dogethereum.dogesubmitter.constants.SystemProperties;
import org.dogethereum.dogesubmitter.util.OperatorKeyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.InetAddress;
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

    private static long ETH_REQUIRED_CONFIRMATIONS = 100;

    private long latestEthBlockProcessed;

    private PeerGroup peerGroup;

    private OperatorKeyHandler keyHandler;

    public EthToDogeClient() {}


    @PostConstruct
    public void setup() throws Exception {
        config = SystemProperties.CONFIG;
        if (config.isEthToDogeEnabled()) {
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

            this.keyHandler = new OperatorKeyHandler(this.config.federatorPrivateKeyFilePath());
            new Timer("Eth to Doge client").scheduleAtFixedRate(new UpdateEthToDogeTimerTask(), Calendar.getInstance().getTime(), 30 * 1000);
        }
    }

    private class UpdateEthToDogeTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (!federatorSupport.isEthNodeSyncing()) {
                    long ethBlockCount = federatorSupport.getEthBlockCount();
                    long topBlock = ethBlockCount - ETH_REQUIRED_CONFIRMATIONS;
                    List<Long> newUnlockRequestIds = federatorSupport.getNewUnlockRequestIds(latestEthBlockProcessed, topBlock);
                    for (Long unlockRequestId : newUnlockRequestIds) {
                        FederatorSupport.Unlock unlock = federatorSupport.getUnlock(unlockRequestId);
                        if (isMine(unlock)) {
                            Transaction tx = buildDogeTransaction(unlock);
                            broadcastDogeTransaction(tx);
                        }
                    }
                    latestEthBlockProcessed = topBlock;
                } else {
                    log.warn("UpdateEthToDogeTimerTask skipped because the eth node is syncing blocks");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        private void broadcastDogeTransaction(Transaction tx) {
            peerGroup.broadcastTransaction(tx);
        }

        private Transaction buildDogeTransaction(FederatorSupport.Unlock unlock) {
            ECKey key = ECKey.fromPrivate(getOperatorPrivKeyBytes());

            NetworkParameters params = config.getBridgeConstants().getDogeParams();
            Transaction tx = new Transaction(params);
            tx.addOutput(Coin.valueOf(unlock.value), Address.fromBase58(params, unlock.dogeAddress));
            for (UTXO utxo : unlock.selectedUtxos) {
                TransactionOutPoint outPoint = new TransactionOutPoint(params, utxo.getIndex(), utxo.getHash());
                tx.addSignedInput(outPoint, config.getBridgeConstants().getFederationPubScript(),  key);
            }
            return tx;
        }

        private boolean isMine(FederatorSupport.Unlock unlock) {
            return true;
        }

    }

    public byte[] getOperatorPrivKeyBytes() {
        return keyHandler.privateKey();
    }

}

