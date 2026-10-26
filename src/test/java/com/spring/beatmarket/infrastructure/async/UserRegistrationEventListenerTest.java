package com.spring.beatmarket.infrastructure.async;

import com.spring.beatmarket.domain.account.ConfirmationEmailSenderPort;
import com.spring.beatmarket.domain.account.User;
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
        User user = User.builder()
                .email("new@example.com")
                .confirmationToken("confirmation-token")
                .build();
        UserRegisteredEvent event = new UserRegisteredEvent(user);

        // when
        listener.handleUserRegistration(event);

        // then
        verify(confirmationEmailSenderPort).sendConfirmationEmail(user);
    }
}
