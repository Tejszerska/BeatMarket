package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import com.spring.beatmarket.domain.catalog.exception.MissingRequiredFieldException;
import com.spring.beatmarket.domain.catalog.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

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
        ArtistDto.Create createDto = ArtistDto.Create.builder()
                .name("New Artist")
                .build();

        ArtistDto.Info artistDtoGiven = artistFacade.addArtist(createDto);

        // when
        ArtistDto.Details artistDtoWhen = artistFacade.getArtistDetails(artistDtoGiven.id());

        // then
        assertThat(artistDtoWhen.id()).isEqualTo(artistDtoGiven.id());
        assertThat(artistDtoWhen.name()).isEqualTo(createDto.name());
    }

    @Test
    @DisplayName("Should bubble up entity validation exception when creating invalid artist")
    void should_bubble_up_validation_exception_when_adding_invalid_artist() {
        // given
        ArtistDto.Create invalidDto = ArtistDto.Create.builder()
                .name("   ")
                .build();

        // when & then
        assertThatThrownBy(() -> artistFacade.addArtist(invalidDto))
                .isInstanceOf(MissingRequiredFieldException.class)
                .hasMessageContaining("name");
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
    @DisplayName("Should deactivate artist by id and cascade deletions to related entities")
    public void should_delete_artist_by_id_when_artist_exists() {
        // given
        ArtistDto.Info addedArtist = addArtist("artist to deactivate");
        Long idForDeactivating = addedArtist.id();
        assertThat(artistFacade.getArtistDetails(idForDeactivating)).isNotNull();

        // when
        artistFacade.deactivateArtist(idForDeactivating);

        // then
        assertThatThrownBy(() -> artistFacade.getArtistDetails(idForDeactivating))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Artist");

        // Weryfikacja interakcji z klasami usuwającymi powiązania
        verify(songDeleter).bulkDeactivate(anySet());
        verify(albumDeleter).deleteAllAlbumsByIds(anySet());
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
        ArtistDto.Create createDto = ArtistDto.Create.builder()
                .name(name)
                .build();

        return artistFacade.addArtist(createDto);
    }
}