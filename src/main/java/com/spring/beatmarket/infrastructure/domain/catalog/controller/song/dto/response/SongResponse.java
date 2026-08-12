package com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spring.beatmarket.domain.catalog.dto.AlbumSummaryDto;
import com.spring.beatmarket.domain.catalog.dto.ArtistSummaryDto;
import com.spring.beatmarket.domain.catalog.dto.GenreDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Detailed information about a specific song")

public record SongResponse(
        @Schema(description = "Unique ID of the song", example = "10") Long id,
        @Schema(description = "Title of the song", example = "In the End") String title,
        @Schema(description = "Language of the song in two letter format, except instrumental being NONE", example = "EN")
        String language,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @Schema(description = "Release date of the song in ISO-8601 format (YYYY-MM-DD)", example = "2026-08-09")
        LocalDate releaseDate,
        @Schema(description = "Link to an audio preview file ", example = "https://s3.aws.com/your-bucket/previews/in-the-end-prv.mp3")
        String previewUrl,
        @Schema(description = "List of artists who collaborated for the song. Contains their ids, names and order in the track",
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
        List<ArtistSummaryDto> artists,
        @Schema(description = "Genre of the song",
                example = "{\"id\": 1, \"name\": \"Rock\"}" )
        GenreDto genre,
        @Schema(description = "Album that is connected to the song",
                example = """
                        {
                            "id": 7,
                            "title": "Something",
                            "coverUrl": "https://s3.aws.com/your-bucket/images/something-cover.jpg"
                          }
                        """)
        AlbumSummaryDto album
) {
}
