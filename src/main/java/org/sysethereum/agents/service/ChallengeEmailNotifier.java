package org.sysethereum.agents.service;

import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.sysethereum.agents.constants.SystemProperties;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;

import javax.annotation.Nullable;

import static java.util.stream.Collectors.joining;

@Service
public class ChallengeEmailNotifier {

    private static final Logger logger = LoggerFactory.getLogger("ChallengeEmailNotifier");

    private final SystemProperties config;

    @Nullable
    private final Mailer mailer;

    public ChallengeEmailNotifier(SystemProperties config, @Nullable Mailer mailer) {
        this.config = config;
        this.mailer = mailer;
    }

    /**
     * Send a notification email about superblock challenge event
     *
     * @param report list of superblock hashes that will be challenged
     * @return true if mailer ATTEMPTS to send an email
     */
    public boolean sendIfEnabled(ChallengeReport report) {
        if (config.getBooleanProperty("agent.mailer.enabled", false)) {
            assert mailer != null;

            String trigger = config.getStringProperty("agent.mailer.challenge.notifier.trigger");
            boolean proceed = (report.isAtLeastOneMine  && "MY_BLOCK".equals(trigger)) || "ANY_BLOCK".equals(trigger);

            if (proceed) {
                String toAddress = config.getStringProperty("agent.mailer.challenge.notifier.to");
                String body = "Some superblocks will be challenged: " + report.challenged.stream().map(Keccak256Hash::toString).collect(joining(", "));

                logger.info("sendIfEnabled: Send notification email (toAddress:{}, body:{})", toAddress, body);
                Email email = EmailBuilder.startingBlank()
                        .from(config.getStringProperty("agent.mailer.challenge.notifier.from"))
                        .to(toAddress)
                        .withSubject(config.getStringProperty("agent.mailer.challenge.notifier.subject"))
                        .withPlainText(body)
                        .buildEmail();

                mailer.sendMail(email);
            }
            return proceed;
        }

        return false;
    }

}
