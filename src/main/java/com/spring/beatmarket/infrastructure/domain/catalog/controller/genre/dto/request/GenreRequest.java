package com.spring.beatmarket.infrastructure.domain.catalog.controller.genre.dto.request;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload for creating a new musical genre")
public record GenreRequest(
        @Schema(description = "Name of the genre", example = "Rock")
        @Size(min=3, max=255, message = "Genre name must be between 3 and 255 characters long.")
        String name
) {
}
