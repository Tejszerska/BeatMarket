package com.spring.beatmarket.infrastructure.security;

import com.spring.beatmarket.domain.account.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityUserTest {

    @Test
    @DisplayName("Maps user credentials and authorities to security user details")
    void maps_user_details() {
        // given
        User user = User.builder()
                .email("member@example.com")
                .password("encoded-password")
                .authorities(List.of("ROLE_USER", "ROLE_ADMIN"))
                .enabled(true)
                .build();
        SecurityUser securityUser = new SecurityUser(user);

        // when
        List<String> authorities = securityUser.getAuthoritiesAsString();

        // then
        assertThat(securityUser.getUsername()).isEqualTo("member@example.com");
        assertThat(securityUser.getPassword()).isEqualTo("encoded-password");
        assertThat(authorities).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
        assertThat(securityUser.getAuthorities())
                .extracting("authority")
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    @DisplayName("Reflects whether the account is enabled")
    void reflects_account_enabled_state() {
        // given
        User enabledUser = User.builder().email("enabled@example.com").enabled(true).build();
        User disabledUser = User.builder().email("disabled@example.com").enabled(false).build();

        // when & then
        assertThat(new SecurityUser(enabledUser).isEnabled()).isTrue();
        assertThat(new SecurityUser(disabledUser).isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Reports account and credential validity as non-expired and unlocked")
    void reports_account_validity() {
        // given
        User user = User.builder().email("member@example.com").build();
        SecurityUser securityUser = new SecurityUser(user);

        // when & then
        assertThat(securityUser.isAccountNonExpired()).isTrue();
        assertThat(securityUser.isAccountNonLocked()).isTrue();
        assertThat(securityUser.isCredentialsNonExpired()).isTrue();
    }
}
