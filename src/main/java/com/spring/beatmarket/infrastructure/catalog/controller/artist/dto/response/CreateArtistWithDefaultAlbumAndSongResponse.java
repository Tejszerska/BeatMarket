package com.spring.beatmarket.infrastructure.catalog.controller.artist.dto.response;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response after creating an artist with a default tracklist")
public record CreateArtistWithDefaultAlbumAndSongResponse (
        @Schema(description = "ID of the created artist", example = "5") Long id,
        @Schema(description = "Name of the created artist", example = "Eminem") String name
) {
}