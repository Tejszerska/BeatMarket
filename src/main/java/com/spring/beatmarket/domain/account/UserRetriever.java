package com.spring.beatmarket.domain.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class UserRetriever {
    private final UserRepository userRepository;

    void checkIfUserExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserExistsException("User with this email already exists.");
        }
    }
}
