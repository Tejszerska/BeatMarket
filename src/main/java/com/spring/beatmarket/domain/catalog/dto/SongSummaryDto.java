package com.spring.beatmarket.domain.catalog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
