package com.spring.beatmarket.domain.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    @DisplayName("Confirms a user and clears the confirmation token")
    void confirms_user_and_clears_token() {
        // given
        User user = User.builder()
                .email("user@example.com")
                .enabled(false)
                .confirmationToken("confirmation-token")
                .build();

        // when
        boolean confirmed = user.confirm();

        // then
        assertThat(confirmed).isTrue();
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.getConfirmationToken()).isNull();
    }
}
