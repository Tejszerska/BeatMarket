package com.spring.songify.domain.crud;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

class InMemoryArtistRepository implements ArtistRepository {
    Map<Long, Artist> db = new HashMap<>();
    AtomicInteger index = new AtomicInteger(0);

    @Override
    public Artist save(final Artist artist) {
        if(artist.getId() == null){
            long index = this.index.getAndIncrement();
            db.put(index, artist);
            artist.setId(index);
            return artist;
        } else {
            db.put(artist.getId(), artist);
            return artist;
        }

    }

    @Override
    public Slice<Artist> findAll(final Pageable pageable) {
        List<Artist> list = new ArrayList<>(db.values());
        int start = (int) pageable.getOffset();
        int pageSize = pageable.getPageSize();
        if (start >= list.size()) return new SliceImpl<>(new ArrayList<>(), pageable, false);
        int end = Math.min(start + pageSize, list.size());
        List<Artist> currentSlice = list.subList(start, end);
        boolean hasNext = (start + pageSize) < list.size();
        return new SliceImpl<>(currentSlice, pageable, hasNext);
    }
    @Override
    public Optional<Artist> findById(final Long artistId) {
        return Optional.ofNullable(db.get(artistId));
    }

    @Override
    public int deleteArtistById(final Long id) {

        if(db.containsKey(id)){
            db.remove(id);
            return 1;
        } else {
            return 0;
        }
    }
}
