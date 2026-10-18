package com.spring.beatmarket.domain.account;

import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class UserRepositoryIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Persists a user and retrieves its registration data by email")
    void saves_and_retrieves_user_by_email() {
        // given
        User user = createUser("persisted@example.com", "confirmation-token");
        userRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        // when
        Optional<User> retrieved = userRepository.findFirstByEmail(user.getEmail());

        // then
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getEmail()).isEqualTo(user.getEmail());
        assertThat(retrieved.get().getPassword()).isEqualTo("encoded-password");
        assertThat(retrieved.get().getAuthorities()).containsExactly("ROLE_CUSTOMER");
        assertThat(retrieved.get().isEnabled()).isFalse();
        assertThat(retrieved.get().getConfirmationToken()).isEqualTo("confirmation-token");
    }

    @Test
    @DisplayName("Finds no user when the email does not exist")
    void returns_empty_result_for_unknown_email() {
        // when
        Optional<User> retrieved = userRepository.findFirstByEmail("missing@example.com");

        // then
        assertThat(retrieved).isEmpty();
    }

    @Test
    @DisplayName("Reports whether a user exists for the email")
    void checks_email_existence() {
        // given
        String email = "existing@example.com";
        userRepository.save(createUser(email, "confirmation-token"));
        entityManager.flush();

        // when
        boolean existing = userRepository.existsByEmail(email);
        boolean missing = userRepository.existsByEmail("missing@example.com");

        // then
        assertThat(existing).isTrue();
        assertThat(missing).isFalse();
    }

    @Test
    @DisplayName("Finds a user by confirmation token")
    void finds_user_by_confirmation_token() {
        // given
        String token = "unique-confirmation-token";
        User user = createUser("confirmation@example.com", token);
        userRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        // when
        Optional<User> retrieved = userRepository.findByConfirmationToken(token);
        Optional<User> missing = userRepository.findByConfirmationToken("unknown-token");

        // then
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getEmail()).isEqualTo(user.getEmail());
        assertThat(missing).isEmpty();
    }

    @Test
    @DisplayName("Rejects a duplicate email in the database")
    void rejects_duplicate_email() {
        // given
        String email = "duplicate@example.com";
        userRepository.save(createUser(email, "first-token"));
        entityManager.flush();
        entityManager.clear();

        // when & then
        assertThatThrownBy(() -> {
            userRepository.save(createUser(email, "second-token"));
            entityManager.flush();
        }).isInstanceOf(PersistenceException.class);
    }

    private User createUser(String email, String confirmationToken) {
        return User.builder()
                .email(email)
                .password("encoded-password")
                .authorities(List.of("ROLE_CUSTOMER"))
                .enabled(false)
                .confirmationToken(confirmationToken)
                .build();
    }
}
