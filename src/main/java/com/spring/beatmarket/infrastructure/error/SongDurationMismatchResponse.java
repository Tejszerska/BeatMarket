package com.spring.beatmarket.infrastructure.error;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SongDurationMismatchResponse")
public record SongDurationMismatchResponse(
        @Schema(description = "General error message", example = "Cannot upload audio file when duration mismatches database record.")
        String message,

        @Schema(description = "Duration of the song declared in database [s.]", example = "217")
        double durationInDatabase
) {
}