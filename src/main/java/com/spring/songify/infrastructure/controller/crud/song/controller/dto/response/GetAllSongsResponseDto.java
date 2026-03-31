package com.spring.songify.infrastructure.controller.crud.song.controller.dto.response;


import com.spring.songify.domain.crud.dto.SongDto;

import java.util.List;
import java.util.Set;

public record GetAllSongsResponseDto(Set<SongDto> songs) {
}
