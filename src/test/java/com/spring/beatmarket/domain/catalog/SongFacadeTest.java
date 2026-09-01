package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.domain.catalog.exception.MissingRequiredFieldException;
import com.spring.beatmarket.domain.catalog.exception.ResourceNotFoundException;
import com.spring.beatmarket.domain.licensing.LicensingFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class SongFacadeTest {

    private final InMemorySongRepository songRepository = new InMemorySongRepository();

    private final GenreRetriever genreRetriever = mock(GenreRetriever.class);
    private final AlbumRetriever albumRetriever = mock(AlbumRetriever.class);
    private final ArtistRetriever artistRetriever = mock(ArtistRetriever.class);
    private final LicensingFacade licensingFacade = mock(LicensingFacade.class);

    private final SongMapper songMapper = new SongMapperImpl(new AlbumMapperImpl(), new ArtistMapperImpl());
    private final RoleValidator roleValidator = new RoleValidator();

    private final SongFacade songFacade = SongFacadeTestConfiguration.createSongFacade(
            songRepository,
            genreRetriever,
            albumRetriever,
            artistRetriever,
            licensingFacade,
            roleValidator,
            songMapper
    );

    @Test
    @DisplayName("Should find song 'Test' by id 0 ")
    void should_find_song_by_id() {
        // given
        SongDto.Info songDtoGiven = addSong("Test");

        // when
        SongDto.Details songDtoWhen = songFacade.getSongDetails(songDtoGiven.id());

        // then
        assertThat(songDtoWhen.id()).isEqualTo(songDtoGiven.id());
        assertThat(songDtoWhen.id()).isEqualTo(1);
        assertThat(songDtoWhen.title()).isEqualTo("Test");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when it is not found")
    void should_throw_exception_when_song_not_found() {
        // given
        Long nonExistingId = 10L;

        // when & then
        assertThatThrownBy(() -> songFacade.getSongDetails(nonExistingId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Song with id 10 not found or is inactive");
    }

    @Test
    @DisplayName("Should add Song")
    void should_add_song() {
        // given
        SongDto.Create createDto = SongDto.Create.builder()
                .title("Test")
                .releaseDate(LocalDate.now())
                .duration(210)
                .language(SongLanguage.EN)
                .build();

        SongDto.Info songDtoGiven = songFacade.addSong(createDto);

        // when
        SongDto.Details songDtoWhen = songFacade.getSongDetails(songDtoGiven.id());

        // then
        assertThat(songDtoWhen.id()).isEqualTo(songDtoGiven.id());
        assertThat(songDtoWhen.id()).isEqualTo(1);
        assertThat(songDtoWhen.title()).isEqualTo(songDtoGiven.title());
    }

    @ParameterizedTest
    @MethodSource("provideIncompleteCreateSongDtos")
    @DisplayName("Should throw MissingRequiredFieldException when required field is missing")
    void should_throw_exception_when_song_missing_required_field(SongDto.Create dto, String expectedMessage) {
        assertThatThrownBy(() -> songFacade.addSong(dto))
                .isInstanceOf(MissingRequiredFieldException.class)
                .hasMessage(expectedMessage);
    }

    private static Stream<Arguments> provideIncompleteCreateSongDtos() {
        return Stream.of(
                Arguments.of(SongDto.Create.builder().releaseDate(LocalDate.now()).duration(210).language(SongLanguage.EN).build(),
                        "Required field title cannot be blank or null."),
                Arguments.of(SongDto.Create.builder().title("Test").duration(210).language(SongLanguage.EN).build(),
                        "Required field releaseDate cannot be null."),
                Arguments.of(SongDto.Create.builder().title("Test").releaseDate(LocalDate.now()).language(SongLanguage.EN).build(),
                        "Required field duration cannot be null."),
                Arguments.of(SongDto.Create.builder().title("Test").duration(210).releaseDate(LocalDate.now()).build(),
                        "Required field language cannot be null.")
        );
    }

//
//        @Test
//        @DisplayName("Should update Song's required fields")
//        public void should_update_song_partially_just_the_title() {
//            //given
//            SongDto.Info oldSong = addSong("Old title");
//
//            SongDto.Update updateDto = SongDto.Update.builder()
//                    .title(Optional.of("New title"))
//                    .releaseDate(Optional.of(LocalDate.of(2025, 8, 1)))
//                    .duration(Optional.of(215))
//                    .language(Optional.of(SongLanguage.EN))
//                    .genreId(Optional.of(5L))
//                    .mainArtistId(Optional.of(1L))
//                    .featArtistIds(Optional.of(List.of(2L, 3L)))
//                    .albumId(Optional.empty()) // Świadomy brak wartości (np. usunięcie piosenki z albumu)
//                    .build();
//
//            LegacySongDto newSongTitle = LegacySongDto.builder().title("new-title").build();
//            //when
//            SongDto.Info afterUpdate = songFacade.updateSong(oldTitle.id(), newTitle);
//            //then
//            assertThat(updated.id()).isEqualTo(original.id());
//            assertThat(updated.title()).isEqualTo("new-title");
//            LegacySongDto songFromDb = beatmarketCrudFacade.findSongDtoById(original.id());
//            assertThat(songFromDb.title()).isEqualTo("new-title");
//            assertThat(songFromDb.genre()).isEqualTo(original.genre());
//        }

    @Test
    @DisplayName("Should deactivate song by id when song exists")
    public void should_delete_song_by_id_when_song_exists() {
        // given
        SongDto.Info addedSong = addSong("song to deactivate");
        Long idForDeactivating = addedSong.id();
        assertThat(songFacade.getSongDetails(idForDeactivating)).isNotNull();

        // when
        songFacade.deactivateSong(idForDeactivating);

        // then
        assertThatThrownBy(() -> songFacade.getSongDetails(idForDeactivating))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Song with id " + idForDeactivating + " not found or is inactive");
    }

    @Test
    @DisplayName("Should throw SongNotFoundException when trying to delete non-existing song")
    public void should_throw_exception_when_deleting_non_existing_song() {
        // given
        Long nonExistingId = 999L;

        // when & then
        assertThatThrownBy(() -> songFacade.deactivateSong(nonExistingId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Song with id " + nonExistingId + " not found or is inactive");
    }

    private SongDto.Info addSong(String title) {
        SongDto.Create createDto = SongDto.Create.builder()
                .title(title)
                .releaseDate(LocalDate.now())
                .duration(210)
                .language(SongLanguage.EN)
                .build();

        return songFacade.addSong(createDto);
    }
}