package com.spring.songify.domain.crud.dto;

import com.spring.songify.domain.crud.SongLanguage;
import lombok.Builder;

import java.time.Instant;
@Builder
public record SongRequestDto(String name, Instant releaseDate, Long duration, SongLanguage language) {
}
