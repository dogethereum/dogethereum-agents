package org.dogethereum.dogesubmitter.core;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.Transaction;
import org.dogethereum.dogesubmitter.constants.SystemProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
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


    public EthToDogeClient() {}


    @PostConstruct
    public void setup() throws Exception {
        config = SystemProperties.CONFIG;
        if (config.isEthToDogeClientEnabled()) {
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
                        Unlock unlock = federatorSupport.getUnlock(unlockRequestId);
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

    }


}

