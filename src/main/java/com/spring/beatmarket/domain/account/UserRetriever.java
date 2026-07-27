package com.spring.beatmarket.domain.account;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class UserRetriever {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    UserDto findFirstByEmail(String email){
        User user = userRepository.findFirstByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );

        return mapper.mapFromEntityToUserDto(user);
    }


    void checkIfUserExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserExistsException("User with this email already exists.");
        }
    }
}
