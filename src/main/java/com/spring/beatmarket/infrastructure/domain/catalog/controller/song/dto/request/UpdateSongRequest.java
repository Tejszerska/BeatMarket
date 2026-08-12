package com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.request;

import com.spring.beatmarket.domain.catalog.SongLanguage;
import io.swagger.v3.oas.annotations.media.Schema;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDate;
import java.util.List;

public record UpdateSongRequest(
        @Schema(description = "Title of the song", example = "In the End")
        JsonNullable<String> title,

        @Schema(description = "Release date of the song in ISO format", example = "2000-10-24")
        JsonNullable<LocalDate> releaseDate,

        @Schema(description = "Duration of the song in seconds", example = "156")
        JsonNullable<Long> duration,

        @Schema(description = "Language of the song", example = "EN")
        JsonNullable<SongLanguage> language,

        @Schema(description = "ID of the genre. Can be omitted if the genre is not yet in the system.", example = "1")
        JsonNullable<Long> genreId,

        @Schema(description = "List of artist IDs. Use an empty array `[]` if no artists are assigned yet.", example = "[1, 2]")
        JsonNullable<List<Long>> artistIds,

        @Schema(description = "ID of the album. Can be omitted if the song is not part of an album or it's not yet in the system.", example = "2")
        JsonNullable<Long> albumId
) {
    public UpdateSongRequest{
        if(title == null) title = JsonNullable.undefined();
        if(releaseDate == null) releaseDate = JsonNullable.undefined();
        if(duration == null) duration = JsonNullable.undefined();
        if(language == null) language = JsonNullable.undefined();
        if(genreId == null) genreId = JsonNullable.undefined();
        if(artistIds == null) artistIds = JsonNullable.undefined();
        if(albumId == null) albumId = JsonNullable.undefined();
    }
}
