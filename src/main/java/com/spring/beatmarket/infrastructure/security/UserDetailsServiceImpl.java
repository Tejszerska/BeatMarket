package com.spring.beatmarket.infrastructure.security;

import com.spring.beatmarket.domain.account.User;
import com.spring.beatmarket.domain.account.UserConformer;
import com.spring.beatmarket.domain.account.UserRepository;
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
    private final static List<String> DEFAULT_USER_ROLES = List.of("ROLE_ADMIN", "ROLE_USER");
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
                .authorities(DEFAULT_USER_ROLES)
                .enabled(false)
                .confirmationToken(UUID.randomUUID().toString())
                .build();

        User savedUser = userRepository.save(createdUser);
        userConformer.sendConfirmationEmail(createdUser);
        log.info("Created and saved new user by id={}", savedUser.getId());
    }

    @Override
    public void updateUser(final UserDetails user) {
        throw new UnsupportedOperationException("Updating user is not implemented yet");
    }

    @Override
    public void deleteUser(final String username) {
        throw new UnsupportedOperationException("Updating user is not implemented yet");
    }

    @Override
    public void changePassword(final String oldPassword, final String newPassword) {
        throw new UnsupportedOperationException("Updating user is not implemented yet");
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
