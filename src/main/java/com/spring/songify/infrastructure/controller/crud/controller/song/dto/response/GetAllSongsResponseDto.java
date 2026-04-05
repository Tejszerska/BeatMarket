package com.spring.songify.infrastructure.controller.crud.controller.song.dto.response;

import java.util.List;

public record GetAllSongsResponseDto(List<SongResponseDto> songs, boolean hasNext) {
}
