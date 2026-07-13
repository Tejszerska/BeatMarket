package com.spring.beatmarket.infrastructure.catalog.controller.genre.dto.response;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Summary representation of a genre")
public record GenreResponseDto(
        @Schema(description = "Genre ID", example = "1")
        Long id,

        @Schema(description = "Genre name", example = "Rock")
        String name
) {
}
