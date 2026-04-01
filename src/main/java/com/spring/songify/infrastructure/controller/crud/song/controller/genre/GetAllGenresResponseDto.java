package com.spring.songify.infrastructure.controller.crud.song.controller.genre;

import com.spring.songify.domain.crud.dto.GenreDto;

import java.util.Set;

public record GetAllGenresResponseDto(Set<GenreDto> genres) {
}
