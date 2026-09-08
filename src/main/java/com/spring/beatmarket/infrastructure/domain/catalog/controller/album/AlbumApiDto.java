package com.spring.beatmarket.infrastructure.domain.catalog.controller.album;

import com.spring.beatmarket.infrastructure.domain.catalog.controller.artist.ArtistApiDto;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.SongApiDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface AlbumApiDto {
    @Schema(name = "CreateAlbumRequest")
    record CreateRequest(
            @NotBlank String title,
            @NotNull LocalDate releaseDate,
            Set<Long> songIds,
            Long mainArtistId,
            Set<Long> featuredArtistsIds
    ) {}

    @Schema(name = "UpdateAlbumRequest")
    record UpdateRequest(
            LocalDate releaseDate,
            List<Long> artistIds
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
