package com.spring.beatmarket.domain.account;

import com.spring.beatmarket.domain.account.dto.UserRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserFacade {
    private final UserConformer userConformer;
    private final UserRegisterer userRegisterer;
    private final ApplicationEventPublisher eventPublisher;

    public boolean confirmUser(String confirmationToken) {
        return userConformer.confirmUser(confirmationToken);
    }

    public void registerNewUser(UserRequestDto userRequestDto) {
        User user = userRegisterer.registerNewUser(userRequestDto);
        eventPublisher.publishEvent(new UserRegisteredEvent(user));
    }

    public void registerNewUser(String email) {
        userRegisterer.registerNewUser(email);
    }
}