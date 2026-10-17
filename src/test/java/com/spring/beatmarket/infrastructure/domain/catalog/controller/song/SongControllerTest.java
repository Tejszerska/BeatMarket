package com.spring.beatmarket.infrastructure.domain.catalog.controller.song;

import com.spring.beatmarket.domain.catalog.SongFacade;
import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.infrastructure.upload.InvalidUploadFileException;
import com.spring.beatmarket.infrastructure.upload.UploadFileValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SongControllerTest {
    private final SongFacade facade = mock(SongFacade.class);
    private final SongControllerMapper mapper = mock(SongControllerMapper.class);
    private final SongController controller = new SongController(facade, mapper, new UploadFileValidator());

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
}
