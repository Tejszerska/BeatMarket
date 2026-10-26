package com.spring.beatmarket.infrastructure.email;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ConfirmationEmailSenderAdapterTest {

    private final MailSender mailSender = mock(MailSender.class);
    private final ConfirmationEmailSenderAdapter adapter = new ConfirmationEmailSenderAdapter(mailSender);

    @Test
    @DisplayName("Sends a confirmation email with the user's address and token")
    void sends_confirmation_email() {
        // given
        String email = "new@example.com";
        String confirmationToken = "confirmation-token";

        // when
        adapter.sendConfirmationEmail(email, confirmationToken);

        // then
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertThat(message.getTo()).containsExactly(email);
        assertThat(message.getSubject()).isEqualTo("Complete your registration");
        assertThat(message.getText())
                .isEqualTo("To confirm your account, please click here: "
                        + "https://localhost:8443/users/confirm?token=" + confirmationToken);
    }

    @Test
    @DisplayName("Does not propagate a mail delivery failure")
    void does_not_propagate_mail_delivery_failure() {
        // given
        String email = "new@example.com";
        String confirmationToken = "confirmation-token";
        doThrow(new MailSendException("Mail server unavailable"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // when & then
        assertThatCode(() -> adapter.sendConfirmationEmail(email, confirmationToken)).doesNotThrowAnyException();
        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}
