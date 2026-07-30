package com.spring.beatmarket.infrastructure.domain.catalog.controller.album.dto.response;

import com.spring.beatmarket.domain.catalog.dto.AlbumDto;

import java.util.List;

public record GetAllAlbumsResponseDto(List<AlbumDto> albums, boolean hasNext) {
}
