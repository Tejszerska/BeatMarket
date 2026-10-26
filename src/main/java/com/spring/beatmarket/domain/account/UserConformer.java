package com.spring.beatmarket.domain.account;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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