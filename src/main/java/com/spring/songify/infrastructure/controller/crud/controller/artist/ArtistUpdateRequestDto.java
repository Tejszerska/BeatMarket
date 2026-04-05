package com.spring.songify.infrastructure.controller.crud.controller.artist;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

record ArtistUpdateRequestDto(
        @NotNull(message = "newArtistName must not be null")
        @NotEmpty(message = "newArtistName must not be empty")
        String newArtistName
) {
}
