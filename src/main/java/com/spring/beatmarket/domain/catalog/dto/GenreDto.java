package com.spring.beatmarket.domain.catalog.dto;

import lombok.Builder;

@Builder
public record GenreDto(Long id, String name) {
}
