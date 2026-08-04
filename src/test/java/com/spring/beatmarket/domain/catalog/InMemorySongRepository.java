package com.spring.beatmarket.domain.catalog;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

class InMemorySongRepository implements SongRepository {
    Map<Long, Song> db = new HashMap<>();
    AtomicInteger index = new AtomicInteger(0);

    @Override
    public Optional<Song> findById(final Long id) {
        return Optional.ofNullable(db.get(id));
    }

    @Override
    public Slice<Long> findSongIds(final Pageable pageable) {
        return null;
    }

    @Override
    public List<Song> findSongsWithDetailsByIds(final List<Long> ids) {
        return List.of();
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
}
