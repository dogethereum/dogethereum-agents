/*
 * Copyright (C) 2017 RSK Labs Ltd.
 * Copyright (C) 2018 Coinfabrik and Oscar Guindzberg.
 * Copyright (C) 2019 Jagdeep Sidhu
 */
package org.sysethereum.agents.core.syscoin;


import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.script.Script;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.sysethereum.agents.constants.SystemProperties;
import org.sysethereum.agents.util.AgentUtils;
import org.sysethereum.agents.constants.AgentConstants;
import org.sysethereum.agents.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.*;

@Component
@Slf4j(topic = "SyscoinWrapper")
public class SyscoinWrapper {

    private static final Logger log = LoggerFactory.getLogger("LocalAgentConstants");
    SystemProperties config;
    private WalletAppKit kit;
    private Context syscoinContext;
    private AgentConstants agentConstants;
    private File dataDirectory;



    @Autowired
    public SyscoinWrapper() throws Exception {
        this.config = SystemProperties.CONFIG;
        if (config.isSyscoinSuperblockSubmitterEnabled() ||
                 config.isSyscoinBlockChallengerEnabled()) {
            this.agentConstants = config.getAgentConstants();
            this.syscoinContext = new Context(agentConstants.getSyscoinParams());
            this.dataDirectory = new File(config.dataDirectory() + "/SyscoinWrapper");
            setup();
            start();
        }
    }


    public void setup() {
        kit = new WalletAppKit(syscoinContext, Script.ScriptType.P2WPKH, null, dataDirectory, "sysethereumAgentLibdohj") {
            @Override
            protected void onSetupCompleted() {
                Context.propagate(syscoinContext);
                vPeerGroup.setDownloadTxDependencies(0);
            }



            @Override
            protected BlockStore provideBlockStore(File file) throws BlockStoreException {
                return new AltcoinLevelDBBlockStore(syscoinContext, getChainFile());
            }


            protected File getChainFile() {
                return new File(directory, "chain");
            }

        };

        // TODO: Make the syscoin peer list configurable
        // if (!peerAddresses.isEmpty()) {
        //    kit.setPeerNodes(peerAddresses.toArray(new PeerAddress[]{}));
        //}
        kit.connectToLocalHost();

        InputStream checkpoints = SyscoinWrapper.class.getResourceAsStream("/" + syscoinContext.getParams().getId() + ".checkpoints");
        if (checkpoints != null) {
            kit.setCheckpoints(checkpoints);
        }
    }

    public void start() {
        Context.propagate(syscoinContext);
        kit.startAsync().awaitRunning();
    }

    public void stop() {
        Context.propagate(syscoinContext);
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
    public StoredBlock getBlockByHeight(Sha256Hash hash, int height) throws BlockStoreException {
        if(height < 0)
            height = 0;
        StoredBlock currentBlock = kit.store().get(hash);
        if(currentBlock == null)
            return null;
        while(true){
            if(currentBlock.getHeight() <= height)
                break;
            currentBlock = kit.store().get(currentBlock.getHeader().getPrevBlockHash());
            if(currentBlock == null)
                return null;
        }
        return currentBlock;
    }
    public StoredBlock getStoredBlockAtHeight(int height) throws BlockStoreException {
        return AgentUtils.getStoredBlockAtHeight(kit.store(), height);
    }




    @PreDestroy
    public void tearDown() throws BlockStoreException, IOException {
        if (config.isSyscoinSuperblockSubmitterEnabled() ||
                 config.isSyscoinBlockChallengerEnabled()) {
            log.info("SyscoinToEthClient tearDown starting...");
            stop();

            log.info("SyscoinToEthClient tearDown finished.");
        }
    }



    public void broadcastSyscoinTransaction(Transaction tx) {
        kit.peerGroup().broadcastTransaction(tx);
    }
}
