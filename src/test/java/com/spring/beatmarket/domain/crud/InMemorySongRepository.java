package com.spring.beatmarket.domain.crud;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.ArrayList;
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
    public Slice<Song> findAllSongsWithGenre(final Pageable pageable) {
        List<Song> list = new ArrayList<>(db.values());
        if (pageable.isUnpaged()) {
            return new SliceImpl<>(list);
        }
        int start = (int) pageable.getOffset();
        int pageSize = pageable.getPageSize();
        if (start >= list.size()) return new SliceImpl<>(new ArrayList<>(), pageable, false);
        int end = Math.min(start + pageSize, list.size());
        List<Song> currentSlice = list.subList(start, end);
        boolean hasNext = (start + pageSize) < list.size();
        return new SliceImpl<>(currentSlice, pageable, hasNext);
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
