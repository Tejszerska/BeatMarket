package com.spring.beatmarket.infrastructure.domain.catalog.controller.artist;

import com.spring.beatmarket.infrastructure.domain.catalog.controller.album.AlbumApiDto;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.SongApiDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

public interface ArtistApiDto {

    @Schema(name = "CreateArtistRequest")
    record CreateRequest(
            @Schema(description = "Official name or pseudonym of the artist", example = "Linkin Park")
            @NotBlank(message = "name must be declared")
            String name,

            @Schema(description = "List of song IDs where the artist is the main performer. Use an empty array `[]` if no songs are assigned yet.", example = "[1, 2]")
            List<Long> mainSongIds,

            @Schema(description = "List of song IDs where the artist is a featured performer. Use an empty array `[]` if no songs are assigned yet.", example = "[7]")
            List<Long> featSongIds,

            @Schema(description = "List of album IDs where the artist is the primary creator. Use an empty array `[]` if no albums are assigned yet.", example = "[1]")
            List<Long> mainAlbumIds,

            @Schema(description = "List of album IDs where the artist is a featured/collaborating creator. Use an empty array `[]` if no albums are assigned yet.", example = "[]")
            List<Long> featAlbumIds
    ) {
    }

    @Schema(name = "UpdateArtistRequest")
    record UpdateRequest(
            @NotBlank JsonNullable<String> name,
            @Schema(description = "List of song IDs where the artist is the main performer. Use an empty array `[]` to clear the list completely.", example = "[1, 2]")
            JsonNullable<List<Long>> mainSongIds,

            @Schema(description = "List of song IDs where the artist is a featured performer. Use an empty array `[]` to clear the list completely.", example = "[7]")
            JsonNullable<List<Long>> featSongIds,

            @Schema(description = "List of album IDs where the artist is the main creator. Use an empty array `[]` to clear the list completely.", example = "[1]")
            JsonNullable<List<Long>> mainAlbumIds,

            @Schema(description = "List of album IDs where the artist is a featured creator. Use an empty array `[]` to clear the list completely.", example = "[]")
            JsonNullable<List<Long>> featAlbumIds
    ) {
    }


    @Schema(name = "ArtistSummaryResponse")
    record SummaryResponse(Long id, String name, String imageUrl) {
    }

    @Schema(name = "ArtistDetailsResponse")
    record DetailsResponse(Long id,
                           String name,
                           String imageUrl,
                           List<SongApiDto.Reference> songs,
                           List<AlbumApiDto.Reference> albums) {
    }

    @Schema(name = "ArtistInfoResponse")
    record InfoResponse(Long id,
                        String name,
                        List<SongApiDto.Reference> songs,
                        List<AlbumApiDto.Reference> albums
    ) {
    }

    @Schema(name = "ArtistReference")
    record Reference(Long id, String name) {
    }

    @Schema(name = "ArtistBasic")
    record Basic(Long id, String name, String imageUrl, Integer displayOrder) {
    }

    @Schema(name = "GetAllArtistsResponse")
    record GetAllResponse(
            List<SummaryResponse> artists,
            boolean hasNext
    ) {
    }
}