package com.spring.songify.domain.crud.dto;

import java.time.Instant;

public record AlbumRequestDto(Long songId, String title, Instant releaseDate) {
}
