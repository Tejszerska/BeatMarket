package com.spring.songify.infrastructure.controller.crud.song.controller.album;

import com.spring.songify.domain.crud.dto.AlbumDto;

import java.util.Set;

public record AllAlbumDto(Set<AlbumDto> albums) {
}
