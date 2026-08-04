package com.spring.beatmarket.domain.catalog.dto;

import java.time.LocalDate;
import java.util.Set;


public interface AlbumInfo {
    Long getId();

    String getTitle();

    LocalDate getReleaseDate();

    Set<SongInfo> getSongs();

    Set<ArtistInfo> getArtists();


    interface SongInfo {
        Long getId();

        String getTitle();

        LocalDate getReleaseDate();

        Long getDuration();

    }


    interface ArtistInfo {
        Long getId();

        String getName();
    }
}