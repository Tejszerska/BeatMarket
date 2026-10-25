package com.spring.beatmarket.domain.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserRetrieverTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserRetriever userRetriever = new UserRetriever(userRepository);

    @Test
    @DisplayName("Rejects registration when the email already exists")
    void rejects_existing_email() {
        // given
        String email = "existing@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userRetriever.ensureUserDoesNotExist(email))
                .isInstanceOf(UserExistsException.class)
                .hasMessage("User with this email already exists.");
    }

    @Test
    @DisplayName("Allows registration when the email does not exist")
    void allows_missing_email() {
        // given
        String email = "new@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        // when
        userRetriever.ensureUserDoesNotExist(email);

        // then
        verify(userRepository).existsByEmail(email);
    }

    @Test
    @DisplayName("Reports whether an email already exists")
    void reports_email_existence() {
        // given
        String email = "existing@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // when
        boolean exists = userRetriever.checkIfUserExists(email);

        // then
        assertThat(exists).isTrue();
        verify(userRepository).existsByEmail(email);
    }
}
