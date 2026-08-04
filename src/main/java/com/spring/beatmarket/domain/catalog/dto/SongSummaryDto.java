package com.spring.beatmarket.domain.catalog.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
@Builder
public record SongSummaryDto(Long id,
                             String title,
                             List<String> artists,
                             String genre,
                             String previewUrl,
                             String album,
                             String language,
                             LocalDate releaseDate,
                             Long duration,
                             Map<String, PriceWithCurrencyDto> pricing) {
}
