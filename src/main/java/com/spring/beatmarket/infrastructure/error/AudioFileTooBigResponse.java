package com.spring.beatmarket.infrastructure.error;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AudioFileTooBigResponse")
public record AudioFileTooBigResponse(
        @Schema(description = "General error message", example = "File is too big for given format.")
        String message,

        @Schema(description = "Size of the uploaded file [MB]", example = "180")
        double fileSize,

        @Schema(description = "Max file size [MB]", example = "165")
        double maxSize
) {
}