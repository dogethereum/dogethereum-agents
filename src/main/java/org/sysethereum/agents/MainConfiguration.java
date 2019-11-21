package org.sysethereum.agents;

import com.google.gson.Gson;

import javax.net.ssl.*;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Context;
import org.bitcoinj.script.Script;
import org.simplejavamail.mailer.Mailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sysethereum.agents.constants.*;
import org.sysethereum.agents.contract.SyscoinBattleManagerExtended;
import org.sysethereum.agents.contract.SyscoinClaimManager;
import org.sysethereum.agents.contract.SyscoinClaimManagerExtended;
import org.sysethereum.agents.contract.SyscoinSuperblocksExtended;
import org.sysethereum.agents.core.syscoin.SyscoinWalletAppKit;
import org.sysethereum.agents.service.MailerFactory;
import org.sysethereum.agents.service.rest.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ClientTransactionManager;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.concurrent.Executors;

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
    public SystemProperties config(SystemPropertiesFactory systemPropertiesFactory) {
        return systemPropertiesFactory.create();
    }

    @Nullable
    @Bean
    public Mailer mailer(MailerFactory mailerFactory) {
        return mailerFactory.create();
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
            case ETH_SYSCOINMAIN:
                result = new MainnetAgentConstantsFactory().create();
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
    public EthAddresses ethAddresses(SystemProperties config) {
        return new EthAddresses(config.generalPurposeAndSendSuperblocksAddress(), config.syscoinSuperblockChallengerAddress());
    }

    @Bean
    public BigInteger superblockDuration(SyscoinBattleManagerExtended battleManager) {
        try {
            Uint256 send = battleManager.superblockDuration().send();
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
        String contractAddress = SyscoinClaimManager.getPreviouslyDeployedAddress(agentConstants.getNetworkId());

        var result = new SyscoinClaimManagerExtended(contractAddress, web3,
                new ClientTransactionManager(web3, ethAddresses.generalPurposeAddress),
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
        String contractAddress = SyscoinClaimManager.getPreviouslyDeployedAddress(agentConstants.getNetworkId());

        var result = new SyscoinClaimManagerExtended(contractAddress, web3,
                new ClientTransactionManager(web3, ethAddresses.challengerAddress),
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

        var result = new SyscoinBattleManagerExtended(contractAddress, web3,
                new ClientTransactionManager(web3, ethAddresses.generalPurposeAddress),
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

        var result = new SyscoinBattleManagerExtended(contractAddress, web3,
                new ClientTransactionManager(web3, ethAddresses.challengerAddress),
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

        var result = new SyscoinSuperblocksExtended(contractAddress, web3,
                new ClientTransactionManager(web3, ethAddresses.generalPurposeAddress),
                BigInteger.valueOf(config.gasPriceMinimum()),
                BigInteger.valueOf(config.gasLimit())
        );
        assert result.isValid();
        return result;
    }


    @Bean
    public BigInteger superblockDelay(SyscoinClaimManagerExtended claimManager) throws Exception {
        return claimManager.superblockDelay().send().getValue();
    }

    @Bean
    public BigInteger superblockTimeout(SyscoinClaimManagerExtended claimManager) throws Exception {
        return claimManager.superblockTimeout().send().getValue();
    }

    @Bean
    public BigInteger minProposalDeposit(SyscoinClaimManagerExtended claimManager) throws Exception {
        return claimManager.minProposalDeposit().send().getValue();
    }

    @Bean
    public HttpsServer httpsServer(
            SystemProperties config,
            GetSPVHandler getSPVHandler,
            GetSuperblockBySyscoinHandler getSuperblockBySyscoinHandler,
            GetSuperblockHandler getSuperblockHandler,
            GetSyscoinRPCHandler getSyscoinRPCHandler,
            InfoHandler infoHandler
    ) throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException {
        HttpsServer httpsServer  = HttpsServer.create(new InetSocketAddress(8443), 0);
        if(!config.isServerEnabled())
            return httpsServer;


        SSLContext sslContext = SSLContext.getInstance("TLS");

        // initialise the keystore
        char[] password = config.sslFilePassword().toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        FileInputStream fis = new FileInputStream(config.sslFile());
        ks.load(fis, password);

        // setup the key manager factory
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, password);

        // setup the trust manager factory
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        // setup the HTTPS context and parameters
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
            public void configure(HttpsParameters params) {
                try {
                    // initialise the SSL context
                    SSLContext context = getSSLContext();
                    SSLEngine engine = context.createSSLEngine();
                    params.setNeedClientAuth(false);
                    params.setCipherSuites(engine.getEnabledCipherSuites());
                    params.setProtocols(engine.getEnabledProtocols());

                    // Set the SSL parameters
                    SSLParameters sslParameters = context.getSupportedSSLParameters();
                    params.setSSLParameters(sslParameters);

                } catch (Exception ex) {
                    System.out.println("Failed to create HTTPS port");
                }
            }
        });


        httpsServer.createContext("/", infoHandler);
        httpsServer.createContext("/spvproof", getSPVHandler);
        httpsServer.createContext("/superblockbysyscoinblock", getSuperblockBySyscoinHandler);
        httpsServer.createContext("/superblock", getSuperblockHandler);
        httpsServer.createContext("/syscoinrpc", getSyscoinRPCHandler);
        httpsServer.setExecutor(Executors.newFixedThreadPool(256));
        return httpsServer;
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
