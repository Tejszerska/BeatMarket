package com.spring.beatmarket.infrastructure.catalog.controller.genre.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response payload after successfully creating a genre")
public record CreateGenreResponse(
        @Schema(description = "Unique ID of the new genre", example = "1")
        Long id,

        @Schema(description = "Name of the new genre", example = "Rock")
        String name
) {
}
