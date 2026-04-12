package com.spring.songify.infrastructure.crud.controller.song.dto.request;

import com.spring.songify.domain.crud.SongLanguage;
import com.spring.songify.domain.crud.dto.GenreDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Set;

public record UpdateSongRequestDto(
        @NotBlank(message = "Title must be declared")
        String title,
        @NotNull(message = "Genre ID must be declared")
        Long genreId,
        @NotNull(message = "Release date must be declared")
        Instant releaseDate,
        @NotNull(message = "Duration must be declared")
        Long duration,
        @NotNull(message = "Song's language must be declared")
        SongLanguage language,
        @NotNull(message = "Set of Album's IDs must be declared")
        Set<Long> albumId
) {
}

