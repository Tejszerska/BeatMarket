package com.spring.beatmarket.infrastructure.crud.controller.artist.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload for updating an existing artist's details")
public record ArtistUpdateRequestDto(
        @Schema(description = "New name for the artist", example = "The Offspring")
        @NotBlank(message = "name must be declared")
        String name
) {
}
