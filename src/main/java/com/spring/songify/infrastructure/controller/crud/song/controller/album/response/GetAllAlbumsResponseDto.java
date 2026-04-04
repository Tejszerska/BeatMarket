package com.spring.songify.infrastructure.controller.crud.song.controller.album.response;

import com.spring.songify.domain.crud.dto.AlbumDto;

import java.util.List;

public record GetAllAlbumsResponseDto(List<AlbumDto> albums, boolean hasNext) {
}
