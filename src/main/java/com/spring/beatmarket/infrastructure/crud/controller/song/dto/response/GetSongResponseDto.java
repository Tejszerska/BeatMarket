package com.spring.beatmarket.infrastructure.crud.controller.song.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detailed information about a specific song")
public record GetSongResponseDto(
        @Schema(description = "Unique ID of the song", example = "10") Long id,
        @Schema(description = "Title of the song", example = "In the End") String title,
        @Schema(description = "ID of the associated genre", example = "2") Long genreId,
        @Schema(description = "Name of the associated genre", example = "Nu Metal") String genreName
) {
}
