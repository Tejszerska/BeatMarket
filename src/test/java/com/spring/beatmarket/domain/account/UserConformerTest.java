package com.spring.beatmarket.domain.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserConformerTest {

    private final MailSender mailSender = mock(MailSender.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserConformer userConformer = new UserConformer(mailSender, userRepository);

    @Test
    @DisplayName("Sends a confirmation email containing the user's token")
    void sends_confirmation_email() {
        // given
        User user = User.builder()
                .email("new@example.com")
                .confirmationToken("confirmation-token")
                .build();

        // when
        userConformer.handleUserRegistration(new UserRegisteredEvent(user));

        // then
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        assertThat(messageCaptor.getValue().getTo()).containsExactly(user.getEmail());
        assertThat(messageCaptor.getValue().getSubject()).isEqualTo("Complete your registration");
        assertThat(messageCaptor.getValue().getText()).contains(user.getConfirmationToken());
    }

    @Test
    @DisplayName("Handles a mail delivery failure without propagating the exception")
    void handles_mail_delivery_failure() {
        // given
        User user = User.builder()
                .email("new@example.com")
                .confirmationToken("confirmation-token")
                .build();
        doThrow(new MailSendException("Mail server unavailable"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // when
        userConformer.handleUserRegistration(new UserRegisteredEvent(user));

        // then
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Activates a user and clears the token when the token matches")
    void confirms_user_with_matching_token() {
        // given
        User user = User.builder()
                .email("new@example.com")
                .enabled(false)
                .confirmationToken("confirmation-token")
                .build();
        when(userRepository.findByConfirmationToken("confirmation-token")).thenReturn(Optional.of(user));

        // when
        boolean confirmed = userConformer.confirmUser("confirmation-token");

        // then
        assertThat(confirmed).isTrue();
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.getConfirmationToken()).isNull();
    }

    @Test
    @DisplayName("Rejects confirmation when the token does not match a user")
    void rejects_unknown_confirmation_token() {
        // given
        String token = "unknown-token";
        when(userRepository.findByConfirmationToken(token)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userConformer.confirmUser(token))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("user not found");
    }
}
