package com.spring.beatmarket.infrastructure.domain.catalog.controller.song;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spring.beatmarket.domain.catalog.SongLanguage;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.album.AlbumApiDto;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.artist.ArtistApiDto;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.genre.GenreApiDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import org.openapitools.jackson.nullable.JsonNullable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SongApiDto {
    @Schema(name = "CreateSongRequest")
    record CreateRequest(
            @Schema(description = "Title of the song", example = "In the End")
            @NotBlank(message = "title must be declared")
            String title,

            @Schema(description = "Release date of the song in ISO format", example = "2000-10-24")
            @NotNull(message = "releaseDate must be declared")
            @PastOrPresent(message = "releaseDate cannot be in the future")
            LocalDate releaseDate,

            @Schema(description = "Duration of the song in seconds", example = "156")
            @NotNull(message = "duration must be declared")
            @Positive(message = "duration must be a positive number")
            Integer duration,

            @Schema(description = "Language of the song", example = "EN")
            @NotNull(message = "language must be declared")
            SongLanguage language,

            @Schema(description = "ID of the genre. Can be omitted if the genre is not yet in the system.", example = "1")
            Long genreId,

            @Schema(description = "List of artist IDs. Use an empty array `[]` if no artists are assigned yet.", example = "[1, 2]")
            List<Long> artistIds,

            @Schema(description = "ID of the album. Can be omitted if the song is not part of an album or it's not yet in the system.", example = "2")
            Long albumId
    ) {
    }

    @Schema(name = "UpdateSongRequest")
    record UpdateRequest(
            @Schema(description = "Title of the song", example = "In the End")
            JsonNullable<String> title,

            @Schema(description = "Release date of the song in ISO format", example = "2000-10-24")
            JsonNullable<LocalDate> releaseDate,

            @Schema(description = "Duration of the song in seconds", example = "156")
            JsonNullable<Integer> duration,

            @Schema(description = "Language of the song", example = "EN")
            JsonNullable<SongLanguage> language,

            @Schema(description = "ID of the genre. Can be omitted if the genre is not yet in the system.", example = "1")
            JsonNullable<Long> genreId,

            @Schema(description = "List of artist IDs. Use an empty array `[]` if no artists are assigned yet.", example = "[1, 2]")
            JsonNullable<List<Long>> artistIds,

            @Schema(description = "ID of the album. Can be omitted if the song is not part of an album or it's not yet in the system.", example = "2")
            JsonNullable<Long> albumId
    ) {
        public UpdateRequest {
            if (title == null) title = JsonNullable.undefined();
            if (releaseDate == null) releaseDate = JsonNullable.undefined();
            if (duration == null) duration = JsonNullable.undefined();
            if (language == null) language = JsonNullable.undefined();
            if (genreId == null) genreId = JsonNullable.undefined();
            if (artistIds == null) artistIds = JsonNullable.undefined();
            if (albumId == null) albumId = JsonNullable.undefined();
        }
    }

    //  GET **/songs/{id}
    @Schema(name = "SongDetailsResponse")
    record DetailsResponse(
            @Schema(description = "Unique ID of the song", example = "10") Long id,
            @Schema(description = "Title of the song", example = "In the End") String title,
            @Schema(description = "Language of the song in two letter format, except instrumental being NONE", example = "EN")
            String language,
            @Schema(description = "Song duration", example = "250") Integer duration,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            @Schema(description = "Release date of the song in ISO-8601 format (YYYY-MM-DD)", example = "2026-08-09")
            LocalDate releaseDate,
            @Schema(description = "Link to an audio preview file ", example = "https://s3.aws.com/your-bucket/previews/in-the-end-prv.mp3")
            String previewUrl,
            @Schema(description = "List of artists who collaborated for the song. Contains their ids, names, url of avatars and order in the track",
                    example = """
                            [
                              {
                                "id": 2,
                                "name": "U2",
                                "imageUrl": "https://s3.aws.com/your-bucket/images/u2-profile.jpg",
                                "displayOrder": 1
                              },
                              {
                                "id": 8,
                                "name": "Coldplay",
                                "imageUrl": "https://s3.aws.com/your-bucket/images/coldplay-profile.jpg",
                                "displayOrder": 2
                              }
                            ]
                            """)
            List<ArtistApiDto.Basic> artists,
            @Schema(description = "Genre of the song",
                    example = "{\"id\": 1, \"name\": \"Rock\"}")
            GenreApiDto.Reference genre,
            @Schema(description = "Album that is connected to the song",
                    example = """
                            {
                                "id": 7,
                                "title": "Something",
                                "coverUrl": "https://s3.aws.com/your-bucket/images/something-cover.jpg"
                              }
                            """)
            AlbumApiDto.Basic album,
            @Schema(description = "Song pricing tiers",
                    example = """
                            {
                              "STANDARD": {
                                  "price": 15.99,
                                  "currency": "USD"
                                    },
                               "UNLIMITED": {
                                  "price": 199.99,
                                  "currency": "USD"
                                    }
                                  }
                            """
            )
            Map<String, Price> pricing
    ) {
    }

    // GET **/songs
    @Schema(name = "SongSummaryResponse")
    record SummaryResponse(
            @Schema(description = "Song ID", example = "10") Long id,
            @Schema(description = "Song title", example = "Shadow Realm") String title,
            @Schema(description = "List of artists who collaborated on the track",
                    example = """
                            [
                              {
                                "id": 2,
                                "name": "U2"
                              },
                              {
                                "id": 8,
                                "name": "Coldplay"
                              }
                            ]
                            """) List<ArtistApiDto.Reference> artists,
            @Schema(description = "Genre of the song",
                    example = "{\"id\": 1, \"name\": \"Rock\"}")
            GenreApiDto.Reference genre,
            @Schema(description = "Url of tracks preview", example = "https://some.link.com/audio.mp3") String previewUrl,
            @Schema(description = "Album with id and title", example = "{\"id\": 1, \"title\": \"Serenity Wraps\"}") AlbumApiDto.Reference album,
            @Schema(description = "Song language", example = "EN") String language,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            @Schema(description = "Release date in YYYY-MM-DD format", example = "2000-10-24") LocalDate releaseDate,
            @Schema(description = "Song duration", example = "250") Integer duration,
            @Schema(description = "Song pricing tiers",
                    example = """
                            {
                              "STANDARD": {
                                  "price": 15.99,
                                  "currency": "USD"
                                    },
                               "UNLIMITED": {
                                  "price": 199.99,
                                  "currency": "USD"
                                    }
                                  }
                            """
            )
            Map<String, Price> pricing
    ) {
    }

    @Schema(name = "GetAllSongsResponse")
    record GetAllResponse(
            @Schema(description = "List of songs for the current page") List<SummaryResponse> songs,
            @Schema(description = "Indicates if there is a next page of songs available", example = "true") boolean hasNext
    ) {
    }

    // for nesting eg. AlbumDetails
    @Schema(name = "SongReference")
    record Reference(
            Long id,
            String title
    ) {
    }

    // for nesting eg. AlbumDetails
    @Schema(name = "SongReference")
    record Basic(
            Long id,
            String title,
            Integer duration
    ) {
    }

    @Schema(name = "SongInfo")
    record InfoResponse(Long id,
                        String title,
                        Integer duration,
                        String language,
                        LocalDate releaseDate,
                        List<ArtistApiDto.Reference> artists,
                        GenreApiDto.Reference genre,
                        AlbumApiDto.Reference album) {}

    @Schema(name = "SongPriceResponse")
    record Price(
            BigDecimal amount,
            String currency
    ) {
    }

    @Schema(name = "SongSearchRequest")
    record SearchRequest(
            String genre,
            String artist,
            String language,
            String album,
            Integer minDuration,
            Integer maxDuration,
            LocalDate releaseDate,
            BigDecimal maxPrice,
            String currency,
            String license
    ) {}
}
