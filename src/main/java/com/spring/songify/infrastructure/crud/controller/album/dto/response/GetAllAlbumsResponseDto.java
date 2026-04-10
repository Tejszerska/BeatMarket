package com.spring.songify.infrastructure.crud.controller.album.dto.response;

import com.spring.songify.domain.crud.dto.AlbumDto;

import java.util.List;

public record GetAllAlbumsResponseDto(List<AlbumDto> albums, boolean hasNext) {
}
