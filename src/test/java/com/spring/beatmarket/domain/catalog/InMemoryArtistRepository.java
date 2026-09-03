package com.spring.beatmarket.domain.catalog;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

class InMemoryArtistRepository implements ArtistRepository {
    Map<Long, Artist> db = new HashMap<>();
    AtomicInteger index = new AtomicInteger(0);

    @Override
    public Artist save(final Artist artist) {
        if (artist.getId() == null) {
            long index = this.index.getAndIncrement();
            db.put(index, artist);
            ReflectionTestUtils.setField(artist, "id", index);
            return artist;
        } else {
            db.put(artist.getId(), artist);
            return artist;
        }

    }

    @Override
    public Slice<Artist> findByActiveTrue(final Pageable pageable) {
        throw new UnsupportedOperationException("This isn't covered in unit testing");
    }

    @Override
    public Slice<Artist> findByActiveTrueAndNameContainsIgnoreCase(final String name, final Pageable pageable) {
        return null;
    }

    @Override
    public Optional<Artist> findByIdAndActiveTrue(final Long artistId) {
        return Optional.ofNullable(db.get(artistId));
    }

    @Override
    public int deleteArtistById(final Long id) {

        if (db.containsKey(id)) {
            db.remove(id);
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public boolean existsById(final Long id) {
        return db.containsKey(id);
    }

    @Override
    public Artist getReferenceById(final Long id) {
        return null;
    }

    @Override
    public List<Artist> findByIdInAndActiveTrue(final List<Long> ids) {
        return List.of();
    }

    @Override
    public Optional<Artist> findByIdWithSongs(final Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Artist> findByIdWithAlbums(final Long id) {
        return Optional.empty();
    }
}
