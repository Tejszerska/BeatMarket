package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import com.spring.beatmarket.domain.catalog.dto.SongDto;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

class TestObjectsFactory {

    public static ArtistDto.Create createArtistDto(String name) {
        return ArtistDto.Create.builder()
                .name(name)
                .build();
    }

    public static SongDto.Create createSongDto(String title) {
        return SongDto.Create.builder()
                .title(title)
                .releaseDate(LocalDate.now())
                .duration(210)
                .language(SongLanguage.EN)
                .build();
    }

    public static Artist createArtistWithId(Long id, String name) {
        Artist artist = Artist.builder().name(name).build();
        ReflectionTestUtils.setField(artist, "id", id);
        return artist;
    }

    public static Song createSongWithId(Long id, String title) {
        Song song = Song.builder()
                .title(title)
                .releaseDate(LocalDate.now())
                .duration(200)
                .language(SongLanguage.EN)
                .build();
        ReflectionTestUtils.setField(song, "id", id);
        return song;
    }

    public static Album createAlbumWithId(Long id, String title) {
        Album album = Album.builder().title(title).build();
        ReflectionTestUtils.setField(album, "id", id);
        return album;
    }
}
