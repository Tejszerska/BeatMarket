package com.spring.beatmarket.infrastructure.domain.catalog.controller.artist.dto.response;

import com.spring.beatmarket.domain.catalog.dto.LegacyArtistDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Paginated list of artists")
public record GetAllArtistsResponseDto(
        @Schema(description = "List of artists for the current page") List<LegacyArtistDto> artists,
        @Schema(description = "Indicates if there is a next page of artists available", example = "false") boolean hasNext
) {
}