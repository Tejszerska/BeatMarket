package com.spring.songify.infrastructure.crud.controller.song.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Summary representation of a song update/response")
public record UpdateSongResponseDto(
        @Schema(description = "Song ID", example = "10") Long id,
        @Schema(description = "Song title", example = "In the End") String title
) {
}
