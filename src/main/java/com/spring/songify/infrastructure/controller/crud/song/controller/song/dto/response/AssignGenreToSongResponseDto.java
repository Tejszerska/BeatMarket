package com.spring.songify.infrastructure.controller.crud.song.controller.song.dto.response;

import com.spring.songify.domain.crud.dto.GenreDto;
import com.spring.songify.domain.crud.dto.SongDto;

public record AssignGenreToSongResponseDto(SongDto song) {
}
