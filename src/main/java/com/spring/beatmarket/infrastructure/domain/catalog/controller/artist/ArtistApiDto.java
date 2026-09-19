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
            @Schema(description = "Official name or pseudonym of the artist", example = "Linkin Park")
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
        public UpdateRequest {
            if (name == null) name = JsonNullable.undefined();
            if (mainSongIds == null) mainSongIds = JsonNullable.undefined();
            if (featSongIds == null) featSongIds = JsonNullable.undefined();
            if (mainAlbumIds == null) mainAlbumIds = JsonNullable.undefined();
            if (featAlbumIds == null) featAlbumIds = JsonNullable.undefined();
        }
    }


    @Schema(name = "ArtistSummaryResponse")
    record SummaryResponse(
            @Schema(description = "Unique ID of the artist", example = "10")
            Long id,
            @Schema(description = "Official name or pseudonym of the artist", example = "Linkin Park")
            String name,
            @Schema(description = "URL to the artist's profile image", example = "https://s3.aws.com/your-bucket/artists/linkin-park.jpg")
            String imageUrl
    ) {
    }

    @Schema(name = "ArtistDetailsResponse")
    record DetailsResponse(
            @Schema(description = "Unique ID of the artist", example = "10")
            Long id,
            @Schema(description = "Official name or pseudonym of the artist", example = "Linkin Park")
            String name,
            @Schema(description = "URL to the artist's profile image", example = "https://s3.aws.com/your-bucket/artists/linkin-park.jpg")
            String imageUrl,
            @Schema(description = "List of songs associated with the artist")
            List<SongApiDto.Reference> songs,
            @Schema(description = "List of albums associated with the artist")
            List<AlbumApiDto.Reference> albums
    ) {
    }

    @Schema(name = "ArtistInfoResponse")
    record InfoResponse(
            @Schema(description = "Unique ID of the artist", example = "10")
            Long id,
            @Schema(description = "Official name or pseudonym of the artist", example = "Linkin Park")
            String name,
            @Schema(description = "List of songs associated with the artist")
            List<SongApiDto.Reference> songs,
            @Schema(description = "List of albums associated with the artist")
            List<AlbumApiDto.Reference> albums
    ) {
    }

    @Schema(name = "ArtistReference")
    record Reference(
            @Schema(description = "Unique ID of the artist", example = "10")
            Long id,
            @Schema(description = "Official name or pseudonym of the artist", example = "Linkin Park")
            String name
    ) {
    }

    @Schema(name = "ArtistBasic")
    record Basic(
            @Schema(description = "Unique ID of the artist", example = "10")
            Long id,
            @Schema(description = "Official name or pseudonym of the artist", example = "Linkin Park")
            String name,
            @Schema(description = "URL to the artist's profile image", example = "https://s3.aws.com/your-bucket/artists/linkin-park.jpg")
            String imageUrl,
            @Schema(description = "Order in which the artist should be displayed in a collaboration list", example = "1")
            Integer displayOrder
    ) {
    }

    @Schema(name = "GetAllArtistsResponse")
    record GetAllResponse(
            @Schema(description = "List of artists for the current page")
            List<SummaryResponse> artists,
            @Schema(description = "Indicates if there is a next page of artists available", example = "true")
            boolean hasNext
    ) {
    }
}