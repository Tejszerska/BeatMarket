package com.spring.beatmarket.domain.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class UserRetriever {
    private final UserRepository userRepository;

    void ensureUserDoesNotExist(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserExistsException();
        }
    }

    boolean checkIfUserExists(String email) {
        return userRepository.existsByEmail(email);
    }

}
