package com.spring.beatmarket.domain.catalog;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

class InMemoryAlbumRepository implements AlbumRepository {
    Map<Long, Album> db = new HashMap<>();
    AtomicInteger index = new AtomicInteger(1);

    @Override
    public Album save(final Album album) {
        if (album.getId() == null) {
            long index = this.index.getAndIncrement();
            db.put(index, album);
            ReflectionTestUtils.setField(album, "id", index);

        } else {
            db.put(album.getId(), album);
        }
        return album;
    }

    @Override
    public Optional<Album> findByIdAndActiveTrue(final Long id) {
        return Optional
                .ofNullable(db.get(id))
                .filter(Album::isActive);
    }

    @Override
    public List<Album> findActiveWithArtistsByIds(final Collection<Long> ids) {
        return ids.stream()
                .map(id -> db.get(id))
                .filter(Objects::nonNull)
                .filter(Album::isActive)
                .toList();
    }

    @Override
    public Optional<Album> findAlbumByIdEagerly(final Long id) {
        return findByIdAndActiveTrue(id);
    }

    @Override
    public void deactivateAllByIds(final Set<Long> albumIds, final Instant now) {
        List<Album> albums = findActiveWithArtistsByIds(albumIds);
        albums.forEach(album -> {
            if (album != null) {
                album.deactivate();
                ReflectionTestUtils.setField(album, "editedOn", now);
            }
        });
    }

    @Override
    public Optional<Album> findAlbumByIdLazily(final Long id) {
        return findByIdAndActiveTrue(id);
    }

    @Override
    public Slice<Album> findByActiveTrue(final Pageable pageable) {
        List<Album> albumList = db.values().stream()
                .filter(Album::isActive)
                .toList();

        return getAlbumSlice(pageable, albumList);
    }

    @Override
    public Slice<Album> findByActiveTrueAndTitleContainingIgnoreCase(final String title, final Pageable pageable) {
        List<Album> albumList = db.values().stream()
                .filter(Album::isActive)
                .filter(album -> album.getTitle().toLowerCase().contains(title.toLowerCase()))
                .toList();

        return getAlbumSlice(pageable, albumList);
    }

    @Override
    public Slice<Album> findByActiveTrueAndArtists_Id(final Long artistId, final Pageable pageable) {
        List<Album> albumList = db.values().stream()
                .filter(Album::isActive)
                .filter(album -> album.getArtists().stream()
                        .anyMatch(artist -> artist.getId().equals(artistId)))
                .toList();

        return getAlbumSlice(pageable, albumList);
    }

    @Override
    public Slice<Album> findByActiveTrueAndArtists_IdAndTitleContainingIgnoreCase(final Long artistId, final String title, final Pageable pageable) {
        List<Album> albumList = db.values().stream()
                .filter(Album::isActive)
                .filter(album -> album.getArtists().stream()
                        .anyMatch(artist -> artist.getId().equals(artistId)))
                .filter(album -> album.getTitle().toLowerCase().contains(title.toLowerCase()))
                .toList();

        return getAlbumSlice(pageable, albumList);
    }

    private static SliceImpl<Album> getAlbumSlice(final Pageable pageable, final List<Album> albumList) {
        int completeListSize = albumList.size();

        int pageStart = (int) pageable.getOffset();
        int pageEnd = Math.min((pageStart + pageable.getPageSize()), completeListSize);

        if(pageStart >= completeListSize)
            return new SliceImpl<>(List.of(), pageable, false);

        List<Album> currentSlice = albumList.subList(pageStart, pageEnd);

        boolean hasNext = pageEnd < completeListSize;

        return new SliceImpl<>(currentSlice, pageable, hasNext);
    }
}
