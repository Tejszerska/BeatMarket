package com.spring.songify.infrastructure.crud.controller.album.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateAlbumRequest(
        @NotNull(message = "Song ID must be declared")
        Long songId,
        @NotBlank(message = "Title must be declared")
        String title,
        @NotNull(message = "Release date must be declared")
        Instant releaseDate) {
}
