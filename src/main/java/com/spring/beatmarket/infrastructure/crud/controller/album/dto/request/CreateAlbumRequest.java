package com.spring.beatmarket.infrastructure.crud.controller.album.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Schema(description = "Response containing the ID and title of the newly created album")
public record CreateAlbumRequest(
        @Schema(description = "ID of the first song to be included in the album", example = "1")
        @NotNull(message = "Song ID must be declared")
        Long songId,

        @Schema(description = "Official title of the album", example = "Hybrid Theory")
        @NotBlank(message = "Title must be declared")
        String title,

        @Schema(description = "Release date in ISO format", example = "2000-10-24T00:00:00Z")
        @NotNull(message = "Release date must be declared")
        Instant releaseDate) {
}
