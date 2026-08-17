package com.spring.beatmarket.infrastructure.domain.catalog.controller.album.dto.response;

import com.spring.beatmarket.domain.catalog.dto.LegacyAlbumDto;

import java.util.List;

public record GetAllAlbumsResponseDto(List<LegacyAlbumDto> albums, boolean hasNext) {
}
