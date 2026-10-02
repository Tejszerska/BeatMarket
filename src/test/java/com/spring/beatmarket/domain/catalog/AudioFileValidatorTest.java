package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.exception.AudioFileTooBigException;
import com.spring.beatmarket.domain.catalog.exception.IncorrectAudioFormatException;
import com.spring.beatmarket.domain.catalog.exception.SongDurationMismatchException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AudioFileValidatorTest {

    @Mock
    private AudioInspectorPort inspectorPort;

    @InjectMocks
    private AudioFileValidator validator;

    private static final int DATABASE_DURATION = 120; // 2 minutes
    private static final double MAX_WAV_SIZE_MB = 165.00;
    private static final double MAX_FLAC_SIZE_MB = 95.00;

    @Test
    @DisplayName("Should successfully validate WAV file when size and duration are correct")
    void should_validate_wav_successfully() {
        // given
        byte[] validWavBytes = createBytesOfSizeMB(10.0);
        given(inspectorPort.inspectExtension(validWavBytes)).willReturn(AudioFileExtension.WAV);
        given(inspectorPort.inspectDuration(validWavBytes, AudioFileExtension.WAV)).willReturn(122.0);

        // when
        AudioFileExtension extension = validator.validateFullTrack(validWavBytes, DATABASE_DURATION);

        // then
        assertThat(extension).isEqualTo(AudioFileExtension.WAV);
    }

    @Test
    @DisplayName("Should successfully validate FLAC file when size and duration are correct")
    void should_validate_flac_successfully() {
        // given
        byte[] validFlacBytes = createBytesOfSizeMB(5.0);
        given(inspectorPort.inspectExtension(validFlacBytes)).willReturn(AudioFileExtension.FLAC);
        given(inspectorPort.inspectDuration(validFlacBytes, AudioFileExtension.FLAC)).willReturn(118.0);

        // when
        AudioFileExtension extension = validator.validateFullTrack(validFlacBytes, DATABASE_DURATION);

        // then
        assertThat(extension).isEqualTo(AudioFileExtension.FLAC);
    }

    @Test
    @DisplayName("Should throw IncorrectAudioFormatException when file is neither WAV nor FLAC")
    void should_throw_when_format_is_invalid() {
        // given
        byte[] mp3Bytes = new byte[100];
        given(inspectorPort.inspectExtension(mp3Bytes)).willReturn(AudioFileExtension.MP3);

        // when & then
        assertThatThrownBy(() -> validator.validateFullTrack(mp3Bytes, DATABASE_DURATION))
                .isInstanceOf(IncorrectAudioFormatException.class)
                .hasMessageContaining("Only .WAV or .FLAC files accepted");
    }

    @Test
    @DisplayName("Should throw AudioFileTooBigException when WAV exceeds 165 MB")
    void should_throw_when_wav_is_too_big() {
        // given
        byte[] hugeWavBytes = createBytesOfSizeMB(MAX_WAV_SIZE_MB + 0.1);
        given(inspectorPort.inspectExtension(hugeWavBytes)).willReturn(AudioFileExtension.WAV);

        // when & then
        assertThatThrownBy(() -> validator.validateFullTrack(hugeWavBytes, DATABASE_DURATION))
                .isInstanceOf(AudioFileTooBigException.class);
    }

    @Test
    @DisplayName("Should throw AudioFileTooBigException when FLAC exceeds 95 MB")
    void should_throw_when_flac_is_too_big() {
        // given
        byte[] hugeFlacBytes = createBytesOfSizeMB(MAX_FLAC_SIZE_MB + 0.1);
        given(inspectorPort.inspectExtension(hugeFlacBytes)).willReturn(AudioFileExtension.FLAC);

        // when & then
        assertThatThrownBy(() -> validator.validateFullTrack(hugeFlacBytes, DATABASE_DURATION))
                .isInstanceOf(AudioFileTooBigException.class);
    }

    @Test
    @DisplayName("Should throw SongDurationMismatchException when audio duration is longer than database duration + 3s")
    void should_throw_when_duration_is_too_long() {
        // given
        byte[] trackBytes = createBytesOfSizeMB(10.0);
        given(inspectorPort.inspectExtension(trackBytes)).willReturn(AudioFileExtension.WAV);
        given(inspectorPort.inspectDuration(trackBytes, AudioFileExtension.WAV)).willReturn(123.01); // Przekracza o 0.01s limit 123s

        // when & then
        assertThatThrownBy(() -> validator.validateFullTrack(trackBytes, DATABASE_DURATION))
                .isInstanceOf(SongDurationMismatchException.class);
    }

    @Test
    @DisplayName("Should throw SongDurationMismatchException when audio duration is shorter than database duration - 3s")
    void should_throw_when_duration_is_too_short() {
        // given
        byte[] trackBytes = createBytesOfSizeMB(10.0);
        given(inspectorPort.inspectExtension(trackBytes)).willReturn(AudioFileExtension.WAV);
        given(inspectorPort.inspectDuration(trackBytes, AudioFileExtension.WAV)).willReturn(116.99); // Krótszy niż limit 117s

        // when & then
        assertThatThrownBy(() -> validator.validateFullTrack(trackBytes, DATABASE_DURATION))
                .isInstanceOf(SongDurationMismatchException.class);
    }


    private byte[] createBytesOfSizeMB(double mb) {
        return new byte[(int) (mb * 1024 * 1024)];
    }
}