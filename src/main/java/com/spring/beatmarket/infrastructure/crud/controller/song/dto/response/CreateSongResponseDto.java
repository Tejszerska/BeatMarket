package com.spring.beatmarket.infrastructure.crud.controller.song.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response payload after successfully creating a song")
public record CreateSongResponseDto(
        @Schema(description = "Unique ID of the new song", example = "10") Long id,
        @Schema(description = "Title of the new song", example = "In the End") String title
) {
}

