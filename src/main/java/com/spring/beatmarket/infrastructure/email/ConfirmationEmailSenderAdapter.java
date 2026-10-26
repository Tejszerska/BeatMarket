package com.spring.beatmarket.infrastructure.email;

import com.spring.beatmarket.domain.account.ConfirmationEmailSenderPort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
class ConfirmationEmailSenderAdapter implements ConfirmationEmailSenderPort {
    private final MailSender mailSender;

    @Override
    public void sendConfirmationEmail(final String email, final String confirmationToken) {
        String subject = "Complete your registration";
        String text = "To confirm your account, please click here: "
                + "https://localhost:8443/users/confirm?token=" + confirmationToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);
        try {
            mailSender.send(message);
            log.info("Confirmation token sent to {}", email);
        } catch (MailException e) {
            log.error("Failed to send confirmation email to {}", email, e);
        }
    }
}
