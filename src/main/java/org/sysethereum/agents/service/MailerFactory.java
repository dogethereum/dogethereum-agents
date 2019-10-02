package org.sysethereum.agents.service;

import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.sysethereum.agents.constants.SystemProperties;

import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

@Service
public class MailerFactory {

    private static final Logger logger = LoggerFactory.getLogger("MailerFactory");
    private final SystemProperties config;

    public MailerFactory(SystemProperties config) {
        this.config = config;
    }

    @Nullable
    public Mailer create() {
        if (!config.getBooleanProperty("agent.mailer.enabled", false)) {
            logger.info("Mailer is not enabled");
            return null;
        } else {
            var mailer = MailerBuilder
                    .withSMTPServer(
                            config.getStringProperty("agent.mailer.smtp.host"),
                            config.getIntProperty("agent.mailer.smtp.port"),
                            config.getStringProperty("agent.mailer.smtp.username"),
                            config.getStringProperty("agent.mailer.smtp.password")
                    )
                    .withTransportStrategy(TransportStrategy.valueOf(config.getStringProperty("agent.mailer.smtp.transportStrategy")))
                    .buildMailer();

            logger.info("Mailer settings: {}", requireNonNull(mailer.getServerConfig()));
            return mailer;
        }
    }
}
