package org.sysethereum.agents;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Context;
import org.bitcoinj.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sysethereum.agents.constants.*;
import org.sysethereum.agents.contract.SyscoinBattleManagerExtended;
import org.sysethereum.agents.contract.SyscoinClaimManagerExtended;
import org.sysethereum.agents.contract.SyscoinSuperblocksExtended;
import org.sysethereum.agents.core.syscoin.SyscoinWalletAppKit;
import org.sysethereum.agents.service.rest.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ClientTransactionManager;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.List;

import static org.sysethereum.agents.constants.SystemProperties.*;

@Configuration
@Slf4j(topic = "MainConfiguration")
public class MainConfiguration {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger("MainConfiguration");

    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    public SystemProperties systemProperties() {
        return new SystemProperties();
    }

    @Bean
    public AgentConstants getAgentConstants(SystemProperties systemProperties) {
        String constants = systemProperties.constants();

        AgentConstants result;

        switch (constants) {
            case INTEGRATION:
                result = new IntegrationAgentConstantsFactory().create();
                break;
            case LOCAL:
                result = new LocalAgentConstantsFactory().create();
                break;
            case ETHGANACHE_SYSCOINMAIN:
                result = new EthGanacheSyscoinMainAgentConstantsFactory().create();
                break;
            default:
                throw new RuntimeException("Unknown value for 'constants': '" + constants + "'");
        }

        return result;
    }

    @Bean
    public Web3jService mainWeb3jService(SystemProperties config) {
        String url = config.getStringProperty("geth.rpc.url_and_port");
        logger.debug("mainWeb3jService: Set to: {}", url);
        return new HttpService(url);
    }

    @Bean
    public Web3jService web3jSecondaryService(SystemProperties config) {
        return new HttpService(config.secondaryURL());
    }

    @Bean
    public Web3j web3(Web3jService mainWeb3jService) {
        return Web3j.build(mainWeb3jService);
    }

    @Bean
    public Web3j web3Secondary(Web3jService web3jSecondaryService) {
        return Web3j.build(web3jSecondaryService);
    }

    @Bean
    public EthAddresses ethAddresses(SystemProperties config, Web3j web3) throws IOException {
        if (config.isGanache()) {
            List<String> accounts = web3.ethAccounts().send().getAccounts();
            return new EthAddresses(accounts.get(0), accounts.get(1));
        } else {
            return new EthAddresses(config.generalPurposeAndSendSuperblocksAddress(), config.syscoinSuperblockChallengerAddress());
        }
    }

    @Bean
    public BigInteger superblockDuration(SyscoinBattleManagerExtended battleManagerGetter) {
        try {
            Uint256 send = battleManagerGetter.superblockDuration().send();
            return send.getValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public SyscoinClaimManagerExtended claimManager(
            SystemProperties config, AgentConstants agentConstants,
            Web3j web3, EthAddresses ethAddresses
    ) throws IOException {
        String contractAddress = SyscoinClaimManagerExtended.getAddress(agentConstants.getNetworkId());

        var result = SyscoinClaimManagerExtended.load(contractAddress, web3,
                new ClientTransactionManager(web3, ethAddresses.generalPurposeAndSendSuperblocksAddress),
                BigInteger.valueOf(config.gasPriceMinimum()),
                BigInteger.valueOf(config.gasLimit())
        );
        assert result.isValid();
        return result;
    }

    @Bean
    public SyscoinClaimManagerExtended claimManagerForChallenges(
            SystemProperties config, AgentConstants agentConstants,
            Web3j web3, EthAddresses ethAddresses
    ) throws IOException {
        String contractAddress = SyscoinClaimManagerExtended.getAddress(agentConstants.getNetworkId());

        var result = SyscoinClaimManagerExtended.load(contractAddress, web3,
                new ClientTransactionManager(web3, ethAddresses.syscoinSuperblockChallengerAddress),
                BigInteger.valueOf(config.gasPriceMinimum()),
                BigInteger.valueOf(config.gasLimit())
        );
        assert result.isValid();
        return result;
    }

    @Bean
    public SyscoinClaimManagerExtended claimManagerForChallengesGetter(
            SystemProperties config, AgentConstants agentConstants,
            Web3j web3Secondary, EthAddresses ethAddresses
    ) throws IOException {
        String contractAddress = SyscoinClaimManagerExtended.getAddress(agentConstants.getNetworkId());

        var result = SyscoinClaimManagerExtended.load(contractAddress, web3Secondary,
                new ClientTransactionManager(web3Secondary, ethAddresses.syscoinSuperblockChallengerAddress),
                BigInteger.valueOf(config.gasPriceMinimum()),
                BigInteger.valueOf(config.gasLimit())
        );
        assert result.isValid();
        return result;
    }

    @Bean
    public SyscoinClaimManagerExtended claimManagerGetter(
            SystemProperties config, AgentConstants agentConstants,
            Web3j web3Secondary, EthAddresses ethAddresses
    ) throws IOException {
        String contractAddress = SyscoinClaimManagerExtended.getAddress(agentConstants.getNetworkId());

        var result = SyscoinClaimManagerExtended.load(contractAddress, web3Secondary,
                new ClientTransactionManager(web3Secondary, ethAddresses.generalPurposeAndSendSuperblocksAddress),
                BigInteger.valueOf(config.gasPriceMinimum()),
                BigInteger.valueOf(config.gasLimit())
        );
        assert result.isValid();
        return result;
    }

    @Bean
    public SyscoinBattleManagerExtended battleManager(
            SystemProperties config, AgentConstants agentConstants,
            Web3j web3, EthAddresses ethAddresses
    ) throws IOException {
        String contractAddress = SyscoinBattleManagerExtended.getAddress(agentConstants.getNetworkId());

        var result = SyscoinBattleManagerExtended.load(contractAddress, web3,
                new ClientTransactionManager(web3, ethAddresses.generalPurposeAndSendSuperblocksAddress),
                BigInteger.valueOf(config.gasPriceMinimum()),
                BigInteger.valueOf(config.gasLimit())
        );
        assert result.isValid();
        return result;
    }

    @Bean
    public SyscoinBattleManagerExtended battleManagerGetter(
            SystemProperties config, AgentConstants agentConstants,
            Web3j web3Secondary, EthAddresses ethAddresses
    ) throws IOException {
        String contractAddress = SyscoinBattleManagerExtended.getAddress(agentConstants.getNetworkId());

        var result = SyscoinBattleManagerExtended.load(contractAddress, web3Secondary,
                new ClientTransactionManager(web3Secondary, ethAddresses.generalPurposeAndSendSuperblocksAddress),
                BigInteger.valueOf(config.gasPriceMinimum()),
                BigInteger.valueOf(config.gasLimit())
        );
        assert result.isValid();
        return result;
    }

    @Bean
    public SyscoinBattleManagerExtended battleManagerForChallenges(
            SystemProperties config, AgentConstants agentConstants,
            Web3j web3, EthAddresses ethAddresses
    ) throws IOException {
        String contractAddress = SyscoinBattleManagerExtended.getAddress(agentConstants.getNetworkId());

        var result = SyscoinBattleManagerExtended.load(contractAddress, web3,
                new ClientTransactionManager(web3, ethAddresses.syscoinSuperblockChallengerAddress),
                BigInteger.valueOf(config.gasPriceMinimum()),
                BigInteger.valueOf(config.gasLimit())
        );
        assert result.isValid();
        return result;
    }

    @Bean
    public SyscoinBattleManagerExtended battleManagerForChallengesGetter(
            SystemProperties config, AgentConstants agentConstants,
            Web3j web3Secondary, EthAddresses ethAddresses
    ) throws IOException {
        String contractAddress = SyscoinBattleManagerExtended.getAddress(agentConstants.getNetworkId());

        var result = SyscoinBattleManagerExtended.load(contractAddress, web3Secondary,
                new ClientTransactionManager(web3Secondary, ethAddresses.syscoinSuperblockChallengerAddress),
                BigInteger.valueOf(config.gasPriceMinimum()),
                BigInteger.valueOf(config.gasLimit())
        );
        assert result.isValid();
        return result;
    }

    @Bean
    public SyscoinSuperblocksExtended superblocks(
            SystemProperties config, AgentConstants agentConstants,
            Web3j web3, EthAddresses ethAddresses
    ) throws IOException {
        String contractAddress = SyscoinSuperblocksExtended.getAddress(agentConstants.getNetworkId());

        var result = SyscoinSuperblocksExtended.load(contractAddress, web3,
                new ClientTransactionManager(web3, ethAddresses.generalPurposeAndSendSuperblocksAddress),
                BigInteger.valueOf(config.gasPriceMinimum()),
                BigInteger.valueOf(config.gasLimit())
        );
        assert result.isValid();
        return result;
    }

    @Bean
    public SyscoinSuperblocksExtended superblocksGetter(
            SystemProperties config, AgentConstants agentConstants,
            Web3j web3Secondary, EthAddresses ethAddresses
    ) throws IOException {
        String contractAddress = SyscoinSuperblocksExtended.getAddress(agentConstants.getNetworkId());

        var result = SyscoinSuperblocksExtended.load(contractAddress, web3Secondary,
                new ClientTransactionManager(web3Secondary, ethAddresses.generalPurposeAndSendSuperblocksAddress),
                BigInteger.valueOf(config.gasPriceMinimum()),
                BigInteger.valueOf(config.gasLimit())
        );
        assert result.isValid();
        return result;
    }

    @Bean
    public BigInteger superblockDelay(SyscoinClaimManagerExtended claimManagerGetter) throws Exception {
        return claimManagerGetter.superblockDelay().send().getValue();
    }

    @Bean
    public BigInteger superblockTimeout(SyscoinClaimManagerExtended claimManagerGetter) throws Exception {
        return claimManagerGetter.superblockTimeout().send().getValue();
    }

    @Bean
    public HttpServer httpServer(
            GetSPVHandler getSPVHandler,
            GetSuperblockBySyscoinHandler getSuperblockBySyscoinHandler,
            GetSuperblockHandler getSuperblockHandler,
            GetSyscoinRPCHandler getSyscoinRPCHandler,
            InfoHandler infoHandler
    ) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", infoHandler);
        server.createContext("/spvproof", getSPVHandler);
        server.createContext("/superblockbysyscoinblock", getSuperblockBySyscoinHandler);
        server.createContext("/superblock", getSuperblockHandler);
        server.createContext("/syscoinrpc", getSyscoinRPCHandler);
        server.setExecutor(null); // creates a default executor
        return server;
    }

    @Bean
    public Context syscoinContext(AgentConstants agentConstants) {
        return new Context(agentConstants.getSyscoinParams());
    }

    @Bean
    public SyscoinWalletAppKit syscoinWalletAppKit(SystemProperties config, Context syscoinContext) {
        File dataDirectory = new File(config.dataDirectory() + "/SyscoinWrapper");
        return new SyscoinWalletAppKit(syscoinContext, Script.ScriptType.P2WPKH, null, dataDirectory, "sysethereumAgentLibdohj");
    }
}
