package com.spring.beatmarket.domain.account;

public interface ConfirmationEmailSenderPort {
    void sendConfirmationEmail(User user);
}
