package com.spring.beatmarket.domain.catalog.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AlbumRequestDto(Long songId, String title, LocalDate releaseDate) {
}
