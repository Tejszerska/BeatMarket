package com.spring.beatmarket.infrastructure.domain.catalog.controller.artist;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public interface ArtistApiDto {
    // POST **/artists
    @Schema(name = "CreateArtistRequest")
    record CreateRequest(
            @NotBlank String name,
            List<Long> songIds,
            List<Long> albumIds
    ) {}

    // GET **/artists
    @Schema(name = "ArtistSummaryResponse")
    record Summary(Long id, String name, String imageUrl) {}

    // GET **/artists/{id}
    @Schema(name = "ArtistDetailsResponse")
    record Details(Long id, String name, String imageUrl
                   //  , List<AlbumDtoOld.Reference> albums,
                   //  List<SongDtoOld.Reference> songs
    ) {}

    // for nesting - the smallest
    @Schema(name = "ArtistReference")
    record Reference(Long id, String name) {}


    // for nesting in detailed records (eg. SongDetailsDto)
    @Schema(name = "ArtistBasic")
    record Basic(Long id, String name, String imageUrl, Integer displayOrder) {}
}
