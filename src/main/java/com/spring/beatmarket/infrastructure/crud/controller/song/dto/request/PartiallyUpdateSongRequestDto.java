package com.spring.beatmarket.infrastructure.crud.controller.song.dto.request;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload for partially updating a song")
public record PartiallyUpdateSongRequestDto(
        @Schema(description = "New title of the song", example = "In the End (Remix)")
        String title) {
}
