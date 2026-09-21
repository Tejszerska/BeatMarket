package com.spring.beatmarket.domain.catalog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class GenreRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @Test
    @DisplayName("Should save Genre and retrieve it")
    void should_save_and_retrieve_genre() {
        // given
        Genre savedGenre = persister.createAndSaveGenre("Pop");
        flushAndClear();

        // when
        Optional<Genre> retrieved = genreRepository.findByIdAndActiveTrue(savedGenre.getId());

        // then
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
        Genre savedGenre = persister.createAndSaveGenre("Pop");
        flushAndClear();

        // when
        boolean existing = genreRepository.existsByIdAndActiveTrue(savedGenre.getId());

        // then
        assertThat(existing).isTrue();
    }

    @Test
    @DisplayName("Should not confirm existing Genre when it isn't active")
    void should_not_confirm() {
        // given
        Genre savedGenre = persister.createAndSaveGenre("Pop");
        savedGenre.deactivate();
        flushAndClear();

        // when
        boolean existing = genreRepository.existsByIdAndActiveTrue(savedGenre.getId());

        // then
        assertThat(existing).isFalse();
    }

    @Test
    @DisplayName("Should return Slice with 3 genres")
    void should_return_slice() {
        // given
        Genre savedGenre1 = persister.createAndSaveGenre("Pop");
        Genre savedGenre2 = persister.createAndSaveGenre("Rap");
        Genre savedGenre3 = persister.createAndSaveGenre("Rock");
        Genre savedGenre4 = persister.createAndSaveGenre("Rop");
        savedGenre4.deactivate();

        flushAndClear();

        Pageable pageable = Pageable.ofSize(5);

        // when
        Slice<Genre> genreSlice = genreRepository.findByActiveTrue(pageable);

        // then
        assertThat(genreSlice.getContent()).extracting(Genre::getId)
                .containsExactlyInAnyOrder(savedGenre1.getId(), savedGenre2.getId(), savedGenre3.getId());
    }
}