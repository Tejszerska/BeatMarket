package com.spring.beatmarket.domain.catalog.dto;

import lombok.Builder;

@Builder
public record LegacyGenreDto(Long id, String name) {
}
