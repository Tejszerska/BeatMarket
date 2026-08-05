package com.spring.beatmarket.domain.catalog.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SongSearchCriteria(String genre,
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
