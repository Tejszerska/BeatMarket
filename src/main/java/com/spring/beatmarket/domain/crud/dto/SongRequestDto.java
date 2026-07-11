package com.spring.beatmarket.domain.crud.dto;

import com.spring.beatmarket.domain.crud.SongLanguage;
import lombok.Builder;

import java.time.Instant;
@Builder
public record SongRequestDto(String name, Instant releaseDate, Long duration, SongLanguage language) {
}
