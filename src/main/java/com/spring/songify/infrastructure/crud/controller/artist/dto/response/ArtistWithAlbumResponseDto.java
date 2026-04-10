package com.spring.songify.infrastructure.crud.controller.artist.dto.response;

import lombok.Builder;

@Builder
public record ArtistWithAlbumResponseDto(Long artistId, String artistName, Long albumId, String albumTitle) {
}
