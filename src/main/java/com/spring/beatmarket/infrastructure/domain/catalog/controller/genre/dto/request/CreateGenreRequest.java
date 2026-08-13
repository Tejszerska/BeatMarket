package com.spring.beatmarket.infrastructure.domain.catalog.controller.genre.dto.request;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

@Schema(description = "Payload for creating a new musical genre")
public record CreateGenreRequest(
        @Schema(description = "Name of the genre", example = "Rock")
        @Min(3)
        String name
) {
}
