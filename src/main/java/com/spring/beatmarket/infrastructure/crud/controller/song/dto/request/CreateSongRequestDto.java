package com.spring.beatmarket.infrastructure.crud.controller.song.dto.request;

import com.spring.beatmarket.domain.crud.SongLanguage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

@Schema(description = "Payload for creating a new song")
public record CreateSongRequestDto(
        @Schema(description = "Title of the song", example = "In the End")
        @NotBlank(message = "title must be declared")
        String title,

        @Schema(description = "Release date of the song in ISO format", example = "2000-10-24T00:00:00Z")
        @NotNull(message = "releaseDate must be declared")
        Instant releaseDate,

        @Schema(description = "Duration of the song in seconds", example = "216")
        @NotNull(message = "duration must be declared")
        @Positive(message = "duration must be a positive number")
        Long duration,

        @Schema(description = "Language of the song", example = "ENGLISH")
        @NotNull(message = "language must be declared")
        SongLanguage language
) {
}

