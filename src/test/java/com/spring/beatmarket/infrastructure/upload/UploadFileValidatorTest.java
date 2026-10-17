package com.spring.beatmarket.infrastructure.upload;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UploadFileValidatorTest {
    private final UploadFileValidator validator = new UploadFileValidator();

    @Test
    @DisplayName("Rejects a file with an unsupported extension")
    void rejects_file_with_unsupported_extension() {
        // given
        UploadFilePolicy policy = policy(10, ".wav");
        MockMultipartFile file = new MockMultipartFile("file", "track.mp3", "audio/mpeg", new byte[]{1});

        // when & then
        assertThatThrownBy(() -> validator.validate(file, policy))
                .isInstanceOf(InvalidUploadFileException.class)
                .hasMessage("Unsupported format.");
    }

    @Test
    @DisplayName("Rejects an empty file with an allowed extension")
    void rejects_empty_file_with_allowed_extension() {
        // given
        UploadFilePolicy policy = policy(10, ".wav");
        MockMultipartFile file = new MockMultipartFile("file", "track.mp3", "audio/mpeg", new byte[0]);

        // when & then
        assertThatThrownBy(() -> validator.validate(file, policy))
                .isInstanceOf(InvalidUploadFileException.class)
                .hasMessage("Uploaded file must not be empty.");
    }

    @Test
    @DisplayName("Rejects a file larger than the configured limit")
    void rejects_file_larger_than_configured_limit() {
        // given
        UploadFilePolicy policy = policy(2, ".wav");
        MockMultipartFile file = new MockMultipartFile("file", "track.wav", "audio/wav", new byte[3]);

        // when & then
        assertThatThrownBy(() -> validator.validate(file, policy))
                .isInstanceOf(UploadFileTooBigException.class)
                .hasMessage("Uploaded file exceeds the maximum allowed size.");
    }

    @Test
    @DisplayName("Accepts a file at the configured size limit")
    void accepts_file_at_configured_size_limit() {
        // given
        UploadFilePolicy policy = policy(2, ".wav");
        MockMultipartFile file = new MockMultipartFile("file", "track.WAV", "audio/wav", new byte[2]);

        // when
        assertThatCode(() -> validator.validate(file, policy))
                .doesNotThrowAnyException();

        // then
    }

    private UploadFilePolicy policy(final long maxSizeBytes, final String extension) {
        return new UploadFilePolicy(
                Set.of(extension),
                maxSizeBytes,
                "Unsupported format."
        );
    }
}
