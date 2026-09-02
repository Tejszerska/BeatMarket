package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.domain.catalog.exception.MissingRequiredFieldException;
import com.spring.beatmarket.domain.catalog.exception.ResourceNotFoundException;
import com.spring.beatmarket.domain.licensing.LicensingFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

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

    @Test
    @DisplayName("Should bubble up entity validation exception when creating invalid song")
    void should_bubble_up_validation_exception_when_adding_invalid_song() {
        // given
        SongDto.Create invalidDto = SongDto.Create.builder()
                .releaseDate(LocalDate.now())
                .duration(210)
                .language(SongLanguage.EN)
                .build();

        // when & then
        assertThatThrownBy(() -> songFacade.addSong(invalidDto))
                .isInstanceOf(MissingRequiredFieldException.class)
                .hasMessageContaining("title");
    }

    @Test
    @DisplayName("Should update only provided fields without touching omitted ones")
    void should_partially_update_song_when_some_fields_are_null() {
        // given
        SongDto.Info originalSong = addSong("Original Title");
        String newTitle = "Updated Title";
        Integer newDuration = 320;

        SongDto.Update updateDto = SongDto.Update.builder()
                .title(Optional.of(newTitle))
                .duration(Optional.of(newDuration))
                .build();

        // when
        SongDto.Info updatedSong = songFacade.updateSong(originalSong.id(), updateDto);
        SongDto.Details detailsAfterUpdate = songFacade.getSongDetails(originalSong.id());

        // then
        assertThat(updatedSong.id()).isEqualTo(originalSong.id());
        assertThat(updatedSong.title()).isEqualTo(newTitle);
        assertThat(detailsAfterUpdate.title()).isEqualTo(newTitle);
        assertThat(detailsAfterUpdate.duration()).isEqualTo(newDuration);
        assertThat(detailsAfterUpdate.language()).isEqualTo(originalSong.language());
    }

    @Test
    @DisplayName("Should detach album when albumId is explicitly empty")
    void should_detach_album_when_album_id_is_empty_optional() {
        // given
        Long albumId = 5L;
        Album mockAlbum = new Album("Existing Album", LocalDate.of(2026, 1, 1));
        org.mockito.Mockito.when(albumRetriever.getActive(albumId)).thenReturn(mockAlbum);

        SongDto.Info song = addSong("Song to detach album");

        SongDto.Update attachAlbumDto = SongDto.Update.builder()
                .albumId(Optional.of(albumId))
                .build();
        songFacade.updateSong(song.id(), attachAlbumDto);
        assertThat(songFacade.getSongDetails(song.id()).album()).isNotNull();

        SongDto.Update detachAlbumDto = SongDto.Update.builder()
                .albumId(Optional.empty())
                .build();

        // when
        songFacade.updateSong(song.id(), detachAlbumDto);
        SongDto.Details detailsAfterDetach = songFacade.getSongDetails(song.id());

        // then
        assertThat(detailsAfterDetach.album()).isNull();
    }

    @Test
    @DisplayName("Should throw MissingRequiredFieldException when explicitly updating required field with empty optional")
    void should_throw_exception_when_required_field_is_empty_optional() {
        // given
        SongDto.Info song = addSong("Title");

        SongDto.Update updateDto = SongDto.Update.builder()
                .title(Optional.empty())
                .build();

        // when & then
        assertThatThrownBy(() -> songFacade.updateSong(song.id(), updateDto))
                .isInstanceOf(MissingRequiredFieldException.class)
                .hasMessage("Required field 'title' cannot be blank or null.");
    }

    @Test
    @DisplayName("Should bubble up entity invariant exception when updating with negative duration")
    void should_bubble_up_entity_exception_when_duration_is_invalid() {
        // given
        SongDto.Info song = addSong("Title");

        SongDto.Update updateDto = SongDto.Update.builder()
                .duration(Optional.of(-150))
                .build();

        // when & then
        assertThatThrownBy(() -> songFacade.updateSong(song.id(), updateDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Duration must be a positive number");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existing song")
    void should_throw_exception_when_updating_non_existing_song() {
        // given
        Long nonExistingId = 999L;
        SongDto.Update updateDto = SongDto.Update.builder()
                .title(Optional.of("New Title"))
                .build();

        // when & then
        assertThatThrownBy(() -> songFacade.updateSong(nonExistingId, updateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Song with id " + nonExistingId + " not found or is inactive");
    }

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