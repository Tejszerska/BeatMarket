package com.spring.beatmarket.infrastructure.email;

import com.spring.beatmarket.domain.account.ConfirmationEmailSenderPort;
import com.spring.beatmarket.domain.account.User;
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
    public void sendConfirmationEmail(final User user) {
        String to = user.getEmail();
        String subject = "Complete your registration";
        String text = "To confirm your account, please click here: "
                + "https://localhost:8443/users/confirm?token=" + user.getConfirmationToken();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        try {
            mailSender.send(message);
            log.info("Confirmation token sent to {}", user.getEmail());
        } catch (MailException e) {
            log.error("Failed to send confirmation email to {}", user.getEmail(), e);
        }
    }
}
