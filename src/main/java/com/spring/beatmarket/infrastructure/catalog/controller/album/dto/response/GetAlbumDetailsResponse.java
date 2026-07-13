package com.spring.beatmarket.infrastructure.catalog.controller.album.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "Detailed information about an album, including its artists and tracklist")
@Builder
public record GetAlbumDetailsResponse(
        @Schema(description = "Unique ID of the album", example = "1")
        Long id,

        @Schema(description = "Title of the album", example = "Hybrid Theory")
        String title,

        @Schema(description = "List of artists associated with this album")
        List<ArtistSummary> artists,

        @Schema(description = "List of songs included in this album")
        List<SongSummary> songs) {

    @Schema(description = "Summary of an artist")
    public record ArtistSummary(
            @Schema(description = "Artist ID", example = "1") Long id,
            @Schema(description = "Artist name", example = "Linkin Park") String name
    ){}
    @Schema(description = "Summary of a song")
    public record SongSummary(
            @Schema(description = "Song ID", example = "5") Long id,
            @Schema(description = "Song title", example = "In the End") String title
    ){}

}
