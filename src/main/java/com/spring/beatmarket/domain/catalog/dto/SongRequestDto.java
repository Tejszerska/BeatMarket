package com.spring.beatmarket.domain.catalog.dto;

import com.spring.beatmarket.domain.catalog.SongLanguage;
import lombok.Builder;

import java.time.Instant;
@Builder
public record SongRequestDto(String name, Instant releaseDate, Long duration, SongLanguage language) {
}
