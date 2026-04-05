package com.spring.songify.infrastructure.controller.crud.controller.genre.dto.response;

import java.util.List;

public record GetAllGenresResponseDto(List<GenreResponseDto> genres, boolean hasNext) {
}
