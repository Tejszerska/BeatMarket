package com.spring.songify.infrastructure.crud.controller.artist.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload for rapidly creating an artist with default setup")
public record CreateArtistWithDefaultAlbumAndSongRequest(
        @Schema(description = "Name of the new artist", example = "Eminem")
        @NotBlank(message = "name must be declared")
        String name) {
}
