package com.spring.songify.domain.crud.dto;

import lombok.Builder;

@Builder
public record SongDto(Long id,
                      String title,
                      GenreDto genre) {
}
