package com.spring.songify.domain.crud;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

class InMemoryGenreRepository implements GenreRepository {

    @Override
    public Genre save(final Genre genre) {
        return null;
    }

    @Override
    public Integer deleteGenreById(final Long genreId) {
        return 0;
    }

    @Override
    public List<Genre> findAll(final Pageable pageable) {
        return List.of();
    }

    @Override
    public Optional<Genre> findGenreById(final Long id) {
        return Optional.empty();
    }
}
