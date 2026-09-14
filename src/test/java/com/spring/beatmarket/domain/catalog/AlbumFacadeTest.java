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
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class AlbumFacadeTest {

    private final InMemoryAlbumRepository albumRepository = new InMemoryAlbumRepository();

    private final SongRetriever songRetriever = mock(SongRetriever.class);
    private final ArtistRetriever artistRetriever = mock(ArtistRetriever.class);

    private final RoleValidator roleValidator = new RoleValidator();
    private final ArtistRoleManager artistRoleManager = new ArtistRoleManager(roleValidator, artistRetriever);
    private final AlbumMapper albumMapper = new AlbumMapperImpl();

    private final AlbumFacade albumFacade = AlbumFacadeTestConfiguration.createAlbumFacade(
            albumMapper,
            albumRepository,
            songRetriever,
            artistRoleManager
    );

    @Test
    @DisplayName("Should return slice with all albums when no filtering present")
    void should_return_all_album_in_slice() {
        // given
        prepareAlbumsForFiltering();
        Pageable pageable = PageRequest.of(0, 10);
        // when
        Slice<AlbumDto.Summary> slice = albumFacade.findAllAlbums(null, null, pageable);

        // then
        assertThat(slice.getContent()).hasSize(3);
    }

    @Test
    @DisplayName("Should return slice with some albums when filtering by title")
    void should_return_some_album_in_slice_filtering_title() {
        // given
        prepareAlbumsForFiltering();
        Pageable pageable = PageRequest.of(0, 10);
        // when
        Slice<AlbumDto.Summary> slice = albumFacade.findAllAlbums(null, "tIt", pageable);

        // then
        assertThat(slice.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("Should return slice with some albums when filtering by artist's ID")
    void should_return_some_album_in_slice_filtering_artistId() {
        // given
        prepareAlbumsForFiltering();
        Pageable pageable = PageRequest.of(0, 10);
        // when
        Slice<AlbumDto.Summary> slice = albumFacade.findAllAlbums(1L, null, pageable);

        // then
        assertThat(slice.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("Should return slice with one album when filtering with all criteria")
    void should_return_some_one_album_in_slice_filtering_all() {
        // given
        prepareAlbumsForFiltering();
        Pageable pageable = PageRequest.of(0, 10);
        // when
        Slice<AlbumDto.Summary> slice = albumFacade.findAllAlbums(1L, "tit", pageable);

        // then
        assertThat(slice.getContent()).hasSize(1);
    }

    @ParameterizedTest
    @MethodSource("provideImproperValues")
    @DisplayName("Should return empty slice, when no album matches filtering criteria")
    void should_return_empty_slice_when_no_album_matches_filtering(String title, Long artistId) {
        // given
        prepareAlbumsForFiltering();
        Pageable pageable = PageRequest.of(0, 10);
        // when
        Slice<AlbumDto.Summary> slice = albumFacade.findAllAlbums(artistId, title, pageable);

        // then
        assertThat(slice.getContent()).isEmpty();
    }

    private static Stream<Arguments> provideImproperValues() {
        return Stream.of(
                Arguments.of("absent", null),
                Arguments.of(null, 3L)
        );
    }

    @Test
    @DisplayName("Should find album by id")
    void should_find_album_by_id() {
        // given
        AlbumDto.Info albumDtoGiven = addAlbum("Test Album");

        // when
        AlbumDto.Details albumDtoWhen = albumFacade.getAlbumDetails(albumDtoGiven.id());

        // then
        assertThat(albumDtoWhen.id()).isEqualTo(albumDtoGiven.id());
        assertThat(albumDtoWhen.title()).isEqualTo("Test Album");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when album is not found")
    void should_throw_exception_when_album_not_found() {
        // given
        Long nonExistingId = 10L;

        // when & then
        assertThatThrownBy(() -> albumFacade.getAlbumDetails(nonExistingId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Album");
    }

    @Test
    @DisplayName("Should add Album without songs or artists")
    void should_add_album_without_song_artists() {
        // given
        AlbumDto.Create createDto = AlbumDto.Create.builder()
                .title("New Album")
                .releaseDate(LocalDate.now())
                .build();

        AlbumDto.Info albumDtoGiven = albumFacade.addAlbum(createDto);

        // when
        AlbumDto.Details albumDtoWhen = albumFacade.getAlbumDetails(albumDtoGiven.id());

        // then
        assertThat(albumDtoWhen.id()).isEqualTo(albumDtoGiven.id());
        assertThat(albumDtoWhen.title()).isEqualTo(createDto.title());
        assertThat(albumDtoWhen.releaseDate()).isEqualTo(createDto.releaseDate());
    }

    @Test
    @DisplayName("Should add Album with songs and artists")
    void should_add_album_with_song_artists() {
        // given
        Set<Long> songIds = Set.of(1L, 2L);
        Song song1 = Song.builder().title("S1").releaseDate(LocalDate.now()).duration(100).language(SongLanguage.EN).build();
        Song song2 = Song.builder().title("S2").releaseDate(LocalDate.now()).duration(100).language(SongLanguage.EN).build();
        Mockito.when(songRetriever.getActive(songIds)).thenReturn(Set.of(song1, song2));

        Long mainArtistId = 1L;
        List<Long> featArtistsIds = List.of(2L);

        Artist mainArtist = Artist.builder().name("A Main").build();
        ReflectionTestUtils.setField(mainArtist, "id", 1L);
        Artist featArtist = Artist.builder().name("A Feat").build();
        ReflectionTestUtils.setField(featArtist, "id", 2L);

        Mockito.when(artistRetriever.getActives(Set.of(1L, 2L))).thenReturn(List.of(mainArtist, featArtist));

        AlbumDto.Create createDto = AlbumDto.Create.builder()
                .title("New Album")
                .releaseDate(LocalDate.now())
                .songIds(songIds)
                .mainArtistId(mainArtistId)
                .featArtistsIds(featArtistsIds)
                .build();

        // when
        AlbumDto.Info albumDtoGiven = albumFacade.addAlbum(createDto);
        AlbumDto.Details albumDtoWhen = albumFacade.getAlbumDetails(albumDtoGiven.id());

        // then
        assertThat(albumDtoWhen.id()).isEqualTo(albumDtoGiven.id());
        assertThat(albumDtoWhen.title()).isEqualTo(createDto.title());
        assertThat(albumDtoWhen.releaseDate()).isEqualTo(createDto.releaseDate());
        assertThat(albumDtoWhen.songs()).hasSize(2);
        assertThat(albumDtoWhen.artists()).hasSize(2);
    }


    @Test
    @DisplayName("Should bubble up entity validation exception when creating invalid album")
    void should_bubble_up_validation_exception_when_adding_invalid_album() {
        // given
        AlbumDto.Create invalidDto = AlbumDto.Create.builder()
                .releaseDate(LocalDate.now())
                .build();

        // when & then
        assertThatThrownBy(() -> albumFacade.addAlbum(invalidDto))
                .isInstanceOf(MissingRequiredFieldException.class)
                .hasMessageContaining("title");
    }

    @Test
    @DisplayName("Should update only provided fields without touching omitted ones")
    void should_partially_update_album_when_some_fields_are_null() {
        // given
        AlbumDto.Info originalAlbum = addAlbum("Original Title");
        String newTitle = "Updated Title";
        LocalDate newDate = LocalDate.now().minusDays(1);

        AlbumDto.Update updateDto = AlbumDto.Update.builder()
                .title(Optional.of(newTitle))
                .releaseDate(Optional.of(newDate))
                .build();

        // when
        AlbumDto.Info updatedAlbum = albumFacade.updateAlbum(originalAlbum.id(), updateDto);
        AlbumDto.Details detailsAfterUpdate = albumFacade.getAlbumDetails(originalAlbum.id());

        // then
        assertThat(updatedAlbum.id()).isEqualTo(originalAlbum.id());
        assertThat(updatedAlbum.title()).isEqualTo(newTitle);
        assertThat(detailsAfterUpdate.title()).isEqualTo(newTitle);
        assertThat(detailsAfterUpdate.releaseDate()).isEqualTo(newDate);
    }

    @Test
    @DisplayName("Should update album's songs when none were present originally")
    void should_update_songs_when_there_where_none() {
        // given
        AlbumDto.Info originalAlbum = addAlbum("Title");
        assertThat(originalAlbum.songs()).isEmpty();

        Set<Long> songIds = Set.of(1L, 2L);
        Song song1 = Song.builder().title("S1").releaseDate(LocalDate.now()).duration(100).language(SongLanguage.EN).build();
        ReflectionTestUtils.setField(song1, "id", 1L);
        Song song2 = Song.builder().title("S2").releaseDate(LocalDate.now()).duration(100).language(SongLanguage.EN).build();
        ReflectionTestUtils.setField(song2, "id", 2L);

        Mockito.when(songRetriever.getActive(songIds)).thenReturn(Set.of(song1, song2));

        AlbumDto.Update updateDto = AlbumDto.Update.builder()
                .songIds(Optional.of(songIds))
                .build();

        // when
        AlbumDto.Info updatedAlbum = albumFacade.updateAlbum(originalAlbum.id(), updateDto);

        // then
        assertThat(updatedAlbum.id()).isEqualTo(originalAlbum.id());
        assertThat(updatedAlbum.songs()).hasSize(2);
        assertThat(updatedAlbum.songs())
                .extracting(SongDto.Reference::id)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @DisplayName("Should update album's main and feat artists")
    void should_update_artists_when_there_where_none() {
        // given
        AlbumDto.Info originalAlbum = addAlbum("Title");
        assertThat(originalAlbum.artists()).isEmpty();

        Artist featArtist1 = Artist.builder().name("A1").build();
        ReflectionTestUtils.setField(featArtist1, "id", 1L);
        Artist featArtist2 = Artist.builder().name("A2").build();
        ReflectionTestUtils.setField(featArtist2, "id", 2L);

        Artist mainArtist = Artist.builder().name("A3").build();
        ReflectionTestUtils.setField(mainArtist, "id", 3L);

        Set<Long> combinedIds = Set.of(1L, 2L, 3L);
        List<Artist> combinedArtists = new ArrayList<>();
        combinedArtists.add(featArtist1);
        combinedArtists.add(featArtist2);
        combinedArtists.add(0, mainArtist);

        Mockito.when(artistRetriever.getActives(combinedIds)).thenReturn(combinedArtists);

        AlbumDto.Update updateDto = AlbumDto.Update.builder()
                .mainArtistId(Optional.of(3L))
                .featArtistsIds(Optional.of(List.of(1L, 2L)))
                .build();

        // when
        AlbumDto.Info updatedAlbum = albumFacade.updateAlbum(originalAlbum.id(), updateDto);

        // then
        assertThat(updatedAlbum.id()).isEqualTo(originalAlbum.id());
        assertThat(updatedAlbum.artists()).hasSize(3);
        assertThat(updatedAlbum.artists().get(0).id()).isEqualTo(3L);
        assertThat(updatedAlbum.artists())
                .extracting(ArtistDto.Reference::id)
                .containsExactlyInAnyOrder(1L, 2L, 3L)
        ;
    }

    @Test
    @DisplayName("Should not update album's feat artists when main is not present")
    void should_not_update_artists_when_only_feat() {
        // given
        AlbumDto.Info originalAlbum = addAlbum("Title");
        assertThat(originalAlbum.artists()).isEmpty();

        Artist featArtist1 = Artist.builder().name("A1").build();
        ReflectionTestUtils.setField(featArtist1, "id", 1L);
        Artist featArtist2 = Artist.builder().name("A2").build();
        ReflectionTestUtils.setField(featArtist2, "id", 2L);


        Set<Long> combinedIds = Set.of(1L, 2L);
        List<Artist> combinedArtists = new ArrayList<>();
        combinedArtists.add(featArtist1);
        combinedArtists.add(featArtist2);

        Mockito.when(artistRetriever.getActives(combinedIds)).thenReturn(combinedArtists);

        AlbumDto.Update updateDto = AlbumDto.Update.builder()
                .featArtistsIds(Optional.of(List.of(1L, 2L)))
                .build();

        // when & then
        assertThatThrownBy(() -> albumFacade.updateAlbum(originalAlbum.id(), updateDto))
                .isInstanceOf(MainRoleAbsentException.class);
    }

    @Test
    @DisplayName("Should update album's songs to empty collection when updating null")
    void should_update_songs_to_empty_when_null() {
        // given

        Set<Long> songIds = Set.of(1L, 2L);
        Song song1 = Song.builder().title("S1").releaseDate(LocalDate.now()).duration(100).language(SongLanguage.EN).build();
        ReflectionTestUtils.setField(song1, "id", 1L);
        Song song2 = Song.builder().title("S2").releaseDate(LocalDate.now()).duration(100).language(SongLanguage.EN).build();
        ReflectionTestUtils.setField(song2, "id", 2L);

        Mockito.when(songRetriever.getActive(songIds)).thenReturn(Set.of(song1, song2));

        AlbumDto.Create album = AlbumDto.Create.builder()
                .title("Album")
                .songIds(songIds)
                .build();

        AlbumDto.Info originalAlbum = albumFacade.addAlbum(album);

        AlbumDto.Update updateDto = AlbumDto.Update.builder()
                .songIds(Optional.empty())
                .build();

        // when
        AlbumDto.Info updatedAlbum = albumFacade.updateAlbum(originalAlbum.id(), updateDto);

        // then
        assertThat(updatedAlbum.id()).isEqualTo(originalAlbum.id());
        assertThat(updatedAlbum.songs()).isEmpty();    }

    @Test
    @DisplayName("Should throw MissingRequiredFieldException when explicitly updating required field with empty optional")
    void should_throw_exception_when_required_field_is_empty_optional() {
        // given
        AlbumDto.Info album = addAlbum("Album Title");

        AlbumDto.Update updateDto = AlbumDto.Update.builder()
                .title(Optional.empty())
                .build();

        // when & then
        assertThatThrownBy(() -> albumFacade.updateAlbum(album.id(), updateDto))
                .isInstanceOf(MissingRequiredFieldException.class)
                .hasMessageContaining("title");
    }

    @Test
    @DisplayName("Should safely handle updating songs with an empty collection")
    void should_clear_songs_when_song_ids_is_empty_optional() {
        // given
        AlbumDto.Info album = addAlbum("Album Title");

        AlbumDto.Update clearSongsDto = AlbumDto.Update.builder()
                .songIds(Optional.of(new HashSet<>()))
                .build();

        // when
        albumFacade.updateAlbum(album.id(), clearSongsDto);
        AlbumDto.Details detailsAfterClear = albumFacade.getAlbumDetails(album.id());

        // then
        assertThat(detailsAfterClear.songs()).isEmpty();
    }

    @Test
    @DisplayName("Should update album's songs with removing ones that were not present in updated list")
    void should_update_songs_when_there_where_some() {
        // given
        Set<Long> originalSongIds = Set.of(1L, 2L);
        Song song1 = Song.builder().title("S1").releaseDate(LocalDate.now()).duration(100).language(SongLanguage.EN).build();
        ReflectionTestUtils.setField(song1, "id", 1L);
        Song song2 = Song.builder().title("S2").releaseDate(LocalDate.now()).duration(100).language(SongLanguage.EN).build();
        ReflectionTestUtils.setField(song2, "id", 2L);

        Mockito.when(songRetriever.getActive(originalSongIds)).thenReturn(Set.of(song1, song2));

        AlbumDto.Create album = AlbumDto.Create.builder()
                .title("Album")
                .songIds(originalSongIds)
                .build();

        AlbumDto.Info originalAlbum = albumFacade.addAlbum(album);


        Set<Long> updatedSongIds = Set.of(2L, 3L);
        Song song3 = Song.builder().title("S3").releaseDate(LocalDate.now()).duration(100).language(SongLanguage.EN).build();
        ReflectionTestUtils.setField(song3, "id", 3L);

        Mockito.when(songRetriever.getActive(updatedSongIds)).thenReturn(Set.of(song2, song3));

        AlbumDto.Update updateDto = AlbumDto.Update.builder()
                .songIds(Optional.of(updatedSongIds))
                .build();

        // when
        AlbumDto.Info updatedAlbum = albumFacade.updateAlbum(originalAlbum.id(), updateDto);

        // then
        assertThat(updatedAlbum.id()).isEqualTo(originalAlbum.id());
        assertThat(updatedAlbum.songs()).hasSize(2);
        assertThat(updatedAlbum.songs())
                .extracting(SongDto.Reference::id)
                .containsExactlyInAnyOrder(3L, 2L)
                .doesNotContain(1L);
    }

    @Test
    @DisplayName("Should bubble up entity invariant exception when updating with future release date")
    void should_bubble_up_entity_exception_when_release_date_is_in_future() {
        // given
        AlbumDto.Info album = addAlbum("Title");

        AlbumDto.Update updateDto = AlbumDto.Update.builder()
                .releaseDate(Optional.of(LocalDate.now().plusDays(5)))
                .build();

        // when & then
        assertThatThrownBy(() -> albumFacade.updateAlbum(album.id(), updateDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Release date can't be in the future");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existing album")
    void should_throw_exception_when_updating_non_existing_album() {
        // given
        Long nonExistingId = 10L;
        AlbumDto.Update updateDto = AlbumDto.Update.builder()
                .title(Optional.of("New Title"))
                .build();

        // when & then
        assertThatThrownBy(() -> albumFacade.updateAlbum(nonExistingId, updateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Album");
    }

    @Test
    @DisplayName("Should deactivate album by id when album exists")
    public void should_delete_album_by_id_when_album_exists() {
        // given
        AlbumDto.Info addedAlbum = addAlbum("album to deactivate");
        Long idForDeactivating = addedAlbum.id();
        assertThat(albumFacade.getAlbumDetails(idForDeactivating)).isNotNull();

        // when
        albumFacade.deactivateAlbum(idForDeactivating);

        // then
        assertThatThrownBy(() -> albumFacade.getAlbumDetails(idForDeactivating))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Album");
    }

    @Test
    @DisplayName("Should safely return when deactivating with id= null")
    public void should_safely_return_when_deactivating_id_null() {
        // given
        Long idForDeactivating = null;

        // when & then
        assertThatCode(() -> albumFacade.deactivateAlbum(idForDeactivating))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when trying to delete non-existing album")
    public void should_throw_exception_when_deleting_non_existing_album() {
        // given
        Long nonExistingId = 10L;

        // when & then
        assertThatThrownBy(() -> albumFacade.deactivateAlbum(nonExistingId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Album");
    }

    private AlbumDto.Info addAlbum(String title) {
        AlbumDto.Create createDto = AlbumDto.Create.builder()
                .title(title)
                .releaseDate(LocalDate.now())
                .build();

        return albumFacade.addAlbum(createDto);
    }

    private void prepareAlbumsForFiltering() {
        AlbumDto.Create createDto1 = AlbumDto.Create.builder()
                .title("Title 1")
                .build();

        albumFacade.addAlbum(createDto1);

        Artist mainArtist = Artist.builder().name("A Main").build();
        Long mainArtistId = 1L;
        ReflectionTestUtils.setField(mainArtist, "id", 1L);


        Artist featArtist = Artist.builder().name("A Feat").build();
        Long featArtistId = 2L;
        ReflectionTestUtils.setField(featArtist, "id", 2L);

        AlbumDto.Create createDto2 = AlbumDto.Create.builder()
                .title("Title 2")
                .mainArtistId(mainArtistId)
                .featArtistsIds(List.of(featArtistId))
                .build();

        Mockito.when(artistRetriever.getActives(Set.of(mainArtistId, featArtistId))).thenReturn(List.of(mainArtist, featArtist));
        albumFacade.addAlbum(createDto2);

        AlbumDto.Create createDto3 = AlbumDto.Create.builder()
                .title("Won't be filtered by t...")
                .mainArtistId(mainArtistId)
                .build();

        Mockito.when(artistRetriever.getActives(Set.of(mainArtistId))).thenReturn(List.of(mainArtist));
        albumFacade.addAlbum(createDto3);

        AlbumDto.Create createDto4 = AlbumDto.Create.builder()
                .title("Title 4")
                .build();

        AlbumDto.Info info4 = albumFacade.addAlbum(createDto4);
        albumFacade.deactivateAlbum(info4.id());
    }
}