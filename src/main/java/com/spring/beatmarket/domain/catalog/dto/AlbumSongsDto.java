package com.spring.beatmarket.domain.catalog.dto;

import com.spring.beatmarket.domain.catalog.dto.song.SongDto;

public record AlbumSongsDto(AlbumDto album, SongDto song) {
}
