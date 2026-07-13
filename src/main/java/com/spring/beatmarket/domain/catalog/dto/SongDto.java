package com.spring.beatmarket.domain.catalog.dto;

import lombok.Builder;

@Builder
public record SongDto(Long id,
                      String title,
                      GenreDto genre) {
}
