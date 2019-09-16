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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.*;
import java.util.Arrays;

@Component
@Slf4j(topic = "SyscoinWrapper")
public class SyscoinWrapper {

    private static final Logger logger = LoggerFactory.getLogger("SyscoinWrapper");
    SystemProperties config;
    private WalletAppKit kit;
    private Context syscoinContext;
    private AgentConstants agentConstants;
    private File dataDirectory;

    @Autowired
    public SyscoinWrapper() {
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

    public StoredBlock getChainHead() {
        return kit.chain().getChainHead();
    }
    /**
     * Gets the median timestamp of the last 11 blocks
     */
    public long getMedianTimestamp(StoredBlock storedBlock) throws BlockStoreException {
        long[] timestamps = new long[11];
        int unused = 9;
        timestamps[10] = storedBlock.getHeader().getTimeSeconds();
        while (unused >= 0 && (storedBlock = storedBlock.getPrev(kit.store())) != null)
            timestamps[unused--] = storedBlock.getHeader().getTimeSeconds();

        Arrays.sort(timestamps, unused+1, 11);
        return timestamps[unused + (11-unused)/2];
    }

    public StoredBlock getBlock(Sha256Hash hash) throws BlockStoreException {
        return kit.store().get(hash);
    }

    public StoredBlock getStoredBlockAtHeight(int height) throws BlockStoreException {
        return AgentUtils.getStoredBlockAtHeight(kit.store(), height);
    }

    @PreDestroy
    public void tearDown() {
        if (config.isSyscoinSuperblockSubmitterEnabled() ||
                 config.isSyscoinBlockChallengerEnabled()) {
            logger.info("SyscoinToEthClient tearDown starting...");
            stop();

            logger.info("SyscoinToEthClient tearDown finished.");
        }
    }

}
