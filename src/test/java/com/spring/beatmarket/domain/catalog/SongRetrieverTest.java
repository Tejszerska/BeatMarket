package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.domain.catalog.exception.DataConflictException;
import com.spring.beatmarket.domain.catalog.exception.ResourceNotFoundException;
import com.spring.beatmarket.domain.licensing.LicensingFacade;
import com.spring.beatmarket.domain.licensing.dto.SongPriceDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SongRetrieverTest {

    @Mock
    private SongRepository songRepository;
    @Mock
    private SongMapper songMapper;
    @Mock
    private LicensingFacade licensingFacade;

    @InjectMocks
    private SongRetriever songRetriever;

    @Test
    @DisplayName("Should throw DataConflictException when genre has assigned songs")
    void should_throw_exception_when_genre_has_songs() {
        // given
        Long genreId = 1L;
        when(songRepository.existsByGenreId(genreId)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> songRetriever.validateGenreHasNoActiveSongs(genreId))
                .isInstanceOf(DataConflictException.class)
                .hasMessageContaining("has songs assigned");
    }

    @Test
    @DisplayName("Should do nothing when genre has no assigned songs")
    void should_do_nothing_when_genre_has_no_songs() {
        // given
        Long genreId = 1L;
        when(songRepository.existsByGenreId(genreId)).thenReturn(false);

        // when & then
        songRetriever.validateGenreHasNoActiveSongs(genreId);
        verify(songRepository).existsByGenreId(genreId);
    }

    @Test
    @DisplayName("Should return empty list when ids collection is null")
    void should_return_empty_list_when_collection_is_null_for_active_with_artist() {
        // when
        List<Song> result = songRetriever.getActiveWithArtist((Collection<Long>) null);

        // then
        assertThat(result).isEmpty();
        verifyNoInteractions(songRepository);
    }

    @Test
    @DisplayName("Should return empty list when ids collection is empty")
    void should_return_empty_list_when_collection_is_empty_for_active_with_artist() {
        // when
        List<Song> result = songRetriever.getActiveWithArtist(Collections.emptyList());

        // then
        assertThat(result).isEmpty();
        verifyNoInteractions(songRepository);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when any active song with artist is missing")
    void should_throw_exception_when_active_song_with_artist_missing() {
        // given
        List<Long> requestedIds = List.of(1L, 2L);
        Song foundSong = TestObjectsFactory.createSongWithId(1L, "Found");
        when(songRepository.findActiveWithArtistsByIds(requestedIds)).thenReturn(List.of(foundSong));

        // when & then
        assertThatThrownBy(() -> songRetriever.getActiveWithArtist(requestedIds))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should return all requested active songs with artists")
    void should_return_all_requested_active_songs_with_artists() {
        // given
        List<Long> requestedIds = List.of(1L, 2L);
        Song song1 = TestObjectsFactory.createSongWithId(1L, "S1");
        Song song2 = TestObjectsFactory.createSongWithId(2L, "S2");
        when(songRepository.findActiveWithArtistsByIds(requestedIds)).thenReturn(List.of(song1, song2));

        // when
        List<Song> result = songRetriever.getActiveWithArtist(requestedIds);

        // then
        assertThat(result).containsExactlyInAnyOrder(song1, song2);
    }

    @Test
    @DisplayName("Should return empty set when ids set is null")
    void should_return_empty_set_when_set_is_null_for_active() {
        // when
        Set<Song> result = songRetriever.getActive(null);

        // then
        assertThat(result).isEmpty();
        verifyNoInteractions(songRepository);
    }

    @Test
    @DisplayName("Should return empty set when ids set is empty")
    void should_return_empty_set_when_set_is_empty_for_active() {
        // when
        Set<Song> result = songRetriever.getActive(Collections.emptySet());

        // then
        assertThat(result).isEmpty();
        verifyNoInteractions(songRepository);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when any active song is missing")
    void should_throw_exception_when_active_song_missing() {
        // given
        Set<Long> requestedIds = Set.of(1L, 2L);
        Song foundSong = TestObjectsFactory.createSongWithId(1L, "Found");
        when(songRepository.findByIdIsInAndActiveTrue(requestedIds)).thenReturn(Set.of(foundSong));

        // when & then
        assertThatThrownBy(() -> songRetriever.getActive(requestedIds))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Song");
    }

    @Test
    @DisplayName("Should return all requested active songs")
    void should_return_all_requested_active_songs() {
        // given
        Set<Long> requestedIds = Set.of(1L, 2L);
        Song song1 = TestObjectsFactory.createSongWithId(1L, "S1");
        Song song2 = TestObjectsFactory.createSongWithId(2L, "S2");
        when(songRepository.findByIdIsInAndActiveTrue(requestedIds)).thenReturn(Set.of(song1, song2));

        // when
        Set<Song> result = songRetriever.getActive(requestedIds);

        // then
        assertThat(result).containsExactlyInAnyOrder(song1, song2);
    }


    @Test
    @DisplayName("Should return empty slice when no songs match criteria (maxPrice is null)")
    @SuppressWarnings("unchecked")
    void should_return_empty_slice_when_no_songs_found_without_max_price() {
        // given
        SongDto.SearchCriteria searchCriteria = mock(SongDto.SearchCriteria.class);
        when(searchCriteria.maxPrice()).thenReturn(null);
        Pageable pageable = PageRequest.of(0, 10);

        when(songRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl(Collections.emptyList()));

        // when
        Slice<SongDto.Summary> result = songRetriever.findAll(searchCriteria, pageable);

        // then
        assertThat(result.isEmpty()).isTrue();
        verifyNoInteractions(licensingFacade);
    }

    @Test
    @DisplayName("Should consult licensing facade but return empty slice when no songs match criteria (maxPrice is present)")
    @SuppressWarnings("unchecked")
    void should_return_empty_slice_when_no_songs_found_with_max_price() {
        // given
        SongDto.SearchCriteria searchCriteria = mock(SongDto.SearchCriteria.class);
        when(searchCriteria.maxPrice()).thenReturn(BigDecimal.TEN);
        when(searchCriteria.currency()).thenReturn("PLN");
        when(searchCriteria.license()).thenReturn("STANDARD");
        Pageable pageable = PageRequest.of(0, 10);

        when(licensingFacade.findSongIdByMaxPrice("PLN", "STANDARD", BigDecimal.TEN))
                .thenReturn(Set.of(99L));

        when(songRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // when
        Slice<SongDto.Summary> result = songRetriever.findAll(searchCriteria, pageable);

        // then
        assertThat(result.isEmpty()).isTrue();
        verify(licensingFacade).findSongIdByMaxPrice(any(), any(), any());
    }

    @Test
    @DisplayName("Should map and return slice when songs are found (maxPrice is null)")
    @SuppressWarnings("unchecked")
    void should_return_mapped_slice_when_songs_found_without_max_price() {
        // given
        SongDto.SearchCriteria searchCriteria = mock(SongDto.SearchCriteria.class);
        when(searchCriteria.maxPrice()).thenReturn(null);
        Pageable pageable = PageRequest.of(0, 10);

        Song song = TestObjectsFactory.createSongWithId(1L, "Title");
        Page<Song> songPage = new PageImpl<>(List.of(song), pageable, 1);

        when(songRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(songPage);

        List<SongPriceDto> priceList = List.of(mock(SongPriceDto.class));
        when(licensingFacade.getMultiplePricingDto(List.of(1L))).thenReturn(Map.of(1L, priceList));

        SongDto.Summary summaryDto = mock(SongDto.Summary.class);
        when(songMapper.toSummaryDto(song, priceList)).thenReturn(summaryDto);

        // when
        Slice<SongDto.Summary> result = songRetriever.findAll(searchCriteria, pageable);

        // then
        assertThat(result.getContent()).containsExactly(summaryDto);
    }

    @Test
    @DisplayName("Should map and return slice when songs are found (maxPrice is present)")
    @SuppressWarnings("unchecked")
    void should_return_mapped_slice_when_songs_found_with_max_price() {
        // given
        SongDto.SearchCriteria searchCriteria = mock(SongDto.SearchCriteria.class);
        when(searchCriteria.maxPrice()).thenReturn(BigDecimal.TEN);
        when(searchCriteria.currency()).thenReturn("PLN");
        when(searchCriteria.license()).thenReturn("STANDARD");
        Pageable pageable = PageRequest.of(0, 10);

        when(licensingFacade.findSongIdByMaxPrice("PLN", "STANDARD", BigDecimal.TEN))
                .thenReturn(Set.of(1L));

        Song song = TestObjectsFactory.createSongWithId(1L, "Title");
        Page <Song> songPage = new PageImpl<>(List.of(song), pageable, 1);

        when(songRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(songPage);

        when(licensingFacade.getMultiplePricingDto(List.of(1L))).thenReturn(Collections.emptyMap());

        SongDto.Summary summaryDto = mock(SongDto.Summary.class);
        when(songMapper.toSummaryDto(song, Collections.emptyList())).thenReturn(summaryDto);

        // when
        Slice<SongDto.Summary> result = songRetriever.findAll(searchCriteria, pageable);

        // then
        assertThat(result.getContent()).containsExactly(summaryDto);
    }
}