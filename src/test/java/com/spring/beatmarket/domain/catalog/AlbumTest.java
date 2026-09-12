package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.exception.MissingRequiredFieldException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class AlbumTest {

    @Test
    @DisplayName("Should create album when all fields are provided")
    void should_create_album_when_all_fields() {
        //given
        LocalDate date = LocalDate.of(2026, 1, 1);
        List<Artist> artists = List.of(new Artist("Main Artist"), new Artist("Featured Artist"));
        Set<Song> songs = Set.of(Song.builder().title("Song 1").releaseDate(date).language(SongLanguage.EN).duration(100).build());

        //when
        Album album = Album.builder()
                .title("Album Title")
                .releaseDate(date)
                .coverUrl("www.coverUrl.com")
                .artists(artists)
                .songs(songs)
                .build();

        //then
        assertThat(album.getId()).isNull();
        assertThat(album.getTitle()).isEqualTo("Album Title");
        assertThat(album.getReleaseDate()).isEqualTo(date);
        assertThat(album.getCoverUrl()).isEqualTo("www.coverUrl.com");
        assertThat(album.getArtists()).isEqualTo(artists);
        assertThat(album.getSongs()).isEqualTo(songs);
    }

    @Test
    @DisplayName("Should create album when only required fields are provided")
    void should_create_album_when_required_fields() {
        //when
        Album album = Album.builder()
                .title("Album Title")
                .build();

        //then
        assertThat(album.getId()).isNull();
        assertThat(album.getTitle()).isEqualTo("Album Title");
        assertThat(album.getReleaseDate()).isNull();
        assertThat(album.getCoverUrl()).isNull();
        assertThat(album.getArtists()).isEmpty();
        assertThat(album.getSongs()).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw MissingRequiredFieldException when title is missing or blank")
    void should_throw_exception_when_title_is_missing(String invalidTitle) {
        //given
        Album.AlbumBuilder builder = Album.builder().title(invalidTitle);

        //when & then
        assertThatThrownBy(builder::build)
                .isInstanceOf(MissingRequiredFieldException.class)
                .hasMessage("Required field 'title' cannot be blank or null.");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when release date is in the future")
    void should_throw_exception_when_release_date_in_future() {
        //given
        LocalDate futureDate = LocalDate.now().plusDays(1);
        Album.AlbumBuilder builder = Album.builder().title("Title").releaseDate(futureDate);

        //when & then
        assertThatThrownBy(builder::build)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Release date can't be in the future");
    }

    @Test
    @DisplayName("Should change album's title")
    void should_change_albums_title() {
        //given
        Album album = Album.builder().title("Old Title").build();
        String newTitle = "New Title";

        //when
        album.changeTitle(newTitle);

        //then
        assertThat(album.getTitle()).isEqualTo(newTitle);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw MissingRequiredFieldException when changing title to invalid value")
    void should_throw_exception_when_changing_to_improper_title(String invalidTitle) {
        //given
        Album album = Album.builder().title("Valid Title").build();

        //when & then
        assertThatThrownBy(() -> album.changeTitle(invalidTitle))
                .isInstanceOf(MissingRequiredFieldException.class)
                .hasMessage("Required field 'title' cannot be blank or null.");
    }

    @Test
    @DisplayName("Should change album's release date")
    void should_change_albums_release_date() {
        //given
        Album album = Album.builder().title("Title").build();
        LocalDate newDate = LocalDate.of(2020, 1, 1);

        //when
        album.changeReleaseDate(newDate);

        //then
        assertThat(album.getReleaseDate()).isEqualTo(newDate);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when changing release date to the future")
    void should_throw_exception_when_changing_release_date_to_future() {
        //given
        Album album = Album.builder().title("Title").build();
        LocalDate futureDate = LocalDate.now().plusDays(1);

        //when & then
        assertThatThrownBy(() -> album.changeReleaseDate(futureDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Release date can't be in the future");
    }

    @Test
    @DisplayName("Should allow changing release date to null")
    void should_allow_changing_release_date_to_null() {
        //given
        Album album = Album.builder().title("Title").releaseDate(LocalDate.now()).build();

        //when
        album.changeReleaseDate(null);

        //then
        assertThat(album.getReleaseDate()).isNull();
    }


    @Test
    @DisplayName("Should change album's cover URL")
    void should_change_albums_cover_url() {
        //given
        Album album = Album.builder().title("Title").build();
        String newUrl = "https://example.com/cover.jpg";

        //when
        album.changeCoverUrl(newUrl);

        //then
        assertThat(album.getCoverUrl()).isEqualTo(newUrl);
    }

    @Test
    @DisplayName("Should allow changing cover URL to null")
    void should_allow_changing_cover_url_to_null() {
        //given
        Album album = Album.builder().title("Title").coverUrl("url").build();

        //when
        album.changeCoverUrl(null);

        //then
        assertThat(album.getCoverUrl()).isNull();
    }

    @Test
    @DisplayName("Should assign main artist to index 0")
    void should_assign_main_artist() {
        //given
        Album album = Album.builder().title("Title").build();
        Artist artist = new Artist("Main");

        //when
        album.assignArtist(artist, true);

        //then
        assertThat(album.getArtists()).hasSize(1);
        assertThat(album.getArtists().get(0)).isEqualTo(artist);
        assertThat(artist.getAlbums()).contains(album);
    }

    @Test
    @DisplayName("Should assign featured artist to the end of the list")
    void should_assign_featured_artist() {
        //given
        Album album = Album.builder().title("Title").build();
        Artist mainArtist = new Artist("Main");
        album.assignArtist(mainArtist, true);

        Artist featArtist = new Artist("Featured");

        //when
        album.assignArtist(featArtist, false);

        //then
        assertThat(album.getArtists()).hasSize(2);
        assertThat(album.getArtists().get(1)).isEqualTo(featArtist);
        assertThat(featArtist.getAlbums()).contains(album);
    }


    @Test
    @DisplayName("Should safely return without modifying state when assigning null artist")
    void should_do_nothing_when_assigning_null_artist() {
        //given
        Album album = Album.builder().title("Title").build();
        Artist mainArtist = null;

        //when
        album.assignArtist(mainArtist, true);

        //then
        assertThat(album.getArtists()).hasSize(0);
    }

    @Test
    @DisplayName("Should safely assign artist when it is already assigned")
    void should_assign_artist_when_it_is_already_assigned() {
        //given
        Album album = Album.builder().title("Title").build();
        Artist artist = new Artist("Artist");
        Artist artist2 = new Artist("Artist 2");
        album.assignArtist(artist, true);
        album.assignArtist(artist2, false);

        //when
        album.assignArtist(artist, true);

        //then
        assertThat(album.getArtists()).hasSize(2);
    }

    @Test
    @DisplayName("Should remove artist bidirectionally")
    void should_remove_artist() {
        //given
        Album album = Album.builder().title("Title").build();
        Artist artist = new Artist("Main");
        album.assignArtist(artist, true);

        //when
        album.removeArtist(artist);

        //then
        assertThat(album.getArtists()).isEmpty();
        assertThat(artist.getAlbums()).doesNotContain(album);
    }

    @Test
    @DisplayName("Should safely return without modifying state when removing null artist")
    void should_do_nothing_when_removing_null_artist() {
        //given
        Album album = Album.builder().title("Title").build();
        Artist mainArtist = new Artist("Main");
        album.assignArtist(mainArtist, true);

        //when
        album.removeArtist(null);

        //then
        assertThat(album.getArtists()).hasSize(1);
    }


    @Test
    @DisplayName("Should safely return without modifying state when removing unassigned artist")
    void should_do_nothing_when_removing_unassigned_artist() {
        //given
        Album album = Album.builder().title("Title").build();
        Artist mainArtist = new Artist("Main");

        //when
        album.removeArtist(mainArtist);

        //then
        assertThat(album.getArtists()).hasSize(0);
    }

    @Test
    @DisplayName("Should add song to album and synchronize bidirectional relationship")
    void should_add_song() {
        //given
        Album album = Album.builder().title("Title").build();
        Song song = Song.builder().title("Song").releaseDate(LocalDate.now()).language(SongLanguage.EN).duration(100).build();

        //when
        album.addSong(song);

        //then
        assertThat(album.getSongs()).contains(song);
        assertThat(song.getAlbum()).isEqualTo(album);
    }

    @Test
    @DisplayName("Should not add null song to album")
    void should_not_add_null_song() {
        //given
        Album album = Album.builder().title("Title").build();
        Song song = null;

        //when
        album.addSong(song);

        //then
        assertThat(album.getSongs()).doesNotContain(song);
        assertThat(album.getSongs()).hasSize(0);
    }

    @Test
    @DisplayName("Should prevent duplicates when adding the same song twice")
    void should_prevent_duplicates_when_adding_song() {
        //given
        Album album = Album.builder().title("Title").build();
        Song song = Song.builder().title("Song").releaseDate(LocalDate.now()).language(SongLanguage.EN).duration(100).build();
        album.addSong(song);

        //when
        album.addSong(song);

        //then
        assertThat(album.getSongs()).hasSize(1);
    }

    @Test
    @DisplayName("Should remove song and synchronize bidirectional relationship")
    void should_remove_song() {
        //given
        Album album = Album.builder().title("Title").build();
        Song song = Song.builder().title("Song").releaseDate(LocalDate.now()).language(SongLanguage.EN).duration(100).build();
        album.addSong(song);

        //when
        album.removeSong(song);

        //then
        assertThat(album.getSongs()).doesNotContain(song);
        assertThat(song.getAlbum()).isNull();
    }

    @Test
    @DisplayName("Should safely not remove song when it is null ")
    void should_not_remove_null_song() {
        //given
        Album album = Album.builder().title("Title").build();
        Song song = Song.builder().title("Song").releaseDate(LocalDate.now()).language(SongLanguage.EN).duration(100).build();
        album.addSong(song);
        Song songNull = null;

        //when
        album.removeSong(songNull);

        //then
        assertThat(album.getSongs()).doesNotContain(songNull);
        assertThat(album.getSongs()).hasSize(1);
    }

    @Test
    @DisplayName("Should safely not remove song when it is not part of album ")
    void should_not_remove_absent_song() {
        //given
        Album album = Album.builder().title("Title").build();
        Song song = Song.builder().title("Song").releaseDate(LocalDate.now()).language(SongLanguage.EN).duration(100).build();

        //when
        album.removeSong(song);

        //then
        assertThat(album.getSongs()).doesNotContain(song);
        assertThat(album.getSongs()).isEmpty();
    }

    @Test
    @DisplayName("Should clear all artists")
    void should_clear_artists() {
        //given
        Album album = Album.builder().title("Title").build();
        Artist artist1 = new Artist("A1");
        Artist artist2 = new Artist("A2");
        album.assignArtist(artist1, true);
        album.assignArtist(artist2, false);

        //when
        album.clearArtists();

        //then
        assertThat(album.getArtists()).isEmpty();
        assertThat(artist1.getAlbums()).doesNotContain(album);
        assertThat(artist2.getAlbums()).doesNotContain(album);
    }

    @Test
    @DisplayName("Should do nothing when clearing empty artists")
    void should_not_clear_artists() {
        //given
        Album album = Album.builder().title("Title").build();
        assertThat(album.getArtists()).hasSize(0);

        //when
        album.clearArtists();

        //then
        assertThat(album.getArtists()).isEmpty();
    }

    @Test
    @DisplayName("Should clear all songs")
    void should_clear_songs() {
        //given
        Album album = Album.builder().title("Title").build();
        Song song1 = Song.builder().title("S1").releaseDate(LocalDate.now()).language(SongLanguage.EN).duration(100).build();
        Song song2 = Song.builder().title("S2").releaseDate(LocalDate.now()).language(SongLanguage.EN).duration(100).build();
        album.addSong(song1);
        album.addSong(song2);

        //when
        album.clearSongs();

        //then
        assertThat(album.getSongs()).isEmpty();
        assertThat(song1.getAlbum()).isNull();
        assertThat(song2.getAlbum()).isNull();
    }
}