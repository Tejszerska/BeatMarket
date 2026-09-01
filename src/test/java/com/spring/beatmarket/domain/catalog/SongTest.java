package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.exception.DataConflictException;
import com.spring.beatmarket.domain.catalog.exception.MissingRequiredFieldException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class SongTest {

    @Test
    @DisplayName("Should create song when all fields are provided")
    void should_create_song_when_all_fields() {
        //given
        LocalDate date = LocalDate.of(2026, 1, 1);

        Genre genre = new Genre("Genre");
        Album album = new Album("Album", date);
        List<Artist> artists = List.of(new Artist("Main Artist"), new Artist("Featured Artist 1"), new Artist("Featured Artist 1"));

        Song song = Song.builder()
                .title("Title")
                .releaseDate(date)
                .duration(100)
                .language(SongLanguage.EN)
                .genre(genre)
                .album(album)
                .artists(artists)
                .previewUrl("www.previewUrl.com")
                .fileUrl("www.fileUrl.com")
                .build();

        // when & then
        assertThat(song.getId()).isNull();
        assertThat(song.getTitle()).isEqualTo("Title");
        assertThat(song.getReleaseDate()).isEqualTo(date);
        assertThat(song.getDuration()).isEqualTo(100);
        assertThat(song.getLanguage()).isEqualTo(SongLanguage.EN);
        assertThat(song.getGenre()).isEqualTo(genre);
        assertThat(song.getAlbum()).isEqualTo(album);
        assertThat(song.getArtists()).isEqualTo(artists);
        assertThat(song.getPreviewUrl()).isEqualTo("www.previewUrl.com");
        assertThat(song.getFileUrl()).isEqualTo("www.fileUrl.com");
    }

    @Test
    @DisplayName("Should create song when only required fields are provided")
    void should_create_song_when_required_fields() {
        //given
        LocalDate date = LocalDate.of(2026, 1, 1);

        Song song = Song.builder()
                .title("Title")
                .releaseDate(date)
                .duration(100)
                .language(SongLanguage.EN)
                .build();

        // when & then
        assertThat(song.getId()).isNull();
        assertThat(song.getTitle()).isEqualTo("Title");
        assertThat(song.getReleaseDate()).isEqualTo(date);
        assertThat(song.getDuration()).isEqualTo(100);
        assertThat(song.getLanguage()).isEqualTo(SongLanguage.EN);
        assertThat(song.getGenre()).isNull();
        assertThat(song.getAlbum()).isNull();
        assertThat(song.getArtists()).isEmpty();
        assertThat(song.getPreviewUrl()).isNull();
        assertThat(song.getFileUrl()).isNull();
    }

    @ParameterizedTest
    @MethodSource("provideIncompleteSongs")
    @DisplayName("Should throw MissingRequiredFieldException when required field is missing")
    void should_throw_exception_when_song_missing_required_field(Song.SongBuilder songToBuild, String expectedMessage) {
        assertThatThrownBy(songToBuild::build)
                .isInstanceOf(MissingRequiredFieldException.class)
                .hasMessage(expectedMessage);
    }

    private static Stream<Arguments> provideIncompleteSongs() {
        return Stream.of(
                Arguments.of(Song.builder().releaseDate(LocalDate.now()).duration(210).language(SongLanguage.EN),
                        "Required field 'title' cannot be blank or null."),
                Arguments.of(Song.builder().title(" ").releaseDate(LocalDate.now()).duration(210).language(SongLanguage.EN),
                        "Required field 'title' cannot be blank or null."),
                Arguments.of(Song.builder().title("Test").duration(210).language(SongLanguage.EN),
                        "Required field 'releaseDate' cannot be blank or null."),
                Arguments.of(Song.builder().title("Test").releaseDate(LocalDate.now()).language(SongLanguage.EN),
                        "Required field 'duration' cannot be blank or null."),
                Arguments.of(Song.builder().title("Test").duration(210).releaseDate(LocalDate.now()),
                        "Required field 'language' cannot be blank or null.")
        );
    }

    @ParameterizedTest
    @MethodSource("provideImproperValues")
    @DisplayName("Should throw MissingRequiredFieldException when required field is missing")
    void should_throw_exception_when_song_has_improper_field(Song.SongBuilder songToBuild, String expectedMessage) {
        assertThatThrownBy(songToBuild::build)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    private static Stream<Arguments> provideImproperValues() {
        LocalDate validDate = LocalDate.of(2026, 1, 1);

        return Stream.of(
                Arguments.of(baseBuilder().duration(0).releaseDate(validDate),
                        "Duration must be a positive number"),
                Arguments.of(baseBuilder().duration(-1).releaseDate(validDate),
                        "Duration must be a positive number"),
                Arguments.of(baseBuilder().duration(100).releaseDate(LocalDate.of(2126, 1, 1)),
                        "Release date can't be in the future")
        );
    }

    private static Song.SongBuilder baseBuilder() {
        return Song.builder()
                .title("Title")
                .language(SongLanguage.EN);
    }

    @Test
    @DisplayName("Should change song's title")
    void should_change_songs_title() {
        //given
        String oldTitle = "Old title";
        String newTitle = "New title";

        Song song = createCompleteSong(oldTitle);

        //when
        song.changeTitle(newTitle);
        //then
        assertThat(song.getTitle()).isEqualTo(newTitle);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("Should throw MissingRequiredFieldException when changing song's title to invalid value")
    void should_throw_exception_when_improper_title(String newTitle) {
        //given
        String oldTitle = "Old title";
        Song song = createCompleteSong(oldTitle);

        //when & then
        assertThatThrownBy(() -> song.changeTitle(newTitle))
                .isInstanceOf(MissingRequiredFieldException.class)
                .hasMessage("Required field 'title' cannot be blank or null.");
    }

    @Test
    @DisplayName("Should change song's duration")
    void should_change_songs_duration() {
        //given
        Song song = createCompleteSong("Title");
        int newDuration = 111;
        int oldDuration = song.getDuration();

        //when
        song.changeDuration(newDuration);
        //then
        assertThat(song.getDuration()).isEqualTo(newDuration);
        assertThat(song.getDuration()).isNotEqualTo(oldDuration);
    }

    @Test
    @DisplayName("Should throw MissingRequiredFieldException when changing song's duration to null")
    void should_throw_exception_when_null_duration() {
        //given
        Song song = createCompleteSong("Title");
        Integer newDuration = null;
        //when & then
        assertThatThrownBy(() -> song.changeDuration(newDuration))
                .isInstanceOf(MissingRequiredFieldException.class)
                .hasMessage("Required field 'duration' cannot be blank or null.");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -500})
    @DisplayName("Should throw IllegalArgumentException when changing duration to zero or negative")
    void should_throw_exception_when_duration_is_zero_or_negative(Integer invalidDuration) {
        // given
        Song song = createCompleteSong("Title");

        // when & then
        assertThatThrownBy(() -> song.changeDuration(invalidDuration))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Duration must be a positive number");
    }

    @Test
    @DisplayName("Should change song's language")
    void should_change_songs_language() {
        //given
        Song song = createCompleteSong("Title");
        // Zakładam, że masz więcej niż jeden język w enumie, np. PL. Zmień w razie potrzeby.
        SongLanguage newLanguage = SongLanguage.PL;
        SongLanguage oldLanguage = song.getLanguage();

        //when
        song.changeLanguage(newLanguage);
        //then
        assertThat(song.getLanguage()).isEqualTo(newLanguage);
        assertThat(song.getLanguage()).isNotEqualTo(oldLanguage);
    }

    @Test
    @DisplayName("Should throw MissingRequiredFieldException when changing song's language to null")
    void should_throw_exception_when_null_language() {
        //given
        Song song = createCompleteSong("Title");
        SongLanguage newLanguage = null;

        //when & then
        assertThatThrownBy(() -> song.changeLanguage(newLanguage))
                .isInstanceOf(MissingRequiredFieldException.class)
                .hasMessage("Required field 'language' cannot be blank or null.");
    }

    @Test
    @DisplayName("Should change song's release date")
    void should_change_songs_release_date() {
        //given
        Song song = createCompleteSong("Title");
        LocalDate newReleaseDate = LocalDate.of(2020, 1, 1);
        LocalDate oldReleaseDate = song.getReleaseDate();

        //when
        song.changeReleaseDate(newReleaseDate);
        //then
        assertThat(song.getReleaseDate()).isEqualTo(newReleaseDate);
        assertThat(song.getReleaseDate()).isNotEqualTo(oldReleaseDate);
    }

    @Test
    @DisplayName("Should throw MissingRequiredFieldException when changing song's release date to null")
    void should_throw_exception_when_null_release_date() {
        //given
        Song song = createCompleteSong("Title");
        LocalDate newReleaseDate = null;

        //when & then
        assertThatThrownBy(() -> song.changeReleaseDate(newReleaseDate))
                .isInstanceOf(MissingRequiredFieldException.class)
                .hasMessage("Required field 'releaseDate' cannot be blank or null.");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when changing release date to the future")
    void should_throw_exception_when_release_date_is_in_future() {
        // given
        Song song = createCompleteSong("Title");
        LocalDate futureDate = LocalDate.now().plusDays(1);

        // when & then
        assertThatThrownBy(() -> song.changeReleaseDate(futureDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Release date can't be in the future");
    }

    @Test
    @DisplayName("Should change song's preview URL")
    void should_change_songs_preview_url() {
        //given
        Song song = createCompleteSong("Title");
        String newPreviewUrl = "https://example.com/new-preview.mp3";
        String oldPreviewUrl = song.getPreviewUrl();

        //when
        song.changePreviewUrl(newPreviewUrl);

        //then
        assertThat(song.getPreviewUrl()).isEqualTo(newPreviewUrl);
        assertThat(song.getPreviewUrl()).isNotEqualTo(oldPreviewUrl);
    }

    @Test
    @DisplayName("Should allow changing preview URL to null")
    void should_allow_changing_preview_url_to_null() {
        //given
        Song song = createCompleteSong("Title");
        song.changePreviewUrl("https://example.com/preview.mp3");

        //when
        song.changePreviewUrl(null);

        //then
        assertThat(song.getPreviewUrl()).isNull();
    }

    @Test
    @DisplayName("Should change song's file URL")
    void should_change_songs_file_url() {
        //given
        Song song = createCompleteSong("Title");
        String newFileUrl = "https://example.com/new-file.mp3";
        String oldFileUrl = song.getFileUrl();

        //when
        song.changeFileUrl(newFileUrl);

        //then
        assertThat(song.getFileUrl()).isEqualTo(newFileUrl);
        assertThat(song.getFileUrl()).isNotEqualTo(oldFileUrl);
    }

    @Test
    @DisplayName("Should allow changing file URL to null")
    void should_allow_changing_file_url_to_null() {
        //given
        Song song = createCompleteSong("Title");
        song.changeFileUrl("https://example.com/file.mp3");

        //when
        song.changeFileUrl(null);

        //then
        assertThat(song.getFileUrl()).isNull();
    }

    @Test
    @DisplayName("Should assign song's main artist")
    void should_assign_song_main_artist() {
        //given
        Song song = createSongJustRequired();
        Artist artist = new Artist("Main");
        boolean isMain = true;
        //when
        song.assignArtist(artist, isMain);
        //then
        assertThat(song.getArtists()).isNotEmpty();
        assertThat(song.getArtists().get(0)).isEqualTo(artist);
        assertThat(artist.getSongs().contains(song)).isTrue();
    }

    @Test
    @DisplayName("Should throw DataConflictException when assigning feat artist to a song that has no main")
    void should_throw_exception_when_wrongly_assigning_feat_artist() {
        //given
        Song song = createSongJustRequired();
        ReflectionTestUtils.setField(song, "id", 1L);

        Artist artistFeat = new Artist("Featured");
        boolean isMain = false;

        //when & then
        assertThatThrownBy(() -> song.assignArtist(artistFeat, isMain))
                .isInstanceOf(DataConflictException.class)
                .hasMessage("Cannot add featured artist to Song by id='"+ song.getId() +"' without a main artist");
        assertThat(song.getArtists()).hasSize(0);
    }

    @Test
    @DisplayName("Should assign song's featured artist")
    void should_assign_song_feat_artist() {
        //given
        Song song = createSongJustRequired();
        Artist artist = new Artist("Main");
        song.assignArtist(artist, true);

        Artist artistFeat = new Artist("Featured");
        boolean isMain = false;

        //when
        song.assignArtist(artistFeat, isMain);
        //then
        assertThat(song.getArtists()).isNotEmpty();
        assertThat(song.getArtists().get(1)).isEqualTo(artistFeat);
        assertThat(artistFeat.getSongs().contains(song)).isTrue();
    }

    @Test
    @DisplayName("Should throw DataConflictException when assigning main artist to a song that already has one")
    void should_throw_exception_when_wrongly_assigning_main_artist() {
        //given
        Song song = createSongJustRequired();
        ReflectionTestUtils.setField(song, "id", 1L);

        Artist artist = new Artist("Main");
        song.assignArtist(artist, true);

        Artist artistFeat = new Artist("Featured");
        boolean isMain = true;

        //when & then
        assertThatThrownBy(() -> song.assignArtist(artistFeat, isMain))
                .isInstanceOf(DataConflictException.class)
                .hasMessage("Song by id='"+ song.getId() +"' already has a main artist");
        assertThat(song.getArtists()).hasSize(1);
    }

    @Test
    @DisplayName("Should change song's title")
    void xxxxxxxxxx() {
        //given

        //when

        //then
    }


    private Song createCompleteSong(String title) {
        LocalDate date = LocalDate.of(2026, 1, 1);

        Genre genre = new Genre("Genre");
        Album album = new Album("Album", date);
        List<Artist> artists = List.of(new Artist("Main Artist"), new Artist("Featured Artist 1"), new Artist("Featured Artist 1"));

        return Song.builder()
                .title(title)
                .releaseDate(date)
                .duration(100)
                .language(SongLanguage.EN)
                .genre(genre)
                .album(album)
                .artists(artists)
                .previewUrl("https://some-example.com/preview.mp3")
                .fileUrl("https://some-example.com/full-file.mp3")
                .build();
    }

    private Song createSongJustRequired() {
        return Song.builder()
                .title("Title")
                .releaseDate(LocalDate.of(2026, 1, 1))
                .duration(100)
                .language(SongLanguage.EN)
                .build();
    }
}


