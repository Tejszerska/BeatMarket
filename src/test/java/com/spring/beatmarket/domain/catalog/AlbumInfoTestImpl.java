package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumInfo;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

class AlbumInfoTestImpl implements AlbumInfo {
    private final Album album;

    AlbumInfoTestImpl(final Album album) {
        this.album = album;
    }

    @Override
    public Long getId() {
        return album.getId();
    }

    @Override
    public String getTitle() {
        return album.getTitle();
    }

    @Override
    public LocalDate getReleaseDate() {
        return album.getReleaseDate();
    }

    @Override
    public Set<SongInfo> getSongs() {
        return album.getSongs().stream()
                .map(SongInfoTestImpl::new)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<ArtistInfo> getArtists() {
        return album.getArtists().stream()
                .map(ArtistInfoTestImpl::new)
                .collect(Collectors.toSet());
    }
}
