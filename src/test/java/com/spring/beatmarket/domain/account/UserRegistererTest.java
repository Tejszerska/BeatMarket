package com.spring.beatmarket.domain.account;

import com.spring.beatmarket.domain.account.dto.UserRegistrationDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserRegistererTest {

    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserRetriever userRetriever = mock(UserRetriever.class);
    private final UserRegisterer userRegisterer =
            new UserRegisterer(passwordEncoder, userRepository, userRetriever);

    @Test
    @DisplayName("Registers a new inactive user with an encoded password and confirmation token")
    void registers_inactive_user() {
        // given
        UserRegistrationDto request = new UserRegistrationDto("new@example.com", "plain-password");
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        User registeredUser = userRegisterer.registerNewUser(request);

        // then
        assertThat(registeredUser.getEmail()).isEqualTo(request.email());
        assertThat(registeredUser.getPassword()).isEqualTo("encoded-password");
        assertThat(registeredUser.getAuthorities()).containsExactly("ROLE_CUSTOMER");
        assertThat(registeredUser.isEnabled()).isFalse();
        assertThat(UUID.fromString(registeredUser.getConfirmationToken())).isNotNull();
        verify(userRetriever).ensureUserDoesNotExist(request.email());
        verify(passwordEncoder).encode(request.password());
        verify(userRepository).save(registeredUser);
    }

    @Test
    @DisplayName("Rejects registration before encoding or saving an existing user")
    void rejects_duplicate_registration() {
        // given
        UserRegistrationDto request = new UserRegistrationDto("existing@example.com", "plain-password");
        doThrow(new UserExistsException("User with this email already exists."))
                .when(userRetriever).ensureUserDoesNotExist(request.email());

        // when & then
        assertThatThrownBy(() -> userRegisterer.registerNewUser(request))
                .isInstanceOf(UserExistsException.class);
        verify(passwordEncoder, never()).encode(request.password());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Creates an enabled passwordless user when an external account is new")
    void registers_new_external_user() {
        // given
        String email = "external@example.com";
        when(userRetriever.checkIfUserExists(email)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        userRegisterer.registerNewUser(email);

        // then
        verify(userRepository).save(argThat(user ->
                user.getEmail().equals(email)
                        && user.getPassword() == null
                        && user.getAuthorities().equals(List.of("ROLE_CUSTOMER"))
                        && user.isEnabled()
                        && user.getConfirmationToken() == null
        ));
    }

    @Test
    @DisplayName("Does not create an external user when the email already exists")
    void skips_existing_external_user() {
        // given
        String email = "existing@example.com";
        when(userRetriever.checkIfUserExists(email)).thenReturn(true);

        // when
        userRegisterer.registerNewUser(email);

        // then
        verify(userRepository, never()).save(any(User.class));
    }
}
