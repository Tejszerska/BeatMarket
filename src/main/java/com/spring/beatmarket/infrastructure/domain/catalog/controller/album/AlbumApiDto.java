package com.spring.beatmarket.infrastructure.domain.catalog.controller.album;

import com.spring.beatmarket.infrastructure.domain.catalog.controller.artist.ArtistApiDto;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.SongApiDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public interface AlbumApiDto {
    @Schema(name = "CreateAlbumRequest")
    record CreateRequest(
            @NotBlank String title,
            @NotNull LocalDate releaseDate,
            List<Long> songIds,
            List<Long> artistIds
    ) {}

    @Schema(name = "UpdateAlbumRequest")
    record UpdateRequest(
            LocalDate releaseDate,
            List<Long> artistIds
    ) {}

    @Schema(name = "AlbumDetailsResponse") // Dla GET /albums/{id}
    record DetailsResponse(
            Long id,
            String title,
            LocalDate releaseDate,
            String coverUrl,
            ArtistApiDto.Reference artist,
            List<SongApiDto.Reference> songs
    ) {}

    @Schema(name = "AlbumSummaryResponse") // Dla GET /albums
    record SummaryResponse(
            Long id,
            String title,
            LocalDate releaseDate,
            String coverUrl,
            ArtistApiDto.Reference artist
    ) {}

    @Schema(name = "GetAllAlbumsResponse")
    record GetAllResponse(
            List<SummaryResponse> albums,
            boolean hasNext
    ) {}

    @Schema(name = "AlbumReference") // Najlżejszy (tylko ID i tytuł)
    record Reference(
            Long id,
            String title
    ) {}

    @Schema(name = "AlbumBasic") // Do zagnieżdżenia w szczegółach Artysty
    record Basic(
            Long id,
            String title,
            String coverUrl
    ) {}
}
