package com.spring.songify.infrastructure.controller.crud.controller.song.dto.response;

import lombok.Builder;

@Builder
public record AssignGenreToSongResponseDto(Long songId, String songTitle, Long genreId, String genreName) {
}
