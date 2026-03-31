package com.spring.songify.domain.crud.dto;

import lombok.Builder;

@Builder
public record AlbumDto(Long id, String title) {
}
