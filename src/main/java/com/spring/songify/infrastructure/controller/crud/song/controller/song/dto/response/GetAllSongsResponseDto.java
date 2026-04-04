package com.spring.songify.infrastructure.controller.crud.song.controller.song.dto.response;


import com.spring.songify.domain.crud.dto.SongDto;

import java.util.List;
import java.util.Set;

public record GetAllSongsResponseDto(List<SongDto> songs, boolean hasNext) {
}
