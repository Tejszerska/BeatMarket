//package com.spring.beatmarket.domain.catalog;
//
//
//import com.spring.beatmarket.domain.catalog.dto.AlbumInfo;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Slice;
//import org.springframework.data.domain.SliceImpl;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.Set;
//import java.util.concurrent.atomic.AtomicInteger;
//
//class InMemoryAlbumRepository implements AlbumRepository {
//    Map<Long, Album> db = new HashMap<>();
//    AtomicInteger index = new AtomicInteger(0);
//
//    @Override
//    public Album save(final Album album) {
//        if (album.getId() == null) {
//            long index = this.index.getAndIncrement();
//            db.put(index, album);
//            ReflectionTestUtils.setField(album, "id", index);
//            return album;
//
//        } else {
//            db.put(album.getId(), album);
//            return album;
//        }
//
//    }
//
//    @Override
//    public Optional<Album> findAlbumByIdWithSongsAndArtists(final Long id) {
//        return Optional.ofNullable(db.get(id));
//    }
//
//    @Override
//    public Optional<AlbumInfo> findAlbumByIdReturnAlbumInfo(final Long id) {
//
//        return Optional.empty();
//    }
//
//    @Override
//    public List<Album> findAllAlbumsByArtistId(final Long id) {
//        return db.values().stream()
//                .filter(album -> album.getArtists()
//                        .stream()
//                        .anyMatch(artist -> artist.getId().equals(id))).toList();
//    }
//
//    @Override
//    public int deleteByIdIn(final Collection<Long> ids) {
//        int initSize = db.size();
//        db.entrySet().removeIf(entry -> ids.contains(entry.getKey()));
//        return initSize - db.size();
//    }
//
//    @Override
//    public Optional<Album> findById(final Long id) {
//        return Optional.ofNullable(db.get(id));
//    }
//
//    @Override
//    public Slice<Album> findAllAlbums(final Pageable pageable) {
//        List<Album> albumList = new ArrayList<>(db.values());
//        if (pageable.isUnpaged()) {
//            return new SliceImpl<>(albumList);
//        }
//        int start = (int) pageable.getOffset();
//        int pageSize = pageable.getPageSize();
//        if (start >= albumList.size()) return new SliceImpl<>(new ArrayList<>(), pageable, false);
//        int end = Math.min(start + pageSize, albumList.size());
//        List<Album> currentSlice = albumList.subList(start, end);
//        boolean hasNext = (start + pageSize) < albumList.size();
//        return new SliceImpl<>(currentSlice, pageable, hasNext);
//    }
//
//    @Override
//    public Album getReferenceById(final Long id) {
//        return null;
//    }
//
//    @Override
//    public boolean existsById(final Long id) {
//        return false;
//    }
//
//    @Override
//    public Optional<Album> findByIdAndActiveTrue(final Long id) {
//        return Optional.empty();
//    }
//
//    @Override
//    public List<Album> findActiveWithArtistsByIds(final Collection<Long> ids) {
//        return List.of();
//    }
//
//    @Override
//    public List<Album> findByIdInAndActiveTrue(final Collection<Long> ids) {
//        return List.of();
//    }
//
//    @Override
//    public void deactivateAllByIds(final Set<Long> albumIds, final Instant now) {
//
//    }
//}
