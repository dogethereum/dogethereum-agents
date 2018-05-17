package org.dogethereum.dogesubmitter;

import lombok.extern.slf4j.Slf4j;
import org.dogethereum.dogesubmitter.constants.SystemProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@Slf4j(topic = "Main")
public class Main {
    public static void main(String[] args) {
        SystemProperties config = SystemProperties.CONFIG;
        log.info("Running DogeSubmitter version: {}-{}", config.projectVersion(), config.projectVersionModifier());
        // Instantiate the spring context
        new AnnotationConfigApplicationContext(Main.class);
    }
}