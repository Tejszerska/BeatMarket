package com.spring.beatmarket.domain.catalog.dto;

import lombok.Builder;

@Builder
public record SongDtoOld(Long id,
                         String title,
                         GenreDto genre) {
}
