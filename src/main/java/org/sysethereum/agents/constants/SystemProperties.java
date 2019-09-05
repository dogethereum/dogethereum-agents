package org.sysethereum.agents.constants;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Utility class to retrieve property values from the configuration file
 *
 * The properties are taken from different sources and merged in the following order
 * (the config option from the next source overrides option from previous):
 * - system property : each config entry might be altered via -D VM option
 * - config specified with the -Dsysethereum.agents.conf.file=[file.conf] VM option
 *
 * @author Roman Mandeleil
 * @since 22.05.2014
 */
@Slf4j(topic = "SystemProperties")
public class SystemProperties {
    private static final Logger logger = LoggerFactory.getLogger("SystemProperties");
    private static final String LOCAL = "local";
    private static final String INTEGRATION = "integration";
    private static final String ETHGANACHE_SYSCOINMAIN = "ethganachesyscoinmain";

    private static final String YES = "yes";
    private static final String NO = "no";

    public static final SystemProperties CONFIG = new SystemProperties();

    protected Config config;


    private String projectVersion = null;
    private String projectVersionModifier = null;
    private AgentConstants agentConstants;

    public SystemProperties() {
        try {
            Config javaSystemProperties = ConfigFactory.load("no-such-resource-only-system-props");
            String file = System.getProperty("sysethereum.agents.conf.file");
            Config cmdLineConfigFile = file != null ? ConfigFactory.parseFile(new File(file)) : ConfigFactory.empty();
            logger.info("Config ( {} ): user properties from -Dsysethereum.agents.conf.file file '{}'",
                    cmdLineConfigFile.entrySet().isEmpty() ? NO : YES, file);
            config = javaSystemProperties
                    .withFallback(cmdLineConfigFile);

            logger.debug("Config trace: " + config.root().render(ConfigRenderOptions.defaults().
                    setComments(false).setJson(false)));

            Properties props = new Properties();
            InputStream is = getClass().getResourceAsStream("/version.properties");
            props.load(is);
            this.projectVersion = props.getProperty("versionNumber");
            this.projectVersion = this.projectVersion.replaceAll("'", "");

            if (this.projectVersion == null) {
                this.projectVersion = "-.-.-";
            }

            this.projectVersionModifier = props.getProperty("modifier");
            this.projectVersionModifier = this.projectVersionModifier.replaceAll("\"", "");

        } catch (Exception e) {
            logger.error("Can't read config.", e);
            throw new RuntimeException(e);
        }
    }

    public Config getConfig() {
        return config;
    }

    /**
     * Puts a new config atop of existing stack making the options
     * in the supplied config overriding existing options
     * Once put this config can't be removed
     *
     * @param overrideOptions - atop config
     */
    public void overrideParams(Config overrideOptions) {
        config = overrideOptions.withFallback(config);
    }

    /**
     * Puts a new config atop of existing stack making the options
     * in the supplied config overriding existing options
     * Once put this config can't be removed
     *
     * @param keyValuePairs [name] [value] [name] [value] ...
     */
    public void overrideParams(String ... keyValuePairs) {
        if (keyValuePairs.length % 2 != 0) {
            throw new RuntimeException("Odd argument number");
        }

        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            map.put(keyValuePairs[i], keyValuePairs[i + 1]);
        }
        overrideParams(map);
    }

    /**
     * Puts a new config atop of existing stack making the options
     * in the supplied config overriding existing options
     * Once put this config can't be removed
     *
     * @param cliOptions -  command line options to take presidency
     */
    public void overrideParams(Map<String, String> cliOptions) {
        Config cliConf = ConfigFactory.parseMap(cliOptions);
        overrideParams(cliConf);
    }

    public <T> T getProperty(String propName, T defaultValue) {
        if (!config.hasPath(propName)) {
            return defaultValue;
        }

        String string = config.getString(propName);
        if (string.trim().isEmpty()) {
            return defaultValue;
        }

        return (T) config.getAnyRef(propName);
    }

    public AgentConstants getAgentConstants() {
        if (agentConstants == null) {
            String constants = constants();
            switch (constants) {
                case INTEGRATION:
                    agentConstants = IntegrationAgentConstants.getInstance();
                    break;
                case LOCAL:
                    agentConstants = LocalAgentConstants.getInstance();
                    break;
                case ETHGANACHE_SYSCOINMAIN:
                    agentConstants = EthGanacheSyscoinMainAgentConstants.getInstance();
                    break;
                default:
                    throw new RuntimeException("Unknown value for 'constants': '" + constants + "'");
            }
        }
        return agentConstants;
    }

    public boolean isSyscoinSuperblockSubmitterEnabled() {
        return getBooleanProperty("syscoin.superblock.submitter.enabled", false);
    }

    public boolean isSyscoinBlockChallengerEnabled() {
        return getBooleanProperty("syscoin.superblock.challenger.enabled", false);
    }

    public boolean isGanache() {
        return LOCAL.equals(constants()) || ETHGANACHE_SYSCOINMAIN.equals(constants());
    }

    public String constants() {
        return config.hasPath("constants") ? config.getString("constants") : null;
    }

    public String projectVersion() {
        return projectVersion;
    }

    public String projectVersionModifier() {
        return projectVersionModifier;
    }

    public String generalPurposeAndSendSuperblocksAddress() {
        return getStringProperty("general.purpose.and.send.superblocks.address", null);
    }
    public String generalPurposeAndSendSuperblocksUnlockPW(){
        return getStringProperty("general.purpose.and.send.superblocks.unlockpw", null);
    }

    public String syscoinSuperblockChallengerAddress() {
        return getStringProperty("syscoin.superblock.challenger.address", null);
    }
    public String syscoinSuperblockChallengerUnlockPW(){
        return getStringProperty("syscoin.superblock.challenger.unlockpw", null);
    }
    public String dataDirectory() {
        return getStringProperty("data.directory", null);
    }
    public String secondaryURL() {
        return getStringProperty("secondary.url", "https://mainnet.infura.io/v3/d178aecf49154b12be98e68e998cfb8d");
    }
    public String syscoinRPCUser() {
        return getStringProperty("syscoinrpc.user", "u");
    }
    public String syscoinRPCPassword() {
        return getStringProperty("syscoinrpc.password", "p");
    }
    public String syscoinRPCURL() {
        return getStringProperty("syscoinrpc.url_and_port", "http://localhost:8370/");
    }
    public long gasPriceMinimum() {
        return getLongProperty("gas.price.min", 0);
    }
    public long gasPriceMaximum() {
        return getLongProperty("gas.price.max",  0);
    }
    public long gasLimit() {
        return getLongProperty("gas.limit", 0);
    }

    public boolean isWithdrawFundsEnabled() {
        return getBooleanProperty("withdraw.funds.enabled", false);
    }

    public long depositedFundsLimit() {
        return getLongProperty("deposited.funds.limit", 0);
    }

    protected String getStringProperty(String propertyName, String defaultValue) {
        return config.hasPath(propertyName) ? config.getString(propertyName) : defaultValue;
    }
    protected long getLongProperty(String propertyName, long defaultValue) {
        return config.hasPath(propertyName) ? config.getLong(propertyName) : defaultValue;
    }
    protected boolean getBooleanProperty(String propertyName, boolean defaultValue) {
        return config.hasPath(propertyName) ? config.getBoolean(propertyName) : defaultValue;
    }

}
