package com.spring.songify.domain.crud;

import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

class InMemoryGenreRepository implements GenreRepository {
    Map<Long, Genre> db = new HashMap<>();
    AtomicLong index = new AtomicLong(2);

    public InMemoryGenreRepository() {
        Genre defaultGenre = new Genre("Default", 1L);
        db.put(1L, defaultGenre);
    }

    @Override
    public Genre save(final Genre genre) {
        if (genre.getId() == null) {
            long idx = index.getAndIncrement();
            genre.setId(idx);
            db.put(idx, genre);
            return genre;
        } else {
            db.put(genre.getId(), genre);
            return genre;
        }
    }

    @Override
    public Integer deleteGenreById(final Long genreId) {
        db.remove(genreId);
        return 1;
    }

    @Override
    public List<Genre> findAll(final Pageable pageable) {
        return new ArrayList<>(db.values());
    }

    @Override
    public Optional<Genre> findGenreById(final Long id) {
        return Optional.ofNullable(db.get(id));
    }
}
