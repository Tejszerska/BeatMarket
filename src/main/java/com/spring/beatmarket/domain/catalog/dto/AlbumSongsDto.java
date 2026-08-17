package com.spring.beatmarket.domain.catalog.dto;

import com.spring.beatmarket.domain.catalog.dto.song.LegacySongDto;

public record AlbumSongsDto(LegacyAlbumDto album, LegacySongDto song) {
}
