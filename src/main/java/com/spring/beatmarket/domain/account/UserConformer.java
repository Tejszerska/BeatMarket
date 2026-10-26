package com.spring.beatmarket.domain.account;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
class UserConformer {

    private final UserRepository userRepository;

    @Transactional
    boolean confirmUser(String confirmationToken) {
        User user = userRepository.findByConfirmationToken(confirmationToken)
                .orElseThrow(UserNotFoundException::new);
        return user.confirm();
    }
}