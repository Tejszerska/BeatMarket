package com.spring.beatmarket.domain.catalog;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

class InMemoryGenreRepository implements GenreRepository {
    Map<Long, Genre> db = new HashMap<>();
    AtomicLong index = new AtomicLong(1);

    @Override
    public Genre save(final Genre genre) {
        if (genre.getId() == null) {
            long idx = this.index.getAndIncrement();
            genre.setId(idx);
            db.put(idx, genre);
        } else {
            db.put(genre.getId(), genre);
        }
        return genre;
    }

    @Override
    public Slice<Genre> findByActiveTrue(final Pageable pageable) {
        List<Genre> fullActiveList = db.values().stream().filter(Genre::isActive).toList();
        int fullListSize = fullActiveList.size();

        int pageStart = (int) pageable.getOffset();
        int pageEnd = Math.min((pageStart + pageable.getPageSize()), fullListSize);

        if (pageStart >= fullListSize) {
            return new SliceImpl<>(Collections.emptyList(), pageable, false);
        }
        List<Genre> listSliced = fullActiveList.subList(pageStart, pageEnd);

        boolean hasNext = pageEnd < fullListSize;

        return new SliceImpl<>(listSliced, pageable, hasNext);
    }

    @Override
    public boolean existsByIdAndActiveTrue(final Long id) {
        Genre genre = db.get(id);
        return genre != null && genre.isActive();
    }

    @Override
    public Optional<Genre> findByIdAndActiveTrue(final Long id) {
        Genre genre = db.get(id);
        if (genre != null && genre.isActive()) {
            return Optional.of(genre);
        }
        return Optional.empty();
    }

}