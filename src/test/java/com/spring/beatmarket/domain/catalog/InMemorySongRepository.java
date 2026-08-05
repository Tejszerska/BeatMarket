package com.spring.beatmarket.domain.catalog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.FluentQuery;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

class InMemorySongRepository implements SongRepository {
    Map<Long, Song> db = new HashMap<>();
    AtomicInteger index = new AtomicInteger(0);

    @Override
    public Optional<Song> findById(final Long id) {
        return Optional.ofNullable(db.get(id));
    }

    @Override
    public Optional<Song> findSongByIdWithGenre(final Long id) {
        return Optional.ofNullable(db.get(id));
    }

    @Override
    public int deleteById(final Long id) {
        if (db.containsKey(id)) {
            db.remove(id);
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public void updateById(final Long id, final Song newSong) {
        db.put(id, newSong);
    }

    @Override
    public boolean existsById(final Long id) {
        return db.containsKey(id);
    }

    @Override
    public Song save(final Song song) {
        if (song.getId() == null) {
            long index = this.index.getAndIncrement();
            db.put(index, song);
            song.setId(index);
            return song;
        } else {
            db.put(song.getId(), song);
            return song;
        }
    }

    @Override
    public int deleteByIdIn(final Collection<Long> ids) {
        int sizeBefore = db.size();
        db.entrySet().removeIf(entry -> ids.contains(entry.getKey()));

        return sizeBefore - db.size();
    }

    @Override
    public Optional<Song> findOne(final Specification<Song> spec) {
        return Optional.empty();
    }

    @Override
    public List<Song> findAll(final Specification<Song> spec) {
        return List.of();
    }

    @Override
    public Page<Song> findAll(final Specification<Song> spec, final Pageable pageable) {
        return null;
    }

    @Override
    public List<Song> findAll(final Specification<Song> spec, final Sort sort) {
        return List.of();
    }

    @Override
    public long count(final Specification<Song> spec) {
        return 0;
    }

    @Override
    public boolean exists(final Specification<Song> spec) {
        return false;
    }

    @Override
    public long delete(final Specification<Song> spec) {
        return 0;
    }

    @Override
    public <S extends Song, R> R findBy(final Specification<Song> spec, final Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }
}
