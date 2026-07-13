package com.spring.beatmarket.infrastructure.catalog.controller.genre.dto.response;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detailed information about a specific genre")
public record GetGenreResponseDto(
        @Schema(description = "Unique ID of the genre", example = "1")
        Long id,

        @Schema(description = "Name of the genre", example = "Rock")
        String name
){
}
