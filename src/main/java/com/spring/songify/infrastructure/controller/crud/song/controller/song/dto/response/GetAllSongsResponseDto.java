package com.spring.songify.infrastructure.controller.crud.song.controller.song.dto.response;


import com.spring.songify.domain.crud.dto.SongDto;

import java.util.List;

public record GetAllSongsResponseDto(List<SongDto> songs, boolean hasNext) {
}
