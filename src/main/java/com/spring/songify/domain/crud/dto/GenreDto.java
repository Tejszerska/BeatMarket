package com.spring.songify.domain.crud.dto;

import lombok.Builder;

@Builder
public record GenreDto(Long id, String name) {
}
