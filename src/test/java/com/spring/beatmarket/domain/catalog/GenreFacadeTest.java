package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.GenreDto;
import com.spring.beatmarket.domain.catalog.exception.NameIsBlankException;
import com.spring.beatmarket.domain.catalog.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GenreFacadeTest {

    private final InMemoryGenreRepository genreRepository = new InMemoryGenreRepository();

    private final SongRetriever songRetriever = mock(SongRetriever.class);
    private final SongUpdater songUpdater = mock(SongUpdater.class);
    private final GenreMapper genreMapper = new GenreMapperImpl();

    private final GenreFacade genreFacade = createGenreFacade();

    private GenreFacade createGenreFacade() {
        GenreAdder genreAdder = new GenreAdder(genreRepository, genreMapper);
        GenreRetriever genreRetriever = new GenreRetriever(genreRepository, genreMapper);
        GenreDeleter genreDeleter = new GenreDeleter(genreRetriever, songRetriever);
        GenreUpdater genreUpdater = new GenreUpdater(genreRepository, genreRetriever, genreMapper);

        return new GenreFacadeImpl(genreAdder, genreRetriever, genreDeleter, genreUpdater, songUpdater);
    }

    @Test
    @DisplayName("Should add Genre")
    void should_add_genre() {
        // given
        GenreDto.Create createDto = new GenreDto.Create("Rock");

        // when
        GenreDto.Info genreDtoWhen = genreFacade.addGenre(createDto);
        GenreDto.Details genreDtoDetails = genreFacade.getGenreDetails(genreDtoWhen.id());

        // then
        assertThat(genreDtoDetails.id()).isEqualTo(genreDtoWhen.id());
        assertThat(genreDtoDetails.name()).isEqualTo("Rock");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Should throw NameIsBlankException when adding genre with invalid name")
    void should_throw_exception_when_adding_genre_with_invalid_name(String invalidName) {
        // given
        GenreDto.Create invalidDto = new GenreDto.Create(invalidName);

        // when & then
        assertThatThrownBy(() -> genreFacade.addGenre(invalidDto))
                .isInstanceOf(NameIsBlankException.class)
                .hasMessage("Genre needs a specified name!");
    }

    @Test
    @DisplayName("Should find genre 'Pop' by id")
    void should_find_genre_by_id() {
        // given
        GenreDto.Info genreDtoGiven = genreFacade.addGenre(new GenreDto.Create("Pop"));

        // when
        GenreDto.Details genreDtoWhen = genreFacade.getGenreDetails(genreDtoGiven.id());

        // then
        assertThat(genreDtoWhen.id()).isEqualTo(genreDtoGiven.id());
        assertThat(genreDtoWhen.name()).isEqualTo("Pop");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when genre is not found")
    void should_throw_exception_when_genre_not_found() {
        // given
        Long nonExistingId = 999L;

        // when & then
        assertThatThrownBy(() -> genreFacade.getGenreDetails(nonExistingId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Genre with id " + nonExistingId + " not found or is inactive");
    }

    @Test
    @DisplayName("Should update genre name and trim whitespaces")
    void should_update_genre_name_and_trim() {
        // given
        GenreDto.Info originalGenre = genreFacade.addGenre(new GenreDto.Create("Jazz"));
        GenreDto.Update updateDto = new GenreDto.Update("  Smooth Jazz  ");

        // when
        GenreDto.Info updatedGenre = genreFacade.updateGenre(originalGenre.id(), updateDto);
        GenreDto.Details detailsAfterUpdate = genreFacade.getGenreDetails(originalGenre.id());

        // then
        assertThat(updatedGenre.id()).isEqualTo(originalGenre.id());
        assertThat(updatedGenre.name()).isEqualTo("Smooth Jazz");
        assertThat(detailsAfterUpdate.name()).isEqualTo("Smooth Jazz");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existing genre")
    void should_throw_exception_when_updating_non_existing_genre() {
        // given
        Long nonExistingId = 999L;
        GenreDto.Update updateDto = new GenreDto.Update("New Name");

        // when & then
        assertThatThrownBy(() -> genreFacade.updateGenre(nonExistingId, updateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Genre with id " + nonExistingId + " not found or is inactive");
    }

    @Test
    @DisplayName("Should deactivate genre by id when genre exists")
    void should_deactivate_genre_by_id_when_genre_exists() {
        // given
        GenreDto.Info addedGenre = genreFacade.addGenre(new GenreDto.Create("Metal"));
        Long idForDeactivating = addedGenre.id();
        assertThat(genreFacade.getGenreDetails(idForDeactivating)).isNotNull();

        // when
        genreFacade.deactivateGenre(idForDeactivating);

        // then
        assertThatThrownBy(() -> genreFacade.getGenreDetails(idForDeactivating))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Genre with id " + idForDeactivating + " not found or is inactive");

        // verify interaction with song retriever
        verify(songRetriever).validateGenreHasNoActiveSongs(idForDeactivating);
    }

    @Test
    @DisplayName("Should transfer genre and return updated songs count")
    void should_transfer_genre() {
        // given
        GenreDto.Info oldGenre = genreFacade.addGenre(new GenreDto.Create("Old Genre"));
        GenreDto.Info newGenre = genreFacade.addGenre(new GenreDto.Create("New Genre"));

        when(songUpdater.bulkUpdateSongsByGenreId(oldGenre.id(), newGenre.id())).thenReturn(5);

        // when
        GenreDto.Transfer transferResult = genreFacade.transferGenre(oldGenre.id(), newGenre.id());

        // then
        assertThat(transferResult.updatedSongsCount()).isEqualTo(5);
        assertThat(transferResult.oldGenreId()).isEqualTo(oldGenre.id());
        assertThat(transferResult.newGenreId()).isEqualTo(newGenre.id());

        verify(songUpdater).bulkUpdateSongsByGenreId(oldGenre.id(), newGenre.id());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when transferring to the same genre id")
    void should_throw_exception_when_transferring_to_same_id() {
        // given
        Long sameId = 1L;

        // when & then
        assertThatThrownBy(() -> genreFacade.transferGenre(sameId, sameId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Source and target genre IDs cannot be the same.");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when transferring from non-existing genre")
    void should_throw_exception_when_transferring_from_non_existing_genre() {
        // given
        Long nonExistingOldId = 999L;
        GenreDto.Info newGenre = genreFacade.addGenre(new GenreDto.Create("Target Genre"));

        // when & then
        assertThatThrownBy(() -> genreFacade.transferGenre(nonExistingOldId, newGenre.id()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Genre with id " + nonExistingOldId + " not found or is inactive");
    }
}