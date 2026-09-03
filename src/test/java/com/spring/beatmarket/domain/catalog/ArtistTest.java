package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.exception.MissingRequiredFieldException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ArtistTest {

    @Test
    @DisplayName("Should create artist when all fields are provided via builder")
    void should_create_artist_when_all_fields() {
        // given
        Song song = Song.builder()
                .title("Test Song")
                .releaseDate(LocalDate.now())
                .duration(120)
                .language(SongLanguage.EN)
                .build();

        Album album = new Album("Test Album", LocalDate.now());

        Set<Song> songs = Set.of(song);
        List<Album> albums = List.of(album);

        // when
        Artist artist = Artist.builder()
                .name("Artist Name")
                .imageUrl("http://example.com/image.jpg")
                .songs(songs)
                .albums(albums)
                .build();

        // then
        assertThat(artist.getId()).isNull();
        assertThat(artist.getName()).isEqualTo("Artist Name");
        assertThat(artist.getImageUrl()).isEqualTo("http://example.com/image.jpg");
        assertThat(artist.getSongs()).containsExactly(song);
        assertThat(artist.getAlbums()).containsExactly(album);
    }

    @Test
    @DisplayName("Should create artist using basic constructor with required name only")
    void should_create_artist_when_only_required_fields() {
        // when
        Artist artist = new Artist("Simple Artist");

        // then
        assertThat(artist.getId()).isNull();
        assertThat(artist.getName()).isEqualTo("Simple Artist");
        assertThat(artist.getImageUrl()).isNull();
        assertThat(artist.getSongs()).isEmpty();
        assertThat(artist.getAlbums()).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("Should throw MissingRequiredFieldException when creating artist with invalid name")
    void should_throw_exception_when_creating_artist_with_invalid_name(String invalidName) {
        // when & then
        assertThatThrownBy(() -> Artist.builder().name(invalidName).build())
                .isInstanceOf(MissingRequiredFieldException.class)
                .hasMessage("Required field 'name' cannot be blank or null.");
    }

    @Test
    @DisplayName("Should change artist's name and trim whitespaces")
    void should_change_artists_name_and_trim() {
        // given
        Artist artist = new Artist("Old Name");
        String newName = "  New Name  ";

        // when
        artist.changeName(newName);

        // then
        assertThat(artist.getName()).isEqualTo("New Name");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("Should throw MissingRequiredFieldException when changing name to invalid value")
    void should_throw_exception_when_changing_name_to_invalid_value(String invalidName) {
        // given
        Artist artist = new Artist("Valid Name");

        // when & then
        assertThatThrownBy(() -> artist.changeName(invalidName))
                .isInstanceOf(MissingRequiredFieldException.class)
                .hasMessage("Required field 'name' cannot be blank or null.");
    }

    @Test
    @DisplayName("Should change artist's image URL")
    void should_change_artists_image_url() {
        // given
        Artist artist = new Artist("Artist Name");
        String newUrl = "http://example.com/new.jpg";

        // when
        artist.changeImageUrl(newUrl);

        // then
        assertThat(artist.getImageUrl()).isEqualTo(newUrl);
    }

    @Test
    @DisplayName("Should allow changing image URL to null")
    void should_allow_changing_image_url_to_null() {
        // given
        Artist artist = new Artist("Artist Name");
        artist.changeImageUrl("http://example.com/old.jpg");

        // when
        artist.changeImageUrl(null);

        // then
        assertThat(artist.getImageUrl()).isNull();
    }

    @Test
    @DisplayName("Should add and remove album passively")
    void should_add_and_remove_album() {
        // given
        Artist artist = new Artist("Artist Name");
        Album album = new Album("Album", LocalDate.now());

        // when
        artist.addAlbum(album);

        // then
        assertThat(artist.getAlbums()).containsExactly(album);

        // when
        artist.removeAlbum(album);

        // then
        assertThat(artist.getAlbums()).isEmpty();
    }

    @Test
    @DisplayName("Should safely ignore null when adding or removing album")
    void should_ignore_null_when_adding_or_removing_album() {
        // given
        Artist artist = new Artist("Artist Name");

        // when
        artist.addAlbum(null);

        // then
        assertThat(artist.getAlbums()).isEmpty();

        // when
        artist.removeAlbum(null);

        // then
        assertThat(artist.getAlbums()).isEmpty();
    }

    @Test
    @DisplayName("Should add and remove song passively")
    void should_add_and_remove_song() {
        // given
        Artist artist = new Artist("Artist Name");
        Song song = Song.builder()
                .title("Title")
                .releaseDate(LocalDate.now())
                .duration(100)
                .language(SongLanguage.EN)
                .build();

        // when
        artist.addSong(song);

        // then
        assertThat(artist.getSongs()).containsExactly(song);

        // when
        artist.removeSong(song);

        // then
        assertThat(artist.getSongs()).isEmpty();
    }

    @Test
    @DisplayName("Should safely ignore null when adding or removing song")
    void should_ignore_null_when_adding_or_removing_song() {
        // given
        Artist artist = new Artist("Artist Name");

        // when
        artist.addSong(null);

        // then
        assertThat(artist.getSongs()).isEmpty();

        // when
        artist.removeSong(null);

        // then
        assertThat(artist.getSongs()).isEmpty();
    }
}