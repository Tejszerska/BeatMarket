package com.spring.beatmarket.infrastructure.domain.catalog.controller.album;

import com.spring.beatmarket.infrastructure.domain.catalog.controller.artist.ArtistApiDto;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.SongApiDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface AlbumApiDto {
    @Schema(name = "CreateAlbumRequest")
    record CreateRequest(
            @Schema(description = "Title of the album", example = "Passivity madness")
            @NotBlank(message = "title must be declared")
            String title,

            @Schema(description = "Release date of the album in ISO format", example = "2010-10-10")
            @NotNull(message = "releaseDate must be declared")
            @PastOrPresent(message = "releaseDate cannot be in the future")
            LocalDate releaseDate,

            @Schema(description = "List of song IDs. Use an empty array `[]` if no featured artists.", example = "[4, 5]")
            Set<Long> songIds,

            @Schema(description = "ID of the main artist. Can be omitted if no artist is assigned yet.", example = "1")
            Long mainArtistId,

            @Schema(description = "List of featured artist IDs. Use an empty array `[]` if no featured artists.", example = "[2, 3]")
            List<Long> featArtistsIds
    ) {}

    @Schema(name = "UpdateAlbumRequest")
    record UpdateRequest(
            @Schema(description = "Title of the album", example = "Passivity madness")
            JsonNullable<String> title,

            @Schema(description = "Release date of the album in ISO format", example = "2010-10-10")
            JsonNullable<LocalDate> releaseDate,

            @Schema(description = "List of song IDs. Use an empty array `[]` if no featured artists.", example = "[4, 5]")
            JsonNullable<Set<Long>> songIds,

            @Schema(description = "ID of the main artist. Can be omitted if no artist is assigned yet.", example = "1")
            JsonNullable<Long> mainArtistId,

            @Schema(description = "List of featured artist IDs. Use an empty array `[]` if no featured artists.", example = "[2, 3]")
            JsonNullable<List<Long>> featArtistsIds
    ) {}

    @Schema(name = "AlbumDetailsResponse")
    record DetailsResponse(
            Long id,
            String title,
            LocalDate releaseDate,
            String coverUrl,
            List<ArtistApiDto.Reference> artists,
            Set<SongApiDto.Reference> songs
    ) {}

    @Schema(name = "AlbumSummaryResponse")
    record SummaryResponse(
            Long id,
            String title,
            String coverUrl,
            List<ArtistApiDto.Reference> artists
    ) {}

    @Schema(name = "GetAllAlbumsResponse")
    record GetAllResponse(
            List<SummaryResponse> albums,
            boolean hasNext
    ) {}

    @Schema(name = "AlbumReference")
    record Reference(
            Long id,
            String title
    ) {}

    @Schema(name = "AlbumBasic")
    record Basic(
            Long id,
            String title,
            String coverUrl
    ) {}

    @Schema(name = "AlbumInfo")
    record InfoResponse(
            Long id,
            String title,
            LocalDate releaseDate,
            String coverUrl,
            List<ArtistApiDto.Reference> artists,
            Set<SongApiDto.Reference> songs
    ) {}
}
