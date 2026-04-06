package com.spring.songify.infrastructure.controller.crud.controller.artist.dto.response;

import lombok.Builder;

@Builder
public record ArtistWithAlbumResponseDto(Long artistId, String artistName, Long albumId, String albumTitle) {
}
