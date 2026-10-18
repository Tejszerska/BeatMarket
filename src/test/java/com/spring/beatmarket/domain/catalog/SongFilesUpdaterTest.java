package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.domain.licensing.LicensingFacade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SongFilesUpdaterTest {
    private final SongRetriever songRetriever = mock(SongRetriever.class);
    private final AudioFileValidator fileValidator = mock(AudioFileValidator.class);
    private final FileStoragePort fileStoragePort = mock(FileStoragePort.class);
    private final LicensingFacade licensingFacade = mock(LicensingFacade.class);
    private final SongFilesUpdater songFilesUpdater = new SongFilesUpdater(
            songRetriever,
            fileValidator,
            fileStoragePort,
            licensingFacade,
            new FileKeyGenerator()
    );

    @AfterEach
    void clearTransactionSynchronization() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    @DisplayName("Should return song ID and upload track with the matching content type")
    void should_return_song_id_and_upload_track_with_content_type() {
        // given
        TransactionSynchronizationManager.initSynchronization();
        Song song = createSong(17L, null, null);
        byte[] trackBytes = {1, 2, 3};
        when(songRetriever.getLazily(17L)).thenReturn(song);
        when(fileValidator.validateFullTrack(trackBytes, song.getDuration())).thenReturn(AudioFileExtension.FLAC);

        // when
        SongDto.Track response = songFilesUpdater.updateTrackFile(trackBytes, 17L);

        // then
        assertThat(response).isEqualTo(new SongDto.Track(17L, "Track uploaded successfully"));
        String trackFileKey = song.getTrackFileKey();
        assertThat(trackFileKey).matches("tracks/test-song-\\d{5}-[a-f0-9]{8}\\.FLAC");
        verify(fileStoragePort).upload(trackBytes, trackFileKey, "audio/flac");
    }

    @Test
    @DisplayName("Should return the storage URL and remove the previous preview after commit")
    void should_return_storage_url_and_remove_previous_preview_after_commit() {
        // given
        TransactionSynchronizationManager.initSynchronization();
        Song song = createSong(18L, "previews/old-preview.mp3", null);
        byte[] previewBytes = {4, 5, 6};
        when(songRetriever.getLazily(18L)).thenReturn(song);
        when(fileValidator.validatePreviewTrack(previewBytes, song.getDuration()))
                .thenReturn(AudioFileExtension.MP3);
        when(fileStoragePort.urlFor(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn("https://cdn.example/preview.mp3");

        // when
        SongDto.Preview response = songFilesUpdater.updatePreviewFile(previewBytes, 18L);

        // then
        assertThat(response.songId()).isEqualTo(18L);
        assertThat(response.previewUrl()).isEqualTo("https://cdn.example/preview.mp3");
        verify(fileStoragePort).upload(previewBytes, song.getPreviewFileKey(), "audio/mpeg");
        verify(fileStoragePort).urlFor(song.getPreviewFileKey());
        verify(fileStoragePort, org.mockito.Mockito.never()).delete("previews/old-preview.mp3");

        List<TransactionSynchronization> synchronizations =
                TransactionSynchronizationManager.getSynchronizations();
        synchronizations.get(0).afterCommit();
        verify(fileStoragePort).delete("previews/old-preview.mp3");
    }

    @Test
    @DisplayName("Should remove the new track and preserve the previous track after rollback")
    void should_remove_new_track_after_rollback() {
        // given
        TransactionSynchronizationManager.initSynchronization();
        Song song = createSong(19L, null, "tracks/old-track.wav");
        byte[] trackBytes = {1, 2, 3};
        when(songRetriever.getLazily(19L)).thenReturn(song);
        when(fileValidator.validateFullTrack(trackBytes, song.getDuration())).thenReturn(AudioFileExtension.WAV);
        when(licensingFacade.hasAnyCurrentLicenses("tracks/old-track.wav")).thenReturn(false);

        // when
        songFilesUpdater.updateTrackFile(trackBytes, 19L);

        // then
        String newFileKey = song.getTrackFileKey();
        TransactionSynchronizationManager.getSynchronizations()
                .get(0)
                .afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);

        verify(fileStoragePort).delete(newFileKey);
        verify(fileStoragePort, org.mockito.Mockito.never()).delete("tracks/old-track.wav");
    }

    private Song createSong(final Long id, final String previewFileKey, final String trackFileKey) {
        Song song = Song.builder()
                .title("Test Song")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(180)
                .language(SongLanguage.EN)
                .previewFileKey(previewFileKey)
                .trackFileKey(trackFileKey)
                .build();
        ReflectionTestUtils.setField(song, "id", id);
        return song;
    }
}
