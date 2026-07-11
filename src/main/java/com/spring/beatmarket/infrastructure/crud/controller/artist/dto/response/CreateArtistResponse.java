package com.spring.beatmarket.infrastructure.crud.controller.artist.dto.response;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response payload after successfully creating an artist")
public record CreateArtistResponse(
        @Schema(description = "Unique ID of the new artist", example = "1") Long id,
        @Schema(description = "Name of the new artist", example = "Linkin Park") String name
) {
}
