package com.spring.beatmarket.domain.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserConformerTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserConformer userConformer = new UserConformer(userRepository);

    @Test
    @DisplayName("Activates a user and clears the token when the token matches")
    void confirms_user_with_matching_token() {
        // given
        User user = User.builder()
                .email("new@example.com")
                .enabled(false)
                .confirmationToken("confirmation-token")
                .build();
        when(userRepository.findByConfirmationToken("confirmation-token")).thenReturn(Optional.of(user));

        // when
        boolean confirmed = userConformer.confirmUser("confirmation-token");

        // then
        assertThat(confirmed).isTrue();
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.getConfirmationToken()).isNull();
    }

    @Test
    @DisplayName("Rejects confirmation when the token does not match a user")
    void rejects_unknown_confirmation_token() {
        // given
        String token = "unknown-token";
        when(userRepository.findByConfirmationToken(token)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userConformer.confirmUser(token))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");
    }
}
