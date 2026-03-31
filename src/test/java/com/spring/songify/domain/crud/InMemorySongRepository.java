package com.spring.songify.domain.crud;

import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

class InMemorySongRepository implements SongRepository {
    Map<Long, Song> db = new HashMap<>();
    AtomicInteger index = new AtomicInteger(0);

    @Override
    public Set<Song> findAll(final Pageable pageable) {
        return new HashSet<>(db.values());
    }

    @Override
    public Optional<Song> findById(final Long id) {
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
}
