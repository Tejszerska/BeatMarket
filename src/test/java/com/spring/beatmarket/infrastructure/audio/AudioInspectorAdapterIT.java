package com.spring.beatmarket.infrastructure.audio;

import com.spring.beatmarket.domain.catalog.AudioFileExtension;
import com.spring.beatmarket.domain.catalog.exception.UnreadableAudioFileException;
import jdk.jfr.Description;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

class AudioInspectorAdapterIT {
    private final AudioInspectorAdapter inspectorAdapter = new AudioInspectorAdapter();

    @Test
    @Description(value = "Should return correct duration of WAV file in seconds.")
    void should_return_correct_duration_of_WAV_file() {
        // given
        byte[] bytes = loadFile(AudioFileExtension.WAV);
        double fileDuration = 6.0;

        // when
        Double inspectDuration = inspectorAdapter.inspectDuration(bytes, AudioFileExtension.WAV);

        //then
        System.out.println(inspectDuration);
        assertThat(inspectDuration).isCloseTo(fileDuration, Percentage.withPercentage(10.0));

    }

    @Test
    @Description(value = "Should return correct file extension for WAV file")
    void should_return_correct_file_extension_for_WAV_file() {
        // given
        AudioFileExtension fileExtension = AudioFileExtension.WAV;
        byte[] bytes = loadFile(fileExtension);

        // when
        AudioFileExtension audioFileExtension = inspectorAdapter.inspectExtension(bytes);

        //then
        assertThat(audioFileExtension).isEqualTo(fileExtension);

    }

    @Test
    @Description(value = "Should return correct file extension for FLAC file")
    void should_return_correct_file_extension_for_FLAC_file() {
        // given
        byte[] bytes = loadFile(AudioFileExtension.FLAC);

        // when
        AudioFileExtension extension = inspectorAdapter.inspectExtension(bytes);

        // then
        assertThat(extension).isEqualTo(AudioFileExtension.FLAC);
    }

    @Test
    @Description(value = "Should return correct duration of FLAC file in seconds.")
    void should_return_correct_duration_of_FLAC_file() {
        // given
        byte[] bytes = loadFile(AudioFileExtension.FLAC);
        double expectedDuration = 11.0;

        // when
        Double inspectDuration = inspectorAdapter.inspectDuration(bytes, AudioFileExtension.FLAC);

        // then
        assertThat(inspectDuration).isCloseTo(expectedDuration, Percentage.withPercentage(10.0));
    }

    @Test
    @Description(value = "Should return null extension when file format is unrecognized")
    void should_return_null_when_file_format_is_unrecognized() {
        // given
        byte[] randomBytes = new byte[]{1, 2, 3, 4, 5};

        // when
        AudioFileExtension extension = inspectorAdapter.inspectExtension(randomBytes);

        // then
        assertThat(extension).isNull();
    }

    @Test
    @Description(value = "Should throw UnreadableAudioFileException when WAV file is corrupted")
    void should_throw_exception_when_WAV_file_is_corrupted() {
        // given
        byte[] corruptedBytes = new byte[]{0, 0, 0, 0};

        // when & then
        assertThatThrownBy(() ->
                inspectorAdapter.inspectDuration(corruptedBytes, AudioFileExtension.WAV)
        ).isInstanceOf(UnreadableAudioFileException.class);
    }

    @Test
    @Description(value = "Should throw UnreadableAudioFileException when FLAC file is corrupted")
    void should_throw_exception_when_FLAC_file_is_corrupted() {
        // given
        byte[] corruptedBytes = new byte[]{0, 0, 0, 0};

        // when & then
        assertThatThrownBy(() ->
                inspectorAdapter.inspectDuration(corruptedBytes, AudioFileExtension.FLAC)
        ).isInstanceOf(UnreadableAudioFileException.class);
    }

    @Test
    @Description(value = "Should throw UnreadableAudioFileException when Tika parsing fails")
    void should_throw_exception_when_tika_parsing_fails() {
        // given
        byte[] validBytes = new byte[]{1, 2, 3};

        // when & then
        try (MockedStatic<TikaAnalysis> mockedTika = Mockito.mockStatic(TikaAnalysis.class)) {
            mockedTika.when(() -> TikaAnalysis.extractMetadatatUsingParser(any()))
                    .thenThrow(new TikaException("Simulated Tika parser failure"));

            assertThatThrownBy(() ->
                    inspectorAdapter.inspectDuration(validBytes, AudioFileExtension.WAV)
            ).isInstanceOf(UnreadableAudioFileException.class);
        }
    }

    @Test
    @Description(value = "Should parse duration from metadata")
    void should_parse_duration_from_metadata() {
        // given
        byte[] randomBytes = new byte[]{1, 2, 3,};
        AudioFileExtension extension = AudioFileExtension.WAV;
        double fileDuration = 10.0;
        Metadata metadata = new Metadata();
        metadata.set("xmpDM:duration", "10000");
        try (MockedStatic<TikaAnalysis> mockedTika = Mockito.mockStatic(TikaAnalysis.class)) {
            mockedTika.when(() -> TikaAnalysis.extractMetadatatUsingParser(any()))
                    .thenReturn(metadata);

            // when
            Double inspectDuration = inspectorAdapter.inspectDuration(randomBytes, extension);

            // then
            assertThat(inspectDuration).isCloseTo(fileDuration, Percentage.withPercentage(10.0));
        }
    }

    @Test
    @Description(value = "Should fallback to byte calculation when metadata duration is unparseable")
    void should_fallback_when_metadata_duration_is_unparseable() {
        // given
        byte[] validBytes = loadFile(AudioFileExtension.WAV);
        AudioFileExtension extension = AudioFileExtension.WAV;

        Metadata metadata = new Metadata();
        metadata.set("xmpDM:duration", "not_a_number");

        try (MockedStatic<TikaAnalysis> mockedTika = Mockito.mockStatic(TikaAnalysis.class)) {
            mockedTika.when(() -> TikaAnalysis.extractMetadatatUsingParser(any()))
                    .thenReturn(metadata);

            // when
            Double inspectDuration = inspectorAdapter.inspectDuration(validBytes, extension);

            // then
            double fileDuration = 6.0;
            assertThat(inspectDuration).isCloseTo(fileDuration, Percentage.withPercentage(10.0));
        }
    }

    @Test
    @Description(value = "Should return null duration when fallback calculation receives unsupported extension")
    void should_return_null_for_unsupported_extension_in_fallback() {
        // given
        byte[] invalidBytes = new byte[]{1, 2, 3};
        AudioFileExtension unsupportedExtension = null;

        // when
        Double duration = inspectorAdapter.inspectDuration(invalidBytes, unsupportedExtension);

        // then
        assertThat(duration).isNull();
    }

    @Test
    @Description(value = "Should return null extension when Tika cannot detect content type")
    void should_return_null_when_tika_returns_null() {
        // given
        byte[] someBytes = new byte[]{1, 2, 3};

        // when & then
        try (MockedConstruction<Tika> mockedTikaConstruction = Mockito.mockConstruction(Tika.class,
                (mock, context) -> Mockito.when(mock.detect(someBytes)).thenReturn(null))) {

            AudioFileExtension extension = inspectorAdapter.inspectExtension(someBytes);

            assertThat(extension).isNull();
        }
    }

    @ParameterizedTest(name = "Should map MIME type ''{0}'' to extension {1}")
    @CsvSource({
            "audio/wav, WAV",
            "audio/vnd.wave, WAV",
            "audio/x-wav, WAV",
            "audio/flac, FLAC",
            "audio/x-flac, FLAC",
            "audio/mpeg, MP3",
            "audio/mp3, MP3"
    })
    void should_map_content_type_to_correct_extension(String mimeType, AudioFileExtension expectedExtension) {
        // given
        byte[] someBytes = new byte[]{1, 2, 3};

        // when & then
        try (MockedConstruction<Tika> mockedTika = Mockito.mockConstruction(Tika.class,
                (mock, context) -> Mockito.when(mock.detect(someBytes)).thenReturn(mimeType))) {

            AudioFileExtension extension = inspectorAdapter.inspectExtension(someBytes);

            assertThat(extension).isEqualTo(expectedExtension);
        }
    }

    byte[] loadFile(AudioFileExtension extension) {
        String fileName = switch (extension) {
            case WAV -> "/wavFile.wav";
            case FLAC -> "/flacFile.flac";
            default -> throw new IllegalArgumentException("Unsupported file type: " + extension);
        };

        try (InputStream inputStream = getClass().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new RuntimeException("File not found in resources: " + fileName);
            }
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read test file", e);
        }
    }

}
