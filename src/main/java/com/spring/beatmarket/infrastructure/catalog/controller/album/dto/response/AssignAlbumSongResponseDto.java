package com.spring.beatmarket.infrastructure.catalog.controller.album.dto.response;

import lombok.Builder;

@Builder
public record AssignAlbumSongResponseDto(Long albumId, String albumTitle, Long songId, String songTitle){
}
