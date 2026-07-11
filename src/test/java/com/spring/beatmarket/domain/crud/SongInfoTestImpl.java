package com.spring.beatmarket.domain.crud;

import com.spring.beatmarket.domain.crud.dto.AlbumInfo;

import java.time.Instant;

class SongInfoTestImpl implements AlbumInfo.SongInfo {
    private final Song song;

    SongInfoTestImpl(final Song song) {
        this.song = song;
    }

    @Override
    public Long getId() {
        return song.getId();
    }

    @Override
    public String getTitle() {
        return song.getTitle();
    }

    @Override
    public Instant getReleaseDate() {
        return song.getReleaseDate();
    }

    @Override
    public Long getDuration() {
        return song.getDuration();
    }
}
