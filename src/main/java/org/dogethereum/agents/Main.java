package org.dogethereum.agents;

import lombok.extern.slf4j.Slf4j;
import org.dogethereum.agents.constants.SystemProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@Slf4j(topic = "Main")
public class Main {
    public static void main(String[] args) {
        SystemProperties config = SystemProperties.CONFIG;
        log.info("Running Dogethereum agents version: {}-{}", config.projectVersion(), config.projectVersionModifier());
        // Instantiate the spring context
        new AnnotationConfigApplicationContext(Main.class);
    }
}