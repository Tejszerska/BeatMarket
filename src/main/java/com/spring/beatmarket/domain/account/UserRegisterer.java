package com.spring.beatmarket.domain.account;

import com.spring.beatmarket.domain.account.dto.UserRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j

class UserRegisterer {
    private final static List<String> DEFAULT_USER_ROLES = List.of("ROLE_ADMIN", "ROLE_CUSTOMER"); // @TODO ADMIN just for the development
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserRetriever userRetriever;

    User registerNewUser(UserRequestDto userRequestDto) {
        userRetriever.checkIfUserExists(userRequestDto.email());

        User user = User.builder()
                .email(userRequestDto.email())
                .password(passwordEncoder.encode(userRequestDto.password()))
                .authorities(DEFAULT_USER_ROLES)
                .enabled(false)
                .confirmationToken(UUID.randomUUID().toString())
                .build();

        User savedUser = userRepository.save(user);
        log.info("Created and saved new user by id={}", savedUser.getId());
        return savedUser;
    }
}
