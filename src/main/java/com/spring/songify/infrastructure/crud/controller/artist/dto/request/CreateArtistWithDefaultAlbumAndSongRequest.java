package com.spring.songify.infrastructure.crud.controller.artist.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateArtistWithDefaultAlbumAndSongRequest(
        @NotBlank(message = "name must be declared")
        String name) {
}
