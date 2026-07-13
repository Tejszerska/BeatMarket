package com.spring.beatmarket.domain.catalog.dto;

import java.time.Instant;
import java.util.Set;


public interface AlbumInfo {
    Long getId();

    String getTitle();

    Instant getReleaseDate();

    Set<SongInfo> getSongs();

    Set<ArtistInfo> getArtists();


    interface SongInfo {
        Long getId();

        String getTitle();

        Instant getReleaseDate();

        Long getDuration();

    }


    interface ArtistInfo {
        Long getId();

        String getName();
    }
}