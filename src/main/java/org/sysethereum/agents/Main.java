package org.sysethereum.agents;

import lombok.extern.slf4j.Slf4j;
import org.sysethereum.agents.constants.SystemProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@Slf4j(topic = "Main")
public class Main {
    private static final Logger logger = LoggerFactory.getLogger("Main");

    public static void main(String[] args) {
        SystemProperties config = SystemProperties.CONFIG;
        logger.info("Running Sysethereum agents version: {}-{}", config.projectVersion(), config.projectVersionModifier());
        // Instantiate the spring context
        new AnnotationConfigApplicationContext(Main.class);
    }
}