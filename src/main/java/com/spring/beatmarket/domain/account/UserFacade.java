package com.spring.beatmarket.domain.account;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserFacade {
    private final UserConformer userConformer;
    private final UserRegisterer userRegisterer;

    public boolean confirmUser(String confirmationToken) {
        return userConformer.confirmUser(confirmationToken);
    }

    public void sendConfirmationEmail(User user) {
        userConformer.sendConfirmationEmail(user);
    }

    public void registerNewUser(UserRequestDto userRequestDto) {
        User user = userRegisterer.registerNewUser(userRequestDto);
        sendConfirmationEmail(user);
    }
}