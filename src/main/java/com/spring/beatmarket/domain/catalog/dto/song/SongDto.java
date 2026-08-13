package com.spring.beatmarket.domain.catalog.dto.song;

import com.spring.beatmarket.domain.catalog.dto.AlbumSummaryDto;
import com.spring.beatmarket.domain.catalog.dto.ArtistSummaryDto;
import com.spring.beatmarket.domain.catalog.dto.GenreDto;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record SongDto(
                      Long id,
                      String title,
                      String language,
                      LocalDate releaseDate,
                      String previewUrl,
                      List<ArtistSummaryDto> artists,
                      GenreDto genre,
                      AlbumSummaryDto album) {
}
