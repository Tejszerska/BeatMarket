package com.spring.beatmarket.infrastructure.audio;

import com.spring.beatmarket.domain.catalog.AudioFileExtension;
import com.spring.beatmarket.domain.catalog.exception.UnreadableAudioFileException;
import org.apache.tika.metadata.Metadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

class AudioInspectorAdapterTest {

    private final AudioInspectorAdapter inspectorAdapter = new AudioInspectorAdapter();

    @Test
    @DisplayName("Falls back to WAV frame duration when metadata scale is invalid")
    void falls_back_when_metadata_scale_is_invalid() throws Exception {
        // given
        byte[] wavBytes = loadWavFile();
        Metadata metadata = new Metadata();
        metadata.set("xmpDM:duration", "10");
        metadata.set("xmpDM:scale", "not-a-scale");

        try (MockedStatic<TikaAnalysis> tikaAnalysis = Mockito.mockStatic(TikaAnalysis.class)) {
            tikaAnalysis.when(() -> TikaAnalysis.extractMetadatatUsingParser(any()))
                    .thenReturn(metadata);

            // when
            Double duration = inspectorAdapter.inspectDuration(wavBytes, AudioFileExtension.WAV);

            // then
            assertThat(duration).isCloseTo(6.0, org.assertj.core.data.Percentage.withPercentage(10.0));
        }
    }

    @Test
    @DisplayName("Falls back to MP3 frame analysis when metadata is missing")
    void falls_back_to_mp3_frame_analysis_when_metadata_is_missing() throws Exception {
        // given
        byte[] mp3Bytes = loadMp3File();
        Metadata metadata = new Metadata();

        try (MockedStatic<TikaAnalysis> tikaAnalysis = Mockito.mockStatic(TikaAnalysis.class)) {
            tikaAnalysis.when(() -> TikaAnalysis.extractMetadatatUsingParser(any()))
                    .thenReturn(metadata);

            // when
            Double duration = inspectorAdapter.inspectDuration(mp3Bytes, AudioFileExtension.MP3);

            // then
            assertThat(duration).isPositive();
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

    private byte[] loadWavFile() throws Exception {
        try (var inputStream = getClass().getResourceAsStream("/wavFile.wav")) {
            if (inputStream == null) {
                throw new IllegalStateException("WAV test fixture was not found");
            }
            return inputStream.readAllBytes();
        }
    }

    private byte[] loadMp3File() throws Exception {
        try (var inputStream = getClass().getResourceAsStream("/mp3Preview.MP3")) {
            if (inputStream == null) {
                throw new IllegalStateException("MP3 test fixture was not found");
            }
            return inputStream.readAllBytes();
        }
    }
}
