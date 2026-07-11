package com.spring.beatmarket.infrastructure.crud.controller.album.dto.response;

import com.spring.beatmarket.domain.crud.dto.AlbumDto;

import java.util.List;

public record GetAllAlbumsResponseDto(List<AlbumDto> albums, boolean hasNext) {
}
