package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

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
 * Methods reachable from {@link AlbumFacadeTest} are tested there.
 */
class AlbumRetrieverTest {

    private final AlbumRepository albumRepository = mock(AlbumRepository.class);
    private final AlbumMapper albumMapper = mock(AlbumMapper.class);

    private final AlbumRetriever albumRetriever = new AlbumRetriever(albumRepository, albumMapper);

    @Test
    @DisplayName("Should return active album by id")
    void should_return_active_album_by_id() {
        // given
        Long id = 1L;
        Album album = Album.builder().title("Title").build();
        when(albumRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.of(album));

        // when
        Album result = albumRetriever.getActiveWithArtist(id);

        // then
        assertThat(result).isEqualTo(album);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should safely return empty list when collection of ids is null or empty")
    void should_return_empty_list_when_ids_null_or_empty(Set<Long> ids) {
        // when
        List<Album> result = albumRetriever.getActiveWithArtist(ids);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException with exact missing ID when repository returns fewer elements")
    void should_throw_exception_with_missing_id() {
        // given
        Album foundAlbum = TestObjectsFactory.createAlbumWithId(2L, "Album");
        when(albumRepository.findActiveWithArtistsByIds(Set.of(1L, 2L)))
                .thenReturn(List.of(foundAlbum));

        // when & then
        assertThatThrownBy(() -> albumRetriever.getActiveWithArtist(Set.of(1L, 2L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Album with id 1 not found or is inactive");
    }

    @Test
    @DisplayName("Should return found Album in a list")
    void should_return_found_album() {
        // given
        Album foundAlbum = Album.builder().title("Found").build();
        when(albumRepository.findActiveWithArtistsByIds(Set.of(1L)))
                .thenReturn(List.of(foundAlbum));

        // when
        List<Album> albumList = albumRetriever.getActiveWithArtist(Set.of(1L));
        // then
        assertThat(albumList).contains(foundAlbum);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException with exact missing ID when repository doesn't return elements")
    void should_throw_exception_with_no_elements() {
        // given
        long idToFind = 99L;
        // when & then
        assertThatThrownBy(() -> albumRetriever.getActive(idToFind))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Album with id 99 not found or is inactive");
        verify(albumRepository).findByIdAndActiveTrue(eq(idToFind));

    }
}