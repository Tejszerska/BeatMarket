package com.spring.beatmarket.domain.catalog.dto;

import lombok.Builder;

@Builder
public record LegacyAlbumDto(Long id, String title) {
}
