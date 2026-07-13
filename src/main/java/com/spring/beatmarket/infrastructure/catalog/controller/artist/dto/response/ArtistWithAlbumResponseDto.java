package com.spring.beatmarket.infrastructure.catalog.controller.artist.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Response detailing the link between an artist and an album")
public record ArtistWithAlbumResponseDto(
        @Schema(description = "ID of the artist", example = "1") Long artistId,
        @Schema(description = "Name of the artist", example = "Linkin Park") String artistName,
        @Schema(description = "ID of the assigned album", example = "2") Long albumId,
        @Schema(description = "Title of the assigned album", example = "Meteora") String albumTitle
) {
}