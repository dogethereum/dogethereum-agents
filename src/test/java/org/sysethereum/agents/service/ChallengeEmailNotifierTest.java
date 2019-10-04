package org.sysethereum.agents.service;

import org.junit.jupiter.api.Test;
import org.simplejavamail.MailException;
import org.simplejavamail.email.Email;
import org.simplejavamail.mailer.Mailer;
import org.sysethereum.agents.constants.SystemProperties;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;

import javax.mail.AuthenticationFailedException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

class ChallengeEmailNotifierTest {

    @Test
    void sendIfEnabled_enabled() {
        var config = SystemProperties.forTest(getClass().getResourceAsStream("challenge.notifier.enabled.conf"));
        var mailer = mock(Mailer.class);

        var underTest = new ChallengeEmailNotifier(config, mailer);
        underTest.sendIfEnabled(new ChallengeReport(true, List.of(Keccak256Hash.ZERO_HASH)));

        verify(mailer, times(1)).sendMail(any(Email.class));
    }

    @Test
    void sendIfEnabled_disabled() {
        var config = SystemProperties.forTest(getClass().getResourceAsStream("challenge.notifier.disabled.conf"));
        var mailer = mock(Mailer.class);

        var underTest = new ChallengeEmailNotifier(config, null);
        underTest.sendIfEnabled(new ChallengeReport(true, List.of(Keccak256Hash.ZERO_HASH)));

        verify(mailer, never()).sendMail(any(Email.class));
    }

    /**
     * May be better to comment the test out
     */
    @Test
    void sendIfEnabled_real() {
        var config = SystemProperties.forTest(getClass().getResourceAsStream("challenge.notifier.real.conf"));
        var mailer = new MailerFactory(config).create();

        var underTest = new ChallengeEmailNotifier(config, mailer);

        try {
            underTest.sendIfEnabled(new ChallengeReport(true, List.of(Keccak256Hash.ZERO_HASH)));
            fail();
        } catch (MailException e) {
            assertEquals(AuthenticationFailedException.class, e.getCause().getClass());
        }
    }
}