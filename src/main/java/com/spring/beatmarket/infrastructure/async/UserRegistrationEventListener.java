package com.spring.beatmarket.infrastructure.async;

import com.spring.beatmarket.domain.account.ConfirmationEmailSenderPort;
import com.spring.beatmarket.domain.account.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
class UserRegistrationEventListener {
    private final ConfirmationEmailSenderPort confirmationEmailSenderPort;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserRegistration(UserRegisteredEvent event) {
        confirmationEmailSenderPort.sendConfirmationEmail(event.user());
    }
}
