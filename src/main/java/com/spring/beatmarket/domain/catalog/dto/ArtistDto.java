package com.spring.beatmarket.domain.catalog.dto;

import lombok.Builder;

@Builder
public record ArtistDto(Long id, String name) {
}
