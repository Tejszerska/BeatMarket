package com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SongSearchRequestDto(String genre,
                                   String artist,
                                   String language,
                                   String album,
                                   Integer minDuration,
                                   Integer maxDuration,
                                   LocalDate releaseDate,
                                   BigDecimal maxPrice,
                                   String currency,
                                   String license
                                 ) {
}
