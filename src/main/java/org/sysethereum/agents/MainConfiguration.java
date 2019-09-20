package org.sysethereum.agents;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sysethereum.agents.constants.SystemProperties;

@Configuration
@Slf4j(topic = "MainConfiguration")
public class MainConfiguration {

    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    public SystemProperties systemProperties() {
        return new SystemProperties();
    }
}
