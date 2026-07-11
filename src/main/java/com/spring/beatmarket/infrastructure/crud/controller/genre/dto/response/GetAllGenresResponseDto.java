package com.spring.beatmarket.infrastructure.crud.controller.genre.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Paginated list of musical genres")
public record GetAllGenresResponseDto(
        @Schema(description = "List of genres for the current page")
        List<GenreResponseDto> genres,

        @Schema(description = "Indicates if there is a next page of genres available", example = "false")
        boolean hasNext
) {
}
