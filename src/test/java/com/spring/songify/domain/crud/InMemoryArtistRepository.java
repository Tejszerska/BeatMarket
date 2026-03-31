package com.spring.songify.domain.crud;


import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.HashSet;
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
    public Set<Artist> findAll(final Pageable pageable) {
        return new HashSet<>(db.values());
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
