package com.spring.beatmarket.infrastructure.domain.catalog.controller.artist;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public interface ArtistApiDto {

    @Schema(name = "CreateArtistRequest")
    record CreateRequest(@NotBlank String name) {}

    @Schema(name = "UpdateArtistRequest")
    record UpdateRequest(@NotBlank String name) {}


    @Schema(name = "ArtistSummaryResponse")
    record SummaryResponse(Long id, String name, String imageUrl) {}

    @Schema(name = "ArtistDetailsResponse")
    record DetailsResponse(Long id, String name, String imageUrl) {}

    @Schema(name = "ArtistInfoResponse")
    record InfoResponse(Long id, String name, String imageUrl) {}

    @Schema(name = "ArtistReference")
    record Reference(Long id, String name) {}

    @Schema(name = "ArtistBasic")
    record Basic(Long id, String name, String imageUrl, Integer displayOrder) {}

    @Schema(name = "GetAllArtistsResponse")
    record GetAllResponse(
            List<SummaryResponse> artists,
            boolean hasNext
    ) {
    }
}