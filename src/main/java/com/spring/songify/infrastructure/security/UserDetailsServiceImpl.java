package com.spring.songify.infrastructure.security;

import com.spring.songify.domain.usercrud.User;
import com.spring.songify.domain.usercrud.UserConformer;
import com.spring.songify.domain.usercrud.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
class UserDetailsServiceImpl implements UserDetailsManager {
    private final static String DEFAULT_USER_ROLE = "ROLE_USER";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserConformer userConformer;

    @Override
    public void createUser(final UserDetails user) {
        if (userExists(user.getUsername())) {
            log.warn("Not saved. User already exists");
            throw new RuntimeException("Not saved. User already exists");
        }
        User createdUser = User.builder()
                .email(user.getUsername())
                .password(passwordEncoder.encode(user.getPassword()))
                .authorities(List.of(DEFAULT_USER_ROLE))
                .enabled(false)
                .confirmationToken(UUID.randomUUID().toString())
                .build();

        User savedUser = userRepository.save(createdUser);
        userConformer.sendConfirmationEmail(createdUser);
        log.info("Created and saved new user by id={}", savedUser.getId());
    }

    @Override
    public void updateUser(final UserDetails user) {

    }

    @Override
    public void deleteUser(final String username) {

    }

    @Override
    public void changePassword(final String oldPassword, final String newPassword) {

    }

    @Override
    public boolean userExists(final String username) {
        return userRepository.existsByEmail(username);
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepository.findFirstByEmail(username)
                .map(SecurityUser::new)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
