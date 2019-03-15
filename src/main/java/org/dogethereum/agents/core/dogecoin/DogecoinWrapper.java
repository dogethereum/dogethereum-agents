/*
 * Copyright (C) 2017 RSK Labs Ltd.
 * Copyright (C) 2018 Coinfabrik and Oscar Guindzberg.
 */
package org.dogethereum.agents.core.dogecoin;


import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.script.Script;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.dogethereum.agents.constants.SystemProperties;
import org.dogethereum.agents.util.AgentUtils;
import org.dogethereum.agents.constants.AgentConstants;
import org.dogethereum.agents.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.*;

@Component
@Slf4j(topic = "DogecoinWrapper")
public class DogecoinWrapper {

    private static final Logger log = LoggerFactory.getLogger("LocalAgentConstants");
    SystemProperties config;
    private WalletAppKit kit;
    private Context dogeContext;
    private AgentConstants agentConstants;
    private File dataDirectory;



    @Autowired
    public DogecoinWrapper() throws Exception {
        this.config = SystemProperties.CONFIG;
        if (config.isDogeSuperblockSubmitterEnabled() ||
                 config.isDogeBlockChallengerEnabled()) {
            this.agentConstants = config.getAgentConstants();
            this.dogeContext = new Context(agentConstants.getDogeParams());
            this.dataDirectory = new File(config.dataDirectory() + "/DogecoinWrapper");
            setup();
            start();
        }
    }


    public void setup() {
        kit = new WalletAppKit(dogeContext, Script.ScriptType.P2WPKH, null, dataDirectory, "dogethereumAgentLibdohj") {
            @Override
            protected void onSetupCompleted() {
                Context.propagate(dogeContext);
                vPeerGroup.setDownloadTxDependencies(0);
            }



            @Override
            protected BlockStore provideBlockStore(File file) throws BlockStoreException {
                return new AltcoinLevelDBBlockStore(dogeContext, getChainFile());
            }


            protected File getChainFile() {
                return new File(directory, "chain");
            }

        };

        // TODO: Make the dogecoin peer list configurable
        // if (!peerAddresses.isEmpty()) {
        //    kit.setPeerNodes(peerAddresses.toArray(new PeerAddress[]{}));
        //}
        kit.connectToLocalHost();

        InputStream checkpoints = DogecoinWrapper.class.getResourceAsStream("/" + dogeContext.getParams().getId() + ".checkpoints");
        if (checkpoints != null) {
            kit.setCheckpoints(checkpoints);
        }
    }

    public void start() {
        Context.propagate(dogeContext);
        kit.startAsync().awaitRunning();
    }

    public void stop() {
        Context.propagate(dogeContext);
        kit.stopAsync().awaitTerminated();
    }

    public int getBestChainHeight() {
        return kit.chain().getBestChainHeight();
    }

    public StoredBlock getChainHead() {
        return kit.chain().getChainHead();
    }

    public StoredBlock getBlock(Sha256Hash hash) throws BlockStoreException {
        return kit.store().get(hash);
    }

    public StoredBlock getStoredBlockAtHeight(int height) throws BlockStoreException {
        return AgentUtils.getStoredBlockAtHeight(kit.store(), height);
    }




    @PreDestroy
    public void tearDown() throws BlockStoreException, IOException {
        if (config.isDogeSuperblockSubmitterEnabled() ||
                 config.isDogeBlockChallengerEnabled()) {
            log.info("DogeToEthClient tearDown starting...");
            stop();

            log.info("DogeToEthClient tearDown finished.");
        }
    }



    public void broadcastDogecoinTransaction(Transaction tx) {
        kit.peerGroup().broadcastTransaction(tx);
    }
}
