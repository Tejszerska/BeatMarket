package com.spring.beatmarket.infrastructure.audio;

import com.spring.beatmarket.domain.catalog.AudioFileExtension;
import com.spring.beatmarket.domain.catalog.exception.UnreadableAudioFileException;
import org.apache.tika.metadata.Metadata;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

class AudioInspectorAdapterTest {

    private final AudioInspectorAdapter inspectorAdapter = new AudioInspectorAdapter();

    @Test
    @DisplayName("Falls back to WAV frame duration when metadata scale is invalid")
    void falls_back_to_wav_frame_duration_when_metadata_scale_is_invalid() {
        // given
        byte[] wavBytes = loadFile(AudioFileExtension.WAV);
        Metadata metadata = new Metadata();
        metadata.set("xmpDM:duration", "10");
        metadata.set("xmpDM:scale", "not-a-scale");

        try (MockedStatic<TikaAnalysis> tikaAnalysis = Mockito.mockStatic(TikaAnalysis.class)) {
            tikaAnalysis.when(() -> TikaAnalysis.extractMetadatatUsingParser(any()))
                    .thenReturn(metadata);

            // when
            Double duration = inspectorAdapter.inspectDuration(wavBytes, AudioFileExtension.WAV);

            // then
            assertThat(duration).isCloseTo(6.0, Percentage.withPercentage(10.0));
        }
    }

    @Test
    @DisplayName("Falls back to MP3 frame analysis when metadata is missing")
    void falls_back_to_mp3_frame_analysis_when_metadata_is_missing() {
        // given
        byte[] mp3Bytes = loadFile(AudioFileExtension.MP3);
        Metadata metadata = new Metadata();

        try (MockedStatic<TikaAnalysis> tikaAnalysis = Mockito.mockStatic(TikaAnalysis.class)) {
            tikaAnalysis.when(() -> TikaAnalysis.extractMetadatatUsingParser(any()))
                    .thenReturn(metadata);

            // when
            Double duration = inspectorAdapter.inspectDuration(mp3Bytes, AudioFileExtension.MP3);

            // then
            assertThat(duration).isPositive().isLessThan(900.0);
        }
    }

    @Test
    @DisplayName("Falls back to FLAC frame analysis when metadata is missing")
    void falls_back_to_flac_frame_analysis_when_metadata_is_missing() {
        // given
        byte[] flacBytes = loadFile(AudioFileExtension.FLAC);
        Metadata metadata = new Metadata();

        try (MockedStatic<TikaAnalysis> tikaAnalysis = Mockito.mockStatic(TikaAnalysis.class)) {
            tikaAnalysis.when(() -> TikaAnalysis.extractMetadatatUsingParser(any()))
                    .thenReturn(metadata);

            // when
            Double duration = inspectorAdapter.inspectDuration(flacBytes, AudioFileExtension.FLAC);

            // then
            assertThat(duration).isCloseTo(11.0, Percentage.withPercentage(10.0));
        }
    }

    @Test
    @DisplayName("Falls back to AAC ADTS analysis when metadata is missing")
    void falls_back_to_aac_adts_analysis_when_metadata_is_missing() {
        // given
        byte[] aacBytes = loadFile(AudioFileExtension.AAC);
        Metadata metadata = new Metadata();

        try (MockedStatic<TikaAnalysis> tikaAnalysis = Mockito.mockStatic(TikaAnalysis.class)) {
            tikaAnalysis.when(() -> TikaAnalysis.extractMetadatatUsingParser(any()))
                    .thenReturn(metadata);

            // when
            Double duration = inspectorAdapter.inspectDuration(aacBytes, AudioFileExtension.AAC);

            // then
            assertThat(duration).isCloseTo(3.0, Percentage.withPercentage(10.0));
        }
    }

    @Test
    @DisplayName("Rejects an unreadable MP3 when metadata and frame analysis fail")
    void rejects_unreadable_mp3_when_frame_analysis_fails() {
        // given
        byte[] invalidMp3 = new byte[]{1, 2, 3, 4};
        Metadata metadata = new Metadata();

        try (MockedStatic<TikaAnalysis> tikaAnalysis = Mockito.mockStatic(TikaAnalysis.class)) {
            tikaAnalysis.when(() -> TikaAnalysis.extractMetadatatUsingParser(any()))
                    .thenReturn(metadata);

            // when & then
            assertThatThrownBy(() -> inspectorAdapter.inspectDuration(invalidMp3, AudioFileExtension.MP3))
                    .isInstanceOf(UnreadableAudioFileException.class)
                    .hasMessage("An error occurred while analysing duration of a MP3 audio file.");
        }
    }

    private byte[] loadFile(AudioFileExtension extension) {
        String fileName = switch (extension) {
            case WAV -> "/wavFile.wav";
            case FLAC -> "/flacFile.flac";
            case MP3 -> "/mp3Preview.MP3";
            case AAC -> "/aacPreview.aac";
            default -> throw new IllegalArgumentException("Unsupported file type: " + extension);
        };

        try (InputStream inputStream = getClass().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IllegalStateException("Audio test fixture was not found: " + fileName);
            }
            return inputStream.readAllBytes();
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read audio test fixture: " + fileName, exception);
        }
    }
}
