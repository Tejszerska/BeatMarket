package com.spring.songify.domain.crud;

class InMemoryGenreRepository implements GenreRepository {

    @Override
    public Genre save(final Genre genre) {
        return null;
    }

    @Override
    public Integer deleteGenreById(final Long genreId) {
        return 0;
    }
}
