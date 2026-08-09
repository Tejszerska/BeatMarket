package com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.request;

import com.spring.beatmarket.domain.catalog.SongLanguage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Payload for creating a new song")
public record CreateSongRequest(
        @Schema(description = "Title of the song", example = "In the End")
        @NotBlank(message = "title must be declared")
        String title,

        @Schema(description = "Release date of the song in ISO format", example = "2000-10-24")
        @NotNull(message = "releaseDate must be declared")
        @PastOrPresent(message = "releaseDate cannot be in the future")
        LocalDate releaseDate,

        @Schema(description = "Duration of the song in seconds", example = "156")
        @NotNull(message = "duration must be declared")
        @Positive(message = "duration must be a positive number")
        Long duration,

        @Schema(description = "Language of the song", example = "EN")
        @NotNull(message = "language must be declared")
        SongLanguage language,

        @Schema(description = "ID of the genre. Can be omitted if the genre is not yet in the system.", example = "1")
        Long genreId,

        @Schema(description = "List of artist IDs. Use an empty array `[]` if no artists are assigned yet.", example = "[1, 2]")
        List<Long> artistIds,

        @Schema(description = "ID of the album. Can be omitted if the song is not part of an album or it's not yet in the system.", example = "2")
        Long albumId
) {
}

