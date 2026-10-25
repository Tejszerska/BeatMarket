package com.spring.beatmarket.infrastructure.domain.catalog.controller.song;

import com.spring.beatmarket.domain.catalog.SongFacade;
import com.spring.beatmarket.domain.catalog.SongLanguage;
import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.infrastructure.upload.InvalidUploadFileException;
import com.spring.beatmarket.infrastructure.upload.UploadFileValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SongControllerTest {
    private final SongFacade facade = mock(SongFacade.class);
    private final SongControllerMapper mapper = mock(SongControllerMapper.class);
    private final SongController controller = new SongController(facade, mapper, new UploadFileValidator());

    @Test
    @DisplayName("Searches songs using valid price criteria")
    void searches_songs_with_valid_price_criteria() {
        // given
        SongApiDto.SearchRequest request = new SongApiDto.SearchRequest(
                null, null, null, null, null, null, null,
                java.math.BigDecimal.TEN, "USD", "STANDARD");
        SongDto.SearchCriteria criteria = new SongDto.SearchCriteria(
                null, null, null, null, null, null, null,
                java.math.BigDecimal.TEN, "USD", "STANDARD");
        Pageable pageable = Pageable.ofSize(10);
        @SuppressWarnings("unchecked")
        Slice<SongDto.Summary> slice = mock(Slice.class);
        SongApiDto.GetAllResponse expected = new SongApiDto.GetAllResponse(List.of(), false);
        when(mapper.toDomainSearchCriteria(request)).thenReturn(criteria);
        when(facade.searchSongs(criteria, pageable)).thenReturn(slice);
        when(mapper.toGetAllResponse(slice)).thenReturn(expected);

        // when
        var response = controller.searchSongs(request, pageable);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Rejects max price search when currency is missing")
    void rejects_max_price_search_without_currency() {
        // given
        SongApiDto.SearchRequest request = new SongApiDto.SearchRequest(
                null, null, null, null, null, null, null,
                java.math.BigDecimal.TEN, null, "STANDARD");

        // when & then
        assertThatThrownBy(() -> controller.searchSongs(request, Pageable.ofSize(10)))
                .isInstanceOf(InvalidSearchCriteriaException.class)
                .hasMessage("Filtering by maxPrice requires declaring currency and license.");
        verifySearchIsSkipped();
    }

    @Test
    @DisplayName("Rejects max price search when license is missing")
    void rejects_max_price_search_without_license() {
        // given
        SongApiDto.SearchRequest request = new SongApiDto.SearchRequest(
                null, null, null, null, null, null, null,
                java.math.BigDecimal.TEN, "USD", null);

        // when & then
        assertThatThrownBy(() -> controller.searchSongs(request, Pageable.ofSize(10)))
                .isInstanceOf(InvalidSearchCriteriaException.class)
                .hasMessage("Filtering by maxPrice requires declaring currency and license.");
        verifySearchIsSkipped();
    }

    @Test
    @DisplayName("Returns song details for the requested ID")
    void returns_song_details() {
        // given
        SongDto.Details details = new SongDto.Details(
                12L, "Song", "EN", 180, LocalDate.of(2020, 1, 1),
                null, List.of(), null, null, Map.of());
        SongApiDto.DetailsResponse expected = new SongApiDto.DetailsResponse(
                12L, "Song", "EN", 180, LocalDate.of(2020, 1, 1),
                null, List.of(), null, null, Map.of());
        when(facade.getSongDetails(12L)).thenReturn(details);
        when(mapper.toDetailsResponse(details)).thenReturn(expected);

        // when
        var response = controller.getSongById(12L);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Creates a song and returns a created response")
    void creates_song() {
        // given
        SongApiDto.CreateRequest request = new SongApiDto.CreateRequest(
                "Song", LocalDate.of(2020, 1, 1), 180, SongLanguage.EN,
                null, null, List.of(), null);
        SongDto.Create domainRequest = new SongDto.Create(
                "Song", LocalDate.of(2020, 1, 1), 180, SongLanguage.EN,
                null, null, List.of(), null);
        SongDto.Info created = new SongDto.Info(
                12L, "Song", 180, "EN", LocalDate.of(2020, 1, 1),
                List.of(), null, null);
        SongApiDto.InfoResponse expected = new SongApiDto.InfoResponse(
                12L, "Song", 180, "EN", LocalDate.of(2020, 1, 1),
                List.of(), null, null);
        when(mapper.toDomainCreate(request)).thenReturn(domainRequest);
        when(facade.addSong(domainRequest)).thenReturn(created);
        when(mapper.toInfoResponse(created)).thenReturn(expected);

        // when
        var response = controller.createSong(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should return song ID when uploading a full track")
    void should_return_song_id_when_uploading_full_track() throws Exception {
        // given
        Long songId = 12L;
        byte[] bytes = {1, 2, 3};
        MockMultipartFile file = new MockMultipartFile("file", "track.FLAC", "audio/flac", bytes);
        SongDto.Track track = new SongDto.Track(songId, "Track uploaded successfully");
        SongApiDto.TrackResponse response = new SongApiDto.TrackResponse(songId, track.message());
        when(facade.addTrackFile(bytes, songId)).thenReturn(track);
        when(mapper.toTrackResponse(track)).thenReturn(response);

        // when
        ResponseEntity<SongApiDto.TrackResponse> result = controller.uploadFullTrack(file, songId);

        // then
        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    @DisplayName("Should return song ID and preview URL when uploading a preview")
    void should_return_song_id_and_preview_url_when_uploading_preview() throws Exception {
        // given
        Long songId = 13L;
        byte[] bytes = {4, 5, 6};
        MockMultipartFile file = new MockMultipartFile("file", "preview.MP3", "audio/mpeg", bytes);
        SongDto.Preview preview = new SongDto.Preview(songId, "https://cdn.example/preview.mp3");
        SongApiDto.PreviewResponse response = new SongApiDto.PreviewResponse(songId, preview.previewUrl());
        when(facade.addPreviewFile(bytes, songId)).thenReturn(preview);
        when(mapper.toPreviewResponse(preview)).thenReturn(response);

        // when
        ResponseEntity<SongApiDto.PreviewResponse> result = controller.uploadPreview(file, songId);

        // then
        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    @DisplayName("Updates a song and returns the updated response")
    void updates_song() {
        // given
        SongApiDto.UpdateRequest request = new SongApiDto.UpdateRequest(
                null, null, null, null, null, null, null, null);
        SongDto.Update domainRequest = new SongDto.Update(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        SongDto.Info updated = new SongDto.Info(
                12L, "Updated", 180, "EN", LocalDate.of(2020, 1, 1),
                List.of(), null, null);
        SongApiDto.InfoResponse expected = new SongApiDto.InfoResponse(
                12L, "Updated", 180, "EN", LocalDate.of(2020, 1, 1),
                List.of(), null, null);
        when(mapper.toDomainUpdate(request)).thenReturn(domainRequest);
        when(facade.updateSong(12L, domainRequest)).thenReturn(updated);
        when(mapper.toInfoResponse(updated)).thenReturn(expected);

        // when
        var response = controller.updateSong(12L, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Deactivates a song and returns no content")
    void deactivates_song() {
        // when
        var response = controller.deleteSong(12L);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(facade).deactivateSong(12L);
    }

    @Test
    @DisplayName("Should reject an unsupported track extension before upload")
    void should_reject_unsupported_track_extension() {
        // given
        byte[] bytes = {1};
        MockMultipartFile file = new MockMultipartFile("file", "track.mp3", "audio/mpeg", bytes);

        // when & then
        assertThatThrownBy(() -> controller.uploadFullTrack(file, 12L))
                .isInstanceOf(InvalidUploadFileException.class)
                .hasMessage("Invalid file format. Only audio/wav (WAV) and audio/flac (FLAC) are supported.");
        verify(facade, never()).addTrackFile(bytes, 12L);
    }

    @Test
    @DisplayName("Should reject an empty preview file")
    void should_reject_empty_preview_file() {
        // given
        byte[] bytes = {};
        MockMultipartFile file = new MockMultipartFile("file", "preview.mp3", "audio/mpeg", bytes);

        // when & then
        assertThatThrownBy(() -> controller.uploadPreview(file, 13L))
                .isInstanceOf(InvalidUploadFileException.class)
                .hasMessage("Uploaded file must not be empty.");
        verify(facade, never()).addPreviewFile(bytes, 13L);
    }

    private void verifySearchIsSkipped() {
        verifyNoInteractions(facade, mapper);
    }
}
