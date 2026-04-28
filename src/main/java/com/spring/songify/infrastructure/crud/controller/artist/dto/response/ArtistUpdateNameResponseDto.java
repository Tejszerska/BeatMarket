package com.spring.songify.infrastructure.crud.controller.artist.dto.response;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response payload after updating an artist's name")
public record ArtistUpdateNameResponseDto(
        @Schema(description = "Artist's ID", example = "1") Long id,
        @Schema(description = "Updated name of the artist", example = "The Offspring") String name
) {
}