package com.spring.beatmarket.domain.catalog.dto;

import com.spring.beatmarket.domain.catalog.SongLanguage;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record SongRequestDto(String name, LocalDate releaseDate, Long duration, SongLanguage language) {
}
