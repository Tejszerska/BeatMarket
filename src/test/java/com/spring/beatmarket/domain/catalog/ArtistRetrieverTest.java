package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing only complicated internal logic and edge cases to avoid "mock hell".
 * Methods reachable from {@link ArtistFacadeTest} are tested there.
 */
class ArtistRetrieverTest {

    private final ArtistRepository artistRepository = mock(ArtistRepository.class);
    private final ArtistMapper artistMapper = mock(ArtistMapper.class);

    private final ArtistRetriever artistRetriever = new ArtistRetriever(artistRepository, artistMapper);

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should safely return empty list when collection of ids is null or empty")
    void should_return_empty_list_when_ids_null_or_empty(Set<Long> ids) {
        // when
        List<Artist> result = artistRetriever.getActives(ids);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return active artists by set of ids")
    void should_return_artists_by_ids() {
        // given
        Artist artist1 = TestObjectsFactory.createArtistWithId(1L, "A1");
        Artist artist2 = TestObjectsFactory.createArtistWithId(2L, "A2");
        Set<Long> ids = Set.of(1L, 2L);

        when(artistRepository.findByIdInAndActiveTrue(ids)).thenReturn(List.of(artist1, artist2));

        // when
        List<Artist> artists = artistRetriever.getActives(ids);

        // then
        assertThat(artists).containsExactlyInAnyOrder(artist1, artist2);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException with exact missing ID when repository returns fewer elements")
    void should_throw_exception_with_missing_id() {
        // given
        Artist artist1 = TestObjectsFactory.createArtistWithId(1L, "A1");
        Set<Long> ids = Set.of(1L, 2L);

        when(artistRepository.findByIdInAndActiveTrue(ids)).thenReturn(List.of(artist1));

        // when & then
        assertThatThrownBy(() -> artistRetriever.getActives(ids))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Artist with id 2 not found or is inactive");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException with exact missing ID when repository doesn't return elements")
    void should_throw_exception_with_no_elements() {
        // given
        Set<Long> ids = Set.of(1L);

        // when & then
        assertThatThrownBy(() -> artistRetriever.getActives(ids))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Artist with id 1 not found or is inactive");

        verify(artistRepository).findByIdInAndActiveTrue(eq(ids));
    }

    @Test
    @DisplayName("Should return artist when both songs and albums are found eagerly")
    void should_return_artist_eagerly() {
        // given
        Long artistId = 1L;
        Artist artist = TestObjectsFactory.createArtistWithId(artistId, "Artist");

        when(artistRepository.findByIdWithSongs(artistId)).thenReturn(Optional.of(artist));
        when(artistRepository.findByIdWithAlbums(artistId)).thenReturn(Optional.of(artist));

        // when
        Artist result = artistRetriever.findEagerly(artistId);

        // then
        assertThat(result).isEqualTo(artist);
        verify(artistRepository).findByIdWithSongs(artistId);
        verify(artistRepository).findByIdWithAlbums(artistId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when artist is not found during songs fetch")
    void should_throw_exception_when_not_found_with_songs() {
        // given
        Long artistId = 1L;
        when(artistRepository.findByIdWithSongs(artistId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> artistRetriever.findEagerly(artistId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Artist with id 1 not found or is inactive");

        verify(artistRepository).findByIdWithSongs(artistId);
        verify(artistRepository, Mockito.never()).findByIdWithAlbums(artistId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when artist is found with songs but missing during albums fetch")
    void should_throw_exception_when_not_found_with_albums() {
        // given
        Long artistId = 1L;
        Artist artist = TestObjectsFactory.createArtistWithId(artistId, "Artist");

        when(artistRepository.findByIdWithSongs(artistId)).thenReturn(Optional.of(artist));
        when(artistRepository.findByIdWithAlbums(artistId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> artistRetriever.findEagerly(artistId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Artist with id 1 not found or is inactive");

        verify(artistRepository).findByIdWithSongs(artistId);
        verify(artistRepository).findByIdWithAlbums(artistId);
    }
}