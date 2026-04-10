package com.spring.songify.infrastructure.crud.controller.song.dto.response;

import java.util.List;

public record GetAllSongsResponseDto(List<SongResponseDto> songs, boolean hasNext) {
}
