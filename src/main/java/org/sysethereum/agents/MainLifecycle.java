package org.sysethereum.agents;

import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.test.util.ReflectionTestUtils;
import org.sysethereum.agents.checker.OperatorPeersChecker;
import org.sysethereum.agents.constants.SystemProperties;
import org.sysethereum.agents.core.*;
import org.sysethereum.agents.core.syscoin.SuperblockLevelDBBlockStore;
import org.sysethereum.agents.core.syscoin.SyscoinWrapper;
import org.sysethereum.agents.util.JsonGasRanges;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.math.BigInteger;

import static org.sysethereum.agents.constants.AgentRole.CHALLENGER;
import static org.sysethereum.agents.constants.AgentRole.SUBMITTER;

@Service
public class MainLifecycle {

    private static final Logger logger = LoggerFactory.getLogger("MainLifecycle");

    private final SystemProperties config;
    private final OperatorPeersChecker operatorPeersChecker;
    private final SyscoinWrapper syscoinWrapper;
    private final SuperblockChainClient sysSuperblockChainClient;
    private final SyscoinToEthClient syscoinToEthClient;
    private final RestServer restServer;
    private final SuperblockChallengerClient superblockChallengerClient;
    private final SuperblockDefenderClient superblockDefenderClient;
    private final Web3j web3;
    private final Web3j web3Secondary;
    private final Web3jService mainWeb3jService;
    private final Web3jService web3jSecondaryService;
    private final SuperblockLevelDBBlockStore superblockLevelDBBlockStore;
    private final JsonGasRanges jsonGasRanges;
    public MainLifecycle(
            SystemProperties config,
            OperatorPeersChecker operatorPeersChecker,
            SyscoinWrapper syscoinWrapper,
            SuperblockChainClient sysSuperblockChainClient,
            SyscoinToEthClient syscoinToEthClient,
            RestServer restServer,
            SuperblockChallengerClient superblockChallengerClient,
            SuperblockDefenderClient superblockDefenderClient,
            Web3j web3,
            Web3j web3Secondary,
            Web3jService mainWeb3jService,
            Web3jService web3jSecondaryService,
            SuperblockLevelDBBlockStore superblockLevelDBBlockStore,
            JsonGasRanges jsonGasRanges
    ) {
        this.config = config;
        this.operatorPeersChecker = operatorPeersChecker;
        this.syscoinWrapper = syscoinWrapper;
        this.sysSuperblockChainClient = sysSuperblockChainClient;
        this.syscoinToEthClient = syscoinToEthClient;
        this.restServer = restServer;
        this.superblockChallengerClient = superblockChallengerClient;
        this.superblockDefenderClient = superblockDefenderClient;
        this.web3 = web3;
        this.web3Secondary = web3Secondary;
        this.mainWeb3jService = mainWeb3jService;
        this.web3jSecondaryService = web3jSecondaryService;
        this.superblockLevelDBBlockStore = superblockLevelDBBlockStore;
        this.jsonGasRanges = jsonGasRanges;
    }

    public void initialize() throws Exception {
        logger.debug("initialize: [Step #1]");

        operatorPeersChecker.setup();

        if (config.isAgentRoleEnabled(CHALLENGER) || config.isAgentRoleEnabled(SUBMITTER)) {
            logger.debug("initialize: [Optional step] Start Syscoin wrapper");
            syscoinWrapper.setupAndStart();
        }

        logger.debug("initialize: [Step #2]");
        adminResult();

        logger.debug("initialize: [Step #3]");
        if (!sysSuperblockChainClient.setup()) return;

        logger.debug("initialize: [Step #4]");
        if (config.isAgentRoleEnabled(SUBMITTER)) {
            if (!syscoinToEthClient.setup()) return;
        }

        logger.debug("initialize: [Step #5]");
        if (config.isAgentRoleEnabled(CHALLENGER)) {
            if (!superblockChallengerClient.setup()) return;
        }

        logger.debug("initialize: [Step #6]");
        if (config.isAgentRoleEnabled(SUBMITTER)) {
            if (!superblockDefenderClient.setup()) return;
        }

        restServer.start();
        jsonGasRanges.setup();
        logger.debug("initialize: Done");
    }

    public void adminResult() throws IOException {
        try {
            Admin admin = Admin.build(mainWeb3jService);
            String generalAddress = config.generalPurposeAndSendSuperblocksAddress();
            if (generalAddress.length() > 0) {
                PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount(generalAddress, config.generalPurposeAndSendSuperblocksUnlockPW(), BigInteger.ZERO).send();
                if (personalUnlockAccount != null && personalUnlockAccount.getResult() != null && personalUnlockAccount.accountUnlocked()) {
                    logger.info("general.purpose.and.send.superblocks.address is unlocked and ready to use!");
                } else {
                    logger.warn("general.purpose.and.send.superblocks.address could not be unlocked, please check the password you set in the configuration file");
                }
            }
            String challengerAddress = config.syscoinSuperblockChallengerAddress();
            if (challengerAddress.length() > 0 && !generalAddress.equalsIgnoreCase(challengerAddress)) {
                PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount(challengerAddress, config.syscoinSuperblockChallengerUnlockPW(), BigInteger.ZERO).send();
                if (personalUnlockAccount != null && personalUnlockAccount.getResult() != null && personalUnlockAccount.accountUnlocked()) {
                    logger.info("syscoin.superblock.challenger.address is unlocked and ready to use!");
                } else {
                    logger.warn("syscoin.superblock.challenger.address could not be unlocked, please check the password you set in the configuration file");
                }
            }
            admin.shutdown();
        } catch (IllegalStateException e) {
            logger.error("AdminResult: Failed", e);
        }
    }

    @PreDestroy
    public void cleanUp() {
        Thread.currentThread().setName("spring-pre-destroy-thread");
        logger.debug("cleanUp: Free resources");

        if (config.isAgentRoleEnabled(CHALLENGER)) {
            try {
                superblockChallengerClient.cleanUp();
            } catch (IOException e) {
                logger.debug("cleanUp: superblockChallengerClient.cleanUp() failed", e);
            }
        }

        if (config.isAgentRoleEnabled(SUBMITTER)) {
            try {
                superblockDefenderClient.cleanUp();
            } catch (IOException e) {
                logger.debug("cleanUp: superblockDefenderClient.cleanUp() failed", e);
            }
        }

        if (config.isAgentRoleEnabled(SUBMITTER)) {
            syscoinToEthClient.cleanUp();
        }

        sysSuperblockChainClient.cleanUp();

        restServer.stop();

        if (config.isAgentRoleEnabled(CHALLENGER) || config.isAgentRoleEnabled(SUBMITTER)) {
            syscoinWrapper.stop();
        }

        superblockLevelDBBlockStore.close();

        web3.shutdown();
        web3Secondary.shutdown();

        var okHttpClient = (OkHttpClient) ReflectionTestUtils.getField(web3jSecondaryService, "httpClient");

        if (okHttpClient != null) {
            logger.debug("cleanUp: Shutdown okHttpClient");
            okHttpClient.dispatcher().executorService().shutdown();
            okHttpClient.connectionPool().evictAll();
        }
        jsonGasRanges.cleanUp();
        logger.debug("cleanUp: Done");
    }

}
