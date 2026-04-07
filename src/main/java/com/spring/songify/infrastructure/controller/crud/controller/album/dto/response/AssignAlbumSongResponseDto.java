package com.spring.songify.infrastructure.controller.crud.controller.album.dto.response;

import lombok.Builder;

@Builder
public record AssignAlbumSongResponseDto(Long albumId, String albumTitle, Long songId, String songTitle){
}
