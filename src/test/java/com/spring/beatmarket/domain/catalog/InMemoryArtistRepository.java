package com.spring.beatmarket.domain.catalog;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

class InMemoryArtistRepository implements ArtistRepository {
    Map<Long, Artist> db = new HashMap<>();
    AtomicInteger index = new AtomicInteger(0);

    @Override
    public Artist save(final Artist artist) {
        if (artist.getId() == null) {
            long newId = this.index.incrementAndGet();
            ReflectionTestUtils.setField(artist, "id", newId);
            db.put(newId, artist);
            return artist;
        } else {
            db.put(artist.getId(), artist);
            return artist;
        }
    }

    @Override
    public Slice<Artist> findByActiveTrue(final Pageable pageable) {
        List<Artist> artistsList = db.values().stream()
                .filter(Artist::isActive)
                .toList();

        return getArtistSlice(pageable, artistsList);
    }

    @Override
    public Slice<Artist> findByActiveTrueAndNameContainsIgnoreCase(final String name, final Pageable pageable) {
        List<Artist> artistsList = db.values().stream()
                .filter(Artist::isActive)
                .filter(artist -> artist.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();

        return getArtistSlice(pageable, artistsList);
    }

    @Override
    public Optional<Artist> findByIdAndActiveTrue(final Long artistId) {
        Artist artist = db.get(artistId);
        if (artist != null && artist.isActive()) {
            return Optional.of(artist);
        }
        return Optional.empty();
    }

    @Override
    public int deleteArtistById(final Long id) {
        if (db.containsKey(id)) {
            db.remove(id);
            return 1;
        }
        return 0;
    }

    @Override
    public boolean existsById(final Long id) {
        return db.containsKey(id);
    }

    @Override
    public Artist getReferenceById(final Long id) {
        return db.get(id);
    }

    @Override
    public List<Artist> findByIdInAndActiveTrue(final Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return ids.stream()
                .map(db::get)
                .filter(Objects::nonNull)
                .filter(Artist::isActive)
                .toList();
    }

    @Override
    public Optional<Artist> findByIdWithSongs(final Long id) {
        return findByIdAndActiveTrue(id);
    }

    @Override
    public Optional<Artist> findByIdWithAlbums(final Long id) {
        return findByIdAndActiveTrue(id);
    }

private SliceImpl<Artist> getArtistSlice(Pageable pageable, List<Artist> artistList){
    int completeListSize = artistList.size();
    int pageStart = (int) pageable.getOffset();
    int pageEnd = Math.min((pageStart + pageable.getPageSize()), completeListSize);

    if(pageStart >= completeListSize){
        return new SliceImpl<>(List.of(), pageable, false);
    }

    List<Artist> currentSlice = artistList.subList(pageStart, pageEnd);

    boolean hasNext = pageEnd < completeListSize;

    return new SliceImpl<>(currentSlice, pageable, hasNext);
}
}