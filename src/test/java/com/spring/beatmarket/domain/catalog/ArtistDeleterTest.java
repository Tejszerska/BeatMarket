package com.spring.beatmarket.domain.catalog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ArtistDeleterTest {

    private final ArtistRetriever artistRetriever = mock(ArtistRetriever.class);
    private final SongDeleter songDeleter = mock(SongDeleter.class);
    private final AlbumDeleter albumDeleter = mock(AlbumDeleter.class);

    private final ArtistDeleter artistDeleter = new ArtistDeleter(artistRetriever, songDeleter, albumDeleter);

    @Test
    @DisplayName("Should cascade deactivate only to entities where artist is the main artist (index 0)")
    void should_cascade_delete_only_main_roles() {
        // given
        Long targetArtistId = 1L;
        Artist targetArtist = new Artist("Target Artist");
        ReflectionTestUtils.setField(targetArtist, "id", targetArtistId);

        Artist otherArtist = new Artist("Other Artist");
        ReflectionTestUtils.setField(otherArtist, "id", 2L);

        Song mainSong = Song.builder().title("Main song").releaseDate(LocalDate.now()).duration(100).language(SongLanguage.EN).build();
        ReflectionTestUtils.setField(mainSong, "id", 101L);
        mainSong.assignArtist(targetArtist, true);

        Song featSong = Song.builder().title("Feat song").releaseDate(LocalDate.now()).duration(100).language(SongLanguage.EN).build();
        ReflectionTestUtils.setField(featSong, "id", 102L);
        featSong.assignArtist(otherArtist, true);
        featSong.assignArtist(targetArtist, false);

        Album mainAlbum = Album.builder().title("Main Album").build();
        ReflectionTestUtils.setField(mainAlbum, "id", 201L);
        mainAlbum.assignArtist(targetArtist, true);

        Album featAlbum = Album.builder().title("Feat Album").build();
        ReflectionTestUtils.setField(featAlbum, "id", 202L);
        featAlbum.assignArtist(otherArtist, true);
        featAlbum.assignArtist(targetArtist, false);

        when(artistRetriever.findEagerly(targetArtistId)).thenReturn(targetArtist);
        // when
        artistDeleter.deactivate(targetArtistId);

        // then
        verify(songDeleter).bulkDeactivate(Set.of(101L));
        verify(albumDeleter).bulkDeactivate(Set.of(201L));
    }
}