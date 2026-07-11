package com.spring.beatmarket.infrastructure.crud.controller.artist.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload for creating a new artist")
public record CreateArtistRequest(
        @Schema(description = "Official name or pseudonym of the artist", example = "Linkin Park")
        @NotBlank(message = "name must be declared")
        String name) {
}
