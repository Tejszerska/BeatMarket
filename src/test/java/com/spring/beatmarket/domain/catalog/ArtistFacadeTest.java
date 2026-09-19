package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.domain.catalog.exception.MainRoleAbsentException;
import com.spring.beatmarket.domain.catalog.exception.MissingRequiredFieldException;
import com.spring.beatmarket.domain.catalog.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ArtistFacadeTest {

    private final InMemoryArtistRepository artistRepository = new InMemoryArtistRepository();

    private final SongRetriever songRetriever = mock(SongRetriever.class);
    private final AlbumRetriever albumRetriever = mock(AlbumRetriever.class);
    private final SongDeleter songDeleter = mock(SongDeleter.class);
    private final AlbumDeleter albumDeleter = mock(AlbumDeleter.class);

    private final ArtistMapper artistMapper = new ArtistMapperImpl();
    private final RoleValidator roleValidator = new RoleValidator();

    private final ArtistFacade artistFacade = createArtistFacade();

    private ArtistFacade createArtistFacade() {
        ArtistAdder artistAdder = new ArtistAdder(artistRepository, albumRetriever, songRetriever, roleValidator, artistMapper);
        ArtistRetriever artistRetriever = new ArtistRetriever(artistRepository, artistMapper);
        ArtistDeleter artistDeleter = new ArtistDeleter(artistRetriever, songDeleter, albumDeleter);
        ArtistUpdater artistUpdater = new ArtistUpdater(artistRetriever, artistMapper, songRetriever, albumRetriever, roleValidator);

        return new ArtistFacadeImpl(artistAdder, artistRetriever, artistDeleter, artistUpdater);
    }

    @Test
    @DisplayName("Should return slice with all artists when no filtering present")
    void should_return_all_artists_in_slice() {
        // given
        prepareArtistsForFiltering();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<ArtistDto.Summary> slice = artistFacade.findAllArtists(null, pageable);

        // then
        assertThat(slice.getContent()).hasSize(3);
    }

    @Test
    @DisplayName("Should return slice with some artists when filtering by name")
    void should_return_some_artists_in_slice_filtering_name() {
        // given
        prepareArtistsForFiltering();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<ArtistDto.Summary> slice = artistFacade.findAllArtists("nAm", pageable);

        // then
        assertThat(slice.getContent()).hasSize(2);
    }

    @ParameterizedTest
    @ValueSource(strings = {"absent", "nonexistent"})
    @DisplayName("Should return empty slice when no artist matches filtering criteria")
    void should_return_empty_slice_when_no_artist_matches_filtering(String name) {
        // given
        prepareArtistsForFiltering();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<ArtistDto.Summary> slice = artistFacade.findAllArtists(name, pageable);

        // then
        assertThat(slice.getContent()).isEmpty();
    }

    private void prepareArtistsForFiltering() {
        addArtist("Name 1");
        addArtist("Name 2");
        addArtist("Won't be filtered by n...");
        ArtistDto.Info inactiveArtist = addArtist("Name 4");
        artistFacade.deactivateArtist(inactiveArtist.id());
    }

    @Test
    @DisplayName("Should find artist by id")
    void should_find_artist_by_id() {
        // given
        ArtistDto.Info artistDtoGiven = addArtist("Test Artist");

        // when
        ArtistDto.Details artistDtoWhen = artistFacade.getArtistDetails(artistDtoGiven.id());

        // then
        assertThat(artistDtoWhen.id()).isEqualTo(artistDtoGiven.id());
        assertThat(artistDtoWhen.name()).isEqualTo("Test Artist");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when artist is not found")
    void should_throw_exception_when_artist_not_found() {
        // given
        Long nonExistingId = 10L;

        // when & then
        assertThatThrownBy(() -> artistFacade.getArtistDetails(nonExistingId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Artist");
    }

    @Test
    @DisplayName("Should add Artist")
    void should_add_artist() {
        // given
        ArtistDto.Create createDto = TestObjectsFactory.createArtistDto("New Artist");

        ArtistDto.Info artistDtoGiven = artistFacade.addArtist(createDto);

        // when
        ArtistDto.Details artistDtoWhen = artistFacade.getArtistDetails(artistDtoGiven.id());

        // then
        assertThat(artistDtoWhen.id()).isEqualTo(artistDtoGiven.id());
        assertThat(artistDtoWhen.name()).isEqualTo(createDto.name());
    }

    @Test
    @DisplayName("Should add Artist with Albums and Songs as main and feat")
    void should_add_artist_with_all() {
        // given
        Artist otherArtist = TestObjectsFactory.createArtistWithId(10L, "Other Artist");

        Set<Long> songIds = Set.of(1L, 2L);
        Song song1 = TestObjectsFactory.createSongWithId(1L, "S1");
        Song song2 = TestObjectsFactory.createSongWithId(2L, "S2");
        song2.assignArtist(otherArtist, true);
        Mockito.when(songRetriever.getActiveWithArtist(songIds)).thenReturn(List.of(song1, song2));

        Set<Long> albumIds = Set.of(101L, 102L);
        Album album1 = TestObjectsFactory.createAlbumWithId(101L, "Al1");
        Album album2 = TestObjectsFactory.createAlbumWithId(102L, "Al2");
        album2.assignArtist(otherArtist, true);
        Mockito.when(albumRetriever.getActiveWithArtist(albumIds)).thenReturn(List.of(album1, album2));

        ArtistDto.Create createDto = ArtistDto.Create.builder()
                .name("Artist")
                .mainSongIds(List.of(1L))
                .featSongIds(List.of(2L))
                .mainAlbumIds(List.of(101L))
                .featAlbumIds(List.of(102L))
                .build();

        // when
        ArtistDto.Info artistDtoInfo = artistFacade.addArtist(createDto);

        // then
        assertThat(artistDtoInfo.name()).isEqualTo(createDto.name());
        assertThat(artistDtoInfo.songs()).hasSize(2);
        assertThat(artistDtoInfo.albums()).hasSize(2);
    }

    @Test
    @DisplayName("Should throw exception when adding artist as a featured in Album that has no main")
    void should_throw_exception_when_artist_featured_Album_has_no_main() {
        // given
        Set<Long> albumIds = Set.of(101L, 102L);
        Album album1 = TestObjectsFactory.createAlbumWithId(101L, "Al1");
        Album album2 = TestObjectsFactory.createAlbumWithId(102L, "Al2");
        Mockito.when(albumRetriever.getActiveWithArtist(albumIds)).thenReturn(List.of(album1, album2));

        ArtistDto.Create createDto = ArtistDto.Create.builder()
                .name("Artist")
                .mainAlbumIds(List.of(101L))
                .featAlbumIds(List.of(102L))
                .build();

        // when & then
        assertThatThrownBy(() -> artistFacade.addArtist(createDto))
                .isInstanceOf(MainRoleAbsentException.class)
                .hasMessage("Cannot update Albums featured artists when main artist isn't specified.");
    }

    @Test
    @DisplayName("Should throw exception when adding artist as a featured in Song that has no main")
    void should_throw_exception_when_artist_featured_song_has_no_main() {
        Set<Long> songIds = Set.of(1L, 2L);
        Song song1 = TestObjectsFactory.createSongWithId(1L, "S1");
        Song song2 = TestObjectsFactory.createSongWithId(2L, "S2");
        Mockito.when(songRetriever.getActiveWithArtist(songIds)).thenReturn(List.of(song1, song2));


        ArtistDto.Create createDto = ArtistDto.Create.builder()
                .name("Artist")
                .mainSongIds(List.of(1L))
                .featSongIds(List.of(2L))
                .build();


        // when & then
        assertThatThrownBy(() -> artistFacade.addArtist(createDto))
                .isInstanceOf(MainRoleAbsentException.class)
                .hasMessage("Cannot update Songs featured artists when main artist isn't specified.");
    }

    @Test
    @DisplayName("Should bubble up entity validation exception when creating invalid artist")
    void should_bubble_up_validation_exception_when_adding_invalid_artist() {
        // given
        ArtistDto.Create invalidDto = TestObjectsFactory.createArtistDto("   ");

        // when & then
        assertThatThrownBy(() -> artistFacade.addArtist(invalidDto))
                .isInstanceOf(MissingRequiredFieldException.class)
                .hasMessageContaining("name");
    }

    @Test
    @DisplayName("Should update all provided fields when all of them were initially set")
    void should_completely_update_artist_when_all_fields_were_set() {
        // given
        Artist artistsForSetUp = TestObjectsFactory.createArtistWithId(100L, "For Set Up");

        Song songInit1 = TestObjectsFactory.createSongWithId(1L, "S1");
        Song songInit2 = TestObjectsFactory.createSongWithId(2L, "S2");
        songInit2.assignArtist(artistsForSetUp, true);

        Album albumInit1 = TestObjectsFactory.createAlbumWithId(11L, "Al1");
        Album albumInit2 = TestObjectsFactory.createAlbumWithId(12L, "Al2");
        albumInit2.assignArtist(artistsForSetUp, true);

        ArtistDto.Create artistBeforeUpdate = ArtistDto.Create.builder()
                .name("Init Name")
                .mainSongIds(List.of(songInit1.getId()))
                .featSongIds(List.of(songInit2.getId()))
                .mainAlbumIds(List.of(albumInit1.getId()))
                .featAlbumIds(List.of(albumInit2.getId()))
                .build();

        Mockito.when(songRetriever.getActiveWithArtist(Set.of(1L, 2L))).thenReturn(List.of(songInit1, songInit2));
        Mockito.when(albumRetriever.getActiveWithArtist(Set.of(11L, 12L))).thenReturn(List.of(albumInit1, albumInit2));
        ArtistDto.Info artistInit = artistFacade.addArtist(artistBeforeUpdate);

        Song songUp1 = TestObjectsFactory.createSongWithId(10L, "S1 up");
        Song songUp2 = TestObjectsFactory.createSongWithId(20L, "S2 up");
        songUp2.assignArtist(artistsForSetUp, true);

        Album albumUp1 = TestObjectsFactory.createAlbumWithId(110L, "Al1 up");
        Album albumUp2 = TestObjectsFactory.createAlbumWithId(120L, "Al2 up");
        albumUp2.assignArtist(artistsForSetUp, true);

        Mockito.when(songRetriever.getActiveWithArtist(Set.of(10L, 20L))).thenReturn(List.of(songUp1, songUp2));
        Mockito.when(albumRetriever.getActiveWithArtist(Set.of(110L, 120L))).thenReturn(List.of(albumUp1, albumUp2));

        ArtistDto.Update updateDto = ArtistDto.Update.builder()
                .name(Optional.of("New name"))
                .mainAlbumIds(Optional.of(List.of(110L)))
                .featAlbumIds(Optional.of(List.of(120L)))
                .mainSongIds(Optional.of(List.of(10L)))
                .featSongIds(Optional.of(List.of(20L)))
                .build();

        // when
        ArtistDto.Info updatedArtist = artistFacade.updateArtist(artistInit.id(), updateDto);

        // then
        assertThat(updatedArtist.id()).isEqualTo(artistInit.id());
        assertThat(updatedArtist.name()).isEqualTo("New name");
        assertThat(updatedArtist.songs()).extracting(SongDto.Reference::id).containsExactlyInAnyOrder(10L, 20L);
        assertThat(updatedArtist.albums()).extracting(AlbumDto.Reference::id).containsExactlyInAnyOrder(110L, 120L);
    }

    @Test
    @DisplayName("Should update feat albums when they were initially set")
    void should_update_feat_albums_when_they_were_set() {
        // given
        Artist artistsForSetUp = TestObjectsFactory.createArtistWithId(100L, "For Set Up");

        Album albumInit1 = TestObjectsFactory.createAlbumWithId(11L, "Al1");
        Album albumInit2 = TestObjectsFactory.createAlbumWithId(12L, "Al2");
        albumInit2.assignArtist(artistsForSetUp, true);

        ArtistDto.Create artistBeforeUpdate = ArtistDto.Create.builder()
                .name("Name")
                .mainAlbumIds(List.of(albumInit1.getId()))
                .featAlbumIds(List.of(albumInit2.getId()))
                .build();

        Mockito.when(albumRetriever.getActiveWithArtist(Set.of(11L, 12L))).thenReturn(List.of(albumInit1, albumInit2));
        ArtistDto.Info artistInit = artistFacade.addArtist(artistBeforeUpdate);

        Album albumUp2 = TestObjectsFactory.createAlbumWithId(120L, "Al2 up");
        albumUp2.assignArtist(artistsForSetUp, true);

        Mockito.when(albumRetriever.getActiveWithArtist(Set.of(11L, 120L))).thenReturn(List.of(albumInit1, albumUp2));

        ArtistDto.Update updateDto = ArtistDto.Update.builder()
                .name(Optional.of("New name"))
                .featAlbumIds(Optional.of(List.of(120L)))
                .build();

        // when
        ArtistDto.Info updatedArtist = artistFacade.updateArtist(artistInit.id(), updateDto);

        // then
        assertThat(updatedArtist.id()).isEqualTo(artistInit.id());
        assertThat(updatedArtist.albums()).extracting(AlbumDto.Reference::id).containsExactlyInAnyOrder(11L, 120L);
    }

    @Test
    @DisplayName("Should update main artist album list")
    void should_update_main_artist_albums() {
        // given
        Artist artistsForSetUp = TestObjectsFactory.createArtistWithId(100L, "For Set Up");

        Album albumInit1 = TestObjectsFactory.createAlbumWithId(11L, "Al1");
        Album albumInit2 = TestObjectsFactory.createAlbumWithId(12L, "Al2");
        albumInit2.assignArtist(artistsForSetUp, true);

        ArtistDto.Create artistBeforeUpdate = ArtistDto.Create.builder()
                .name("Init Name")
                .mainAlbumIds(List.of(albumInit1.getId()))
                .featAlbumIds(List.of(albumInit2.getId()))
                .build();

        Mockito.when(albumRetriever.getActiveWithArtist(Set.of(11L, 12L))).thenReturn(List.of(albumInit1, albumInit2));
        ArtistDto.Info artistInit = artistFacade.addArtist(artistBeforeUpdate);

        Album albumUp1 = TestObjectsFactory.createAlbumWithId(110L, "Al1 up");
        Album albumUp2 = TestObjectsFactory.createAlbumWithId(120L, "Al2 up");
        albumUp2.assignArtist(artistsForSetUp, true);

        Mockito.when(albumRetriever.getActiveWithArtist(Set.of(110L, 12L))).thenReturn(List.of(albumUp1, albumInit2));

        ArtistDto.Update updateDto = ArtistDto.Update.builder()
                .mainAlbumIds(Optional.of(List.of(110L)))
                .build();

        // when
        ArtistDto.Info updatedArtist = artistFacade.updateArtist(artistInit.id(), updateDto);

        // then
        assertThat(updatedArtist.id()).isEqualTo(artistInit.id());
        assertThat(updatedArtist.albums()).extracting(AlbumDto.Reference::id).containsExactlyInAnyOrder(110L, 12L);
    }

    @Test
    @DisplayName("Should update feat songs when they were initially set")
    void should_update_feat_songs_when_they_were_set() {
        // given
        Artist artistsForSetUp = TestObjectsFactory.createArtistWithId(100L, "For Set Up");

        Song songInit1 = TestObjectsFactory.createSongWithId(1L, "S1");
        Song songInit2 = TestObjectsFactory.createSongWithId(2L, "S2");
        songInit2.assignArtist(artistsForSetUp, true);

        ArtistDto.Create artistBeforeUpdate = ArtistDto.Create.builder()
                .name("Name")
                .mainSongIds(List.of(songInit1.getId()))
                .featSongIds(List.of(songInit2.getId()))
                .build();

        Mockito.when(songRetriever.getActiveWithArtist(Set.of(1L, 2L))).thenReturn(List.of(songInit1, songInit2));
        ArtistDto.Info artistInit = artistFacade.addArtist(artistBeforeUpdate);

        Song songUp2 = TestObjectsFactory.createSongWithId(20L, "S2 up");
        songUp2.assignArtist(artistsForSetUp, true);

        // Oczekujemy połączenia zachowanego głównego utworu (1L) z nowym gościnnym (20L)
        Mockito.when(songRetriever.getActiveWithArtist(Set.of(1L, 20L))).thenReturn(List.of(songInit1, songUp2));

        ArtistDto.Update updateDto = ArtistDto.Update.builder()
                .name(Optional.of("New name"))
                .featSongIds(Optional.of(List.of(20L))) // Brak mainSongIds sprawi, że program wejdzie w drugi warunek operatora ||
                .build();

        // when
        ArtistDto.Info updatedArtist = artistFacade.updateArtist(artistInit.id(), updateDto);

        // then
        assertThat(updatedArtist.id()).isEqualTo(artistInit.id());
        assertThat(updatedArtist.songs()).extracting(SongDto.Reference::id).containsExactlyInAnyOrder(1L, 20L);
    }

    @Test
    @DisplayName("Should update main artist song list")
    void should_update_main_artist_songs() {
        // given
        Artist artistsForSetUp = TestObjectsFactory.createArtistWithId(100L, "For Set Up");

        Song songInit1 = TestObjectsFactory.createSongWithId(1L, "S1");
        Song songInit2 = TestObjectsFactory.createSongWithId(2L, "S2");
        songInit2.assignArtist(artistsForSetUp, true);

        ArtistDto.Create artistBeforeUpdate = ArtistDto.Create.builder()
                .name("Init Name")
                .mainSongIds(List.of(songInit1.getId()))
                .featSongIds(List.of(songInit2.getId()))
                .build();

        Mockito.when(songRetriever.getActiveWithArtist(Set.of(1L, 2L))).thenReturn(List.of(songInit1, songInit2));
        ArtistDto.Info artistInit = artistFacade.addArtist(artistBeforeUpdate);

        Song songUp1 = TestObjectsFactory.createSongWithId(10L, "S1 up");

        Mockito.when(songRetriever.getActiveWithArtist(Set.of(10L, 2L))).thenReturn(List.of(songUp1, songInit2));

        ArtistDto.Update updateDto = ArtistDto.Update.builder()
                .mainSongIds(Optional.of(List.of(10L)))
                .build();

        // when
        ArtistDto.Info updatedArtist = artistFacade.updateArtist(artistInit.id(), updateDto);

        // then
        assertThat(updatedArtist.id()).isEqualTo(artistInit.id());
        assertThat(updatedArtist.songs()).extracting(SongDto.Reference::id).containsExactlyInAnyOrder(10L, 2L);
    }

    @Test
    @DisplayName("Should update only provided fields without touching omitted ones")
    void should_partially_update_artist_when_some_fields_are_null() {
        // given
        ArtistDto.Info originalArtist = addArtist("Original Name");
        String newName = "Updated Name";

        ArtistDto.Update updateDto = ArtistDto.Update.builder()
                .name(Optional.of(newName))
                .build();

        // when
        ArtistDto.Info updatedArtist = artistFacade.updateArtist(originalArtist.id(), updateDto);
        ArtistDto.Details detailsAfterUpdate = artistFacade.getArtistDetails(originalArtist.id());

        // then
        assertThat(updatedArtist.id()).isEqualTo(originalArtist.id());
        assertThat(updatedArtist.name()).isEqualTo(newName);
        assertThat(detailsAfterUpdate.name()).isEqualTo(newName);
    }

    @Test
    @DisplayName("Should throw MissingRequiredFieldException when explicitly updating required field with empty optional")
    void should_throw_exception_when_required_field_is_empty_optional() {
        // given
        ArtistDto.Info artist = addArtist("Artist Name");

        ArtistDto.Update updateDto = ArtistDto.Update.builder()
                .name(Optional.empty())
                .build();

        // when & then
        assertThatThrownBy(() -> artistFacade.updateArtist(artist.id(), updateDto))
                .isInstanceOf(MissingRequiredFieldException.class)
                .hasMessageContaining("name");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existing artist")
    void should_throw_exception_when_updating_non_existing_artist() {
        // given
        Long nonExistingId = 999L;
        ArtistDto.Update updateDto = ArtistDto.Update.builder()
                .name(Optional.of("New Name"))
                .build();

        // when & then
        assertThatThrownBy(() -> artistFacade.updateArtist(nonExistingId, updateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Artist");
    }

    @Test
    @DisplayName("Should deactivate artist by id and verify deleters are called")
    public void should_delete_artist_by_id_when_artist_exists() {
        // given
        ArtistDto.Info addedArtist = addArtist("artist to deactivate");
        Long idForDeactivating = addedArtist.id();

        // when
        artistFacade.deactivateArtist(idForDeactivating);

        // then
        assertThatThrownBy(() -> artistFacade.getArtistDetails(idForDeactivating))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Artist");
        verify(songDeleter).bulkDeactivate(anySet());
        verify(albumDeleter).bulkDeactivate(anySet());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when trying to delete non-existing artist")
    public void should_throw_exception_when_deleting_non_existing_artist() {
        // given
        Long nonExistingId = 999L;

        // when & then
        assertThatThrownBy(() -> artistFacade.deactivateArtist(nonExistingId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Artist");
    }

    private ArtistDto.Info addArtist(String name) {
        ArtistDto.Create createDto = TestObjectsFactory.createArtistDto(name);
        return artistFacade.addArtist(createDto);
    }
}