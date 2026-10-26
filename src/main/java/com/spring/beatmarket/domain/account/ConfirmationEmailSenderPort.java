package com.spring.beatmarket.domain.account;

public interface ConfirmationEmailSenderPort {
    void sendConfirmationEmail(String email, String confirmationToken);
}
