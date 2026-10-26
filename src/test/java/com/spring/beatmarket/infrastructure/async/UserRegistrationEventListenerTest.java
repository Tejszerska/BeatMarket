package com.spring.beatmarket.infrastructure.async;

import com.spring.beatmarket.domain.account.ConfirmationEmailSenderPort;
import com.spring.beatmarket.domain.account.UserRegisteredEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class UserRegistrationEventListenerTest {

    private final ConfirmationEmailSenderPort confirmationEmailSenderPort = mock(ConfirmationEmailSenderPort.class);
    private final UserRegistrationEventListener listener =
            new UserRegistrationEventListener(confirmationEmailSenderPort);

    @Test
    @DisplayName("Sends a confirmation email when a user registers")
    void sends_confirmation_email_for_registered_user() {
        // given
        UserRegisteredEvent event = new UserRegisteredEvent("new@example.com", "confirmation-token");

        // when
        listener.handleUserRegistration(event);

        // then
        verify(confirmationEmailSenderPort).sendConfirmationEmail(event.email(), event.confirmationToken());
    }
}
