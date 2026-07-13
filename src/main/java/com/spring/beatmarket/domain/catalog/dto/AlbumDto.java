package com.spring.beatmarket.domain.catalog.dto;

import lombok.Builder;

@Builder
public record AlbumDto(Long id, String title) {
}
