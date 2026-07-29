package com.spring.beatmarket.domain.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class UserRetriever {
    private final UserRepository userRepository;

    void throwExceptionIfUserExits(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserExistsException("User with this email already exists.");
        }
    }

    boolean checkIfUserExists(String email) {
        return userRepository.existsByEmail(email);
    }

}
