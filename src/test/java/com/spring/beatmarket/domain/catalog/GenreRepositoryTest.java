package com.spring.beatmarket.domain.catalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class GenreRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.getEntityManager()
                .createQuery("DELETE FROM Genre")
                .executeUpdate();
    }

    @Test
    @DisplayName("Should save Genre and retrieve it")
    void should_save_and_retrieve_genre() {
        // given
        Genre genre = new Genre("Pop");

        // when
        Genre savedGenre = genreRepository.save(genre);

        entityManager.flush();
        entityManager.clear();

        // then
        Optional<Genre> retrieved = genreRepository.findByIdAndActiveTrue(savedGenre.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Pop");
    }

    @Test
    @DisplayName("Should retrieve empty Optional")
    void should_retrieve_empty_optional() {
        // given
        Long nonExistingId = 99L;

        // when
        Optional<Genre> retrieved = genreRepository.findByIdAndActiveTrue(nonExistingId);

        // then
        assertThat(retrieved).isEmpty();
    }

    @Test
    @DisplayName("Should confirm existing Genre when it is active")
    void should_confirm() {
        // given
        Genre genre = new Genre("Pop");
        Genre savedGenre = genreRepository.save(genre);
        entityManager.flush();
        entityManager.clear();
        assertThat(savedGenre.isActive()).isTrue();
        // when
        boolean existing = genreRepository.existsByIdAndActiveTrue(savedGenre.getId());

        // then
        assertThat(existing).isTrue();
    }

    @Test
    @DisplayName("Should not confirm existing Genre when it isn't active")
    void should_not_confirm() {
        // given
        Genre genre = new Genre("Pop");
        Genre savedGenre = genreRepository.save(genre);
        savedGenre.deactivate();

        entityManager.flush();
        entityManager.clear();

        assertThat(savedGenre.isActive()).isFalse();
        // when
        boolean existing = genreRepository.existsByIdAndActiveTrue(savedGenre.getId());

        // then
        assertThat(existing).isFalse();
    }

    @Test
    @DisplayName("Should return Slice with 3 genres")
    void should_return_slice() {
        // given
        Genre savedGenre1 = genreRepository.save(new Genre("Pop"));
        Genre savedGenre2 = genreRepository.save(new Genre("Rap"));
        Genre savedGenre3 = genreRepository.save(new Genre("Rock"));
        Genre savedGenre4 = genreRepository.save(new Genre("Rop"));
        savedGenre4.deactivate();

        entityManager.flush();
        entityManager.clear();

        assertThat(savedGenre1.isActive()).isTrue();
        assertThat(savedGenre4.isActive()).isFalse();

        Pageable pageable = Pageable.ofSize(5);

        // when
        Slice<Genre> genreSlice = genreRepository.findByActiveTrue(pageable);

        // then
        assertThat(genreSlice.getContent()).extracting(Genre::getId)
                .containsExactlyInAnyOrder(savedGenre1.getId(), savedGenre2.getId(), savedGenre3.getId());
    }
}