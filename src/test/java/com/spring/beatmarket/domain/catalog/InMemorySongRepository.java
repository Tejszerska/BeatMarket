package com.spring.beatmarket.domain.catalog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

class InMemorySongRepository implements SongRepository {
    Map<Long, Song> db = new HashMap<>();
    AtomicInteger index = new AtomicInteger(0);

    @Override
    public Optional<Song> findByIdAndActiveTrue(final Long id) {
        Song song = db.get(id);
        if (song != null && song.isActive()) {
            return Optional.of(song);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Song> findSongByIdEagerly(final Long id) {
        return findByIdAndActiveTrue(id);
    }

    @Override
    public boolean existsByIdAndActiveTrue(final Long id) {
        Song song = db.get(id);
        return song != null && song.isActive();
    }


    @Override
    public Song save(final Song song) {
        if (song.getId() == null) {
            long newId = this.index.incrementAndGet();
            ReflectionTestUtils.setField(song, "id", newId);
            db.put(newId, song);
            return song;
        } else {
            db.put(song.getId(), song);
            return song;
        }
    }

    @Override
    public boolean existsByGenreId(final Long genreId) {
        return db.values().stream()
                .anyMatch(song -> song.getGenre() != null &&  song.getGenre().getId().equals(genreId));
    }

    @Override
    public List<Song> findActiveWithArtistsByIds(final Collection<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        return ids.stream()
                .map(db::get)
                .filter(Objects::nonNull)
                .filter(Song::isActive)
                .toList();
    }

    @Override
    public Integer bulkUpdateGenre(final Long oldGenreId, final Long newGenreId, final Instant now) {
        List<Song> songsToUpdate = db.values().stream()
                .filter(Song::isActive)
                .filter(song -> song.getGenre() != null && song.getGenre().getId().equals(oldGenreId))
                .toList();

        Genre genreProxy = new Genre();
        genreProxy.setId(newGenreId);

        songsToUpdate.forEach(song -> {
            ReflectionTestUtils.setField(song, "genre", genreProxy);
            updateVersionAndEditedOn(now, song);
        });

        return songsToUpdate.size();

    }

    @Override
    public void deactivateAllByIds(final Set<Long> songIds, final Instant now) {
        songIds.stream()
                .map(db::get)
                .filter(Objects::nonNull)
                .forEach(song -> {
                    ReflectionTestUtils.setField(song, "active", false);
                    updateVersionAndEditedOn(now, song);
                });
    }

    private static void updateVersionAndEditedOn(final Instant now, final Song song) {
        ReflectionTestUtils.setField(song, "editedOn", now);
        Integer currentVersion = (Integer) ReflectionTestUtils.getField(song, "version");
        int nextVersion = (currentVersion == null) ? 1 : currentVersion + 1;
        ReflectionTestUtils.setField(song, "version", nextVersion);
    }

    @Override
    public Optional<Song> findOne(final Specification<Song> spec) {
        throw new UnsupportedOperationException("Specification related methods are covered in integration tests.");
    }

    @Override
    public List<Song> findAll(final Specification<Song> spec) {
        throw new UnsupportedOperationException("Specification related methods are covered in integration tests.");
    }

    @Override
    public Page<Song> findAll(final Specification<Song> spec, final Pageable pageable) {
        throw new UnsupportedOperationException("Specification related methods are covered in integration tests.");
    }

    @Override
    public List<Song> findAll(final Specification<Song> spec, final Sort sort) {
        throw new UnsupportedOperationException("Specification related methods are covered in integration tests.");
    }

    @Override
    public long count(final Specification<Song> spec) {
        throw new UnsupportedOperationException("Specification related methods are covered in integration tests.");
    }

    @Override
    public boolean exists(final Specification<Song> spec) {
        throw new UnsupportedOperationException("Specification related methods are covered in integration tests.");
    }

    @Override
    public long delete(final Specification<Song> spec) {
        throw new UnsupportedOperationException("Specification related methods are covered in integration tests.");
    }

    @Override
    public <S extends Song, R> R findBy(final Specification<Song> spec, final Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throw new UnsupportedOperationException("Specification related methods are covered in integration tests.");
    }
}
