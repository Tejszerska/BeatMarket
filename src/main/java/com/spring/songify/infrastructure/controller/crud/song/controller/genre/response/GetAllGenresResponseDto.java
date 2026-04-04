package com.spring.songify.infrastructure.controller.crud.song.controller.genre.response;

import com.spring.songify.domain.crud.dto.GenreDto;

import java.util.List;
import java.util.Set;

public record GetAllGenresResponseDto(List<GenreDto> genres, boolean hasNext) {
}
