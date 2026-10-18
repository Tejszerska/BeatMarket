package com.spring.beatmarket.infrastructure.security;

import com.spring.beatmarket.domain.account.User;
import com.spring.beatmarket.domain.account.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserDetailsServiceImplTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(userRepository);

    @Test
    @DisplayName("Loads security details for a registered email")
    void loads_security_user_for_registered_email() {
        // given
        String email = "member@example.com";
        User user = User.builder()
                .email(email)
                .password("encoded-password")
                .authorities(List.of("ROLE_USER"))
                .enabled(true)
                .build();
        when(userRepository.findFirstByEmail(email)).thenReturn(Optional.of(user));

        // when
        SecurityUser securityUser = (SecurityUser) userDetailsService.loadUserByUsername(email);

        // then
        assertThat(securityUser.getUsername()).isEqualTo(email);
        assertThat(securityUser.getPassword()).isEqualTo("encoded-password");
        assertThat(securityUser.getAuthoritiesAsString()).containsExactly("ROLE_USER");
        verify(userRepository).findFirstByEmail(email);
    }

    @Test
    @DisplayName("Throws username not found when the email is not registered")
    void throws_username_not_found_for_unknown_email() {
        // given
        String email = "missing@example.com";
        when(userRepository.findFirstByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }
}
