package com.spring.songify.infrastructure.crud.controller.song.dto.request;

import com.spring.songify.domain.crud.SongLanguage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record CreateSongRequestDto(
        @NotBlank(message = "title must be declared")
        String title,

        @NotNull(message = "releaseDate must be declared")
        Instant releaseDate,

        @NotNull(message = "duration must be declared")
        @Positive(message = "duration must be a positive number")
        Long duration,

        @NotNull(message = "language must be declared")
        SongLanguage language
) {
}

