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

    public static void main(String[] args) throws Exception {
        var c = new AnnotationConfigApplicationContext(Main.class);
        c.registerShutdownHook();

        SystemProperties config = c.getBean(SystemProperties.class);
        logger.info("Running Sysethereum agents version: {}-{}", config.projectVersion(), config.projectVersionModifier());

        var lifecycle = c.getBean(MainLifecycle.class);
        lifecycle.initialize();
    }

}