package com.spring.beatmarket.domain.catalog.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record LegacySongDto(
                      Long id,
                      String title,
                      String language,
                      LocalDate releaseDate,
                      String previewUrl,
                      List<ArtistSummaryDto> artists,
                      LegacyGenreDto genre,
                      AlbumSummaryDto album) {
}
