package com.spring.beatmarket.domain.catalog;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

class InMemoryArtistRepository  {
    Map<Long, Artist> db = new HashMap<>();
    AtomicInteger index = new AtomicInteger(0);

    public Artist save(final Artist artist) {
        if(artist.getId() == null){
            long index = this.index.getAndIncrement();
            db.put(index, artist);
            ReflectionTestUtils.setField(artist, "id", index);
            return artist;
        } else {
            db.put(artist.getId(), artist);
            return artist;
        }

    }

    public Slice<Artist> findByActiveTrue(final Pageable pageable) {
            List<Artist> list = new ArrayList<>(db.values());
        if (pageable.isUnpaged()) {
            return new SliceImpl<>(list);
        }
            int start = (int) pageable.getOffset();
            int pageSize = pageable.getPageSize();
            if (start >= list.size()) return new SliceImpl<>(new ArrayList<>(), pageable, false);
            int end = Math.min(start + pageSize, list.size());
            List<Artist> currentSlice = list.subList(start, end);
            boolean hasNext = (start + pageSize) < list.size();
            return new SliceImpl<>(currentSlice, pageable, hasNext);

    }

    public Optional<Artist> findByIdAndActiveTrue(final Long artistId) {
        return Optional.ofNullable(db.get(artistId));
    }

    public int deleteArtistById(final Long id) {

        if(db.containsKey(id)){
            db.remove(id);
            return 1;
        } else {
            return 0;
        }
    }

    public boolean existsById(final Long id) {
        return db.containsKey(id);
    }

    public Artist getReferenceById(final Long id) {
        return null;
    }

    public List<Artist> findByIdInAndActiveTrue(final List<Long> ids) {
        return List.of();
    }
}
