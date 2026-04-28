package com.spring.songify.infrastructure.crud.controller.genre.dto.request;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload for creating a new musical genre")
public record CreateGenreRequest(
        @Schema(description = "Name of the genre", example = "Rock")
        String name
) {
}
