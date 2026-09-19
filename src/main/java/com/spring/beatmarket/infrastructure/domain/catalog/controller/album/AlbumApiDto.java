package com.spring.beatmarket.infrastructure.domain.catalog.controller.album;

import com.fasterxml.jackson.annotation.JsonFormat;
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

            @Schema(description = "List of song IDs. Use an empty array `[]` if no songs are assigned.", example = "[4, 5]")
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

            @Schema(description = "List of song IDs. Use an empty array `[]` to clear the list completely.", example = "[4, 5]")
            JsonNullable<Set<Long>> songIds,

            @Schema(description = "ID of the main artist. Null omits update, empty clears.", example = "1")
            JsonNullable<Long> mainArtistId,

            @Schema(description = "List of featured artist IDs. Null omits update, empty list `[]` clears.", example = "[2, 3]")
            JsonNullable<List<Long>> featArtistsIds
    ) {
        public UpdateRequest {
            if (title == null) title = JsonNullable.undefined();
            if (releaseDate == null) releaseDate = JsonNullable.undefined();
            if (songIds == null) songIds = JsonNullable.undefined();
            if (mainArtistId == null) mainArtistId = JsonNullable.undefined();
            if (featArtistsIds == null) featArtistsIds = JsonNullable.undefined();
        }
    }

    @Schema(name = "AlbumDetailsResponse")
    record DetailsResponse(
            @Schema(description = "Unique ID of the album", example = "5")
            Long id,

            @Schema(description = "Title of the album", example = "Passivity madness")
            String title,

            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            @Schema(description = "Release date in YYYY-MM-DD format", example = "2010-10-10")
            LocalDate releaseDate,

            @Schema(description = "Link to the album cover image", example = "https://s3.aws.com/your-bucket/covers/passivity-madness.jpg")
            String coverUrl,

            @Schema(description = "List of artists who collaborated on the album")
            List<ArtistApiDto.Reference> artists,

            @Schema(description = "Set of songs included in the album")
            Set<SongApiDto.Reference> songs
    ) {}

    @Schema(name = "AlbumSummaryResponse")
    record SummaryResponse(
            @Schema(description = "Unique ID of the album", example = "5")
            Long id,

            @Schema(description = "Title of the album", example = "Passivity madness")
            String title,

            @Schema(description = "Link to the album cover image", example = "https://s3.aws.com/your-bucket/covers/passivity-madness.jpg")
            String coverUrl,

            @Schema(description = "List of artists who collaborated on the album")
            List<ArtistApiDto.Reference> artists
    ) {}

    @Schema(name = "GetAllAlbumsResponse")
    record GetAllResponse(
            @Schema(description = "List of albums for the current page")
            List<SummaryResponse> albums,

            @Schema(description = "Indicates if there is a next page of albums available", example = "true")
            boolean hasNext
    ) {}

    @Schema(name = "AlbumReference")
    record Reference(
            @Schema(description = "Unique ID of the album", example = "5")
            Long id,

            @Schema(description = "Title of the album", example = "Passivity madness")
            String title
    ) {}

    @Schema(name = "AlbumBasic")
    record Basic(
            @Schema(description = "Unique ID of the album", example = "5")
            Long id,

            @Schema(description = "Title of the album", example = "Passivity madness")
            String title,

            @Schema(description = "Link to the album cover image", example = "https://s3.aws.com/your-bucket/covers/passivity-madness.jpg")
            String coverUrl
    ) {}

    @Schema(name = "AlbumInfo")
    record InfoResponse(
            @Schema(description = "Unique ID of the album", example = "5")
            Long id,

            @Schema(description = "Title of the album", example = "Passivity madness")
            String title,

            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            @Schema(description = "Release date in YYYY-MM-DD format", example = "2010-10-10")
            LocalDate releaseDate,

            @Schema(description = "Link to the album cover image", example = "https://s3.aws.com/your-bucket/covers/passivity-madness.jpg")
            String coverUrl,

            @Schema(description = "List of artists who collaborated on the album")
            List<ArtistApiDto.Reference> artists,

            @Schema(description = "Set of songs included in the album")
            Set<SongApiDto.Reference> songs
    ) {}
}