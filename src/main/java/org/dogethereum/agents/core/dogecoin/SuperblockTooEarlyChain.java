package org.dogethereum.agents.core.dogecoin;

import org.bitcoinj.core.Context;
import org.bitcoinj.store.BlockStoreException;
import org.dogethereum.agents.constants.AgentConstants;
import org.dogethereum.agents.constants.SystemProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

@Component
public class SuperblockTooEarlyChain extends SuperblockChain {

    @Autowired
    public SuperblockTooEarlyChain() throws Exception, BlockStoreException {
        super();
    }

    @PostConstruct
    private void setup() throws Exception, BlockStoreException {
        SystemProperties config = SystemProperties.CONFIG;
        AgentConstants agentConstants = config.getAgentConstants();
        Context context = new Context(agentConstants.getDogeParams());
        File directory = new File(config.dataDirectory());
        File chainFile = new File(directory.getAbsolutePath() + "/SuperblockTooEarlyChain");
        this.params = agentConstants.getDogeParams();
        this.superblockStorage = new SuperblockLevelDBBlockStore(context, chainFile, params);
        this.SUPERBLOCK_DURATION = 30;
        this.SUPERBLOCK_DELAY = 30;
    }
}
