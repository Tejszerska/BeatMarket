package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.shared.domain.BaseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

class InMemoryGenreRepository implements GenreRepository {
    Map<Long, Genre> db = new HashMap<>();
    AtomicLong index = new AtomicLong(0);

    @Override
    public Genre save(final Genre genre) {
        if (genre.getId() == null) {
            long idx = this.index.incrementAndGet();
            genre.setId(idx);
            db.put(idx, genre);
            return genre;
        } else {
            db.put(genre.getId(), genre);
            return genre;
        }
    }

    @Override
    public Slice<Genre> findByActiveTrue(final Pageable pageable) {
        List<Genre> activeGenres = db.values().stream()
                .filter(BaseEntity::isActive)
                .sorted(Comparator.comparing(Genre::getId))
                .toList();

        List<Genre> list = new ArrayList<>(activeGenres);
        if (pageable.isUnpaged()) {
            return new SliceImpl<>(list);
        }

        int start = (int) pageable.getOffset();
        int pageSize = pageable.getPageSize();
        if (start >= list.size()) return new SliceImpl<>(new ArrayList<>(), pageable, false);
        int end = Math.min(start + pageSize, list.size());
        List<Genre> currentSlice = list.subList(start, end);
        boolean hasNext = (start + pageSize) < list.size();
        return new SliceImpl<>(currentSlice, pageable, hasNext);
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