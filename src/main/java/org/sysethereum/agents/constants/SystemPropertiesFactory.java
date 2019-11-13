package org.sysethereum.agents.constants;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import static org.sysethereum.agents.constants.SystemProperties.NO;
import static org.sysethereum.agents.constants.SystemProperties.YES;

@Service
public class SystemPropertiesFactory {

    private static final Logger logger = LoggerFactory.getLogger("SystemPropertiesFactory");

    public SystemProperties create() {
        try {
            Config javaSystemProperties = ConfigFactory.load("no-such-resource-only-system-props");
            String file = System.getProperty("sysethereum.agents.conf.file");
            Config cmdLineConfigFile = file != null ? ConfigFactory.parseFile(new File(file)) : ConfigFactory.empty();
            logger.info("Config ({}): user properties from -Dsysethereum.agents.conf.file file '{}'",
                    cmdLineConfigFile.entrySet().isEmpty() ? NO : YES, file);

            var config = javaSystemProperties.withFallback(cmdLineConfigFile);

            Properties props = new Properties();
            InputStream is = getClass().getResourceAsStream("/version.properties");
            props.load(is);

            var projectVersion = props.getProperty("versionNumber").replaceAll("'", "");
            var projectVersionModifier = props.getProperty("modifier").replaceAll("\"", "");

            return new SystemProperties(config, projectVersion, projectVersionModifier);
        } catch (Exception e) {
            logger.error("Can't read config.", e);
            throw new RuntimeException(e);
        }
    }

}
