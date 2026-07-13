package com.spring.beatmarket.domain.catalog.dto;

import lombok.Builder;

import java.time.Instant;
@Builder
public record AlbumRequestDto(Long songId, String title, Instant releaseDate) {
}
