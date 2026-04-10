package com.spring.songify.infrastructure.crud.controller.artist.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateArtistRequest(
        @NotBlank(message = "name must be declared")
        String name) {
}
