package com.spring.beatmarket.domain.catalog.dto;

import lombok.Builder;

@Builder
public record LegacyArtistDto(Long id, String name) {
}
