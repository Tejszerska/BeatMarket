package com.spring.songify.infrastructure.controller.crud.song.controller.genre.dto.response;

import com.spring.songify.domain.crud.dto.GenreDto;

import java.util.List;

public record GetAllGenresResponseDto(List<GenreDto> genres, boolean hasNext) {
}
