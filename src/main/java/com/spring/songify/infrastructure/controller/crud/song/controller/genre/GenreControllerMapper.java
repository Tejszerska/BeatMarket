package com.spring.songify.infrastructure.controller.crud.song.controller.genre;

import com.spring.songify.domain.crud.dto.GenreDto;
import com.spring.songify.infrastructure.controller.crud.song.controller.genre.dto.response.GetAllGenresResponseDto;
import org.springframework.data.domain.Slice;

import java.util.List;

class GenreControllerMapper {
    static GetAllGenresResponseDto mapSliceToGetAllGenresResponseDto (Slice<GenreDto> slice){
        List<GenreDto> content = slice.getContent();
        boolean hasNext = slice.hasNext();
        return new GetAllGenresResponseDto(content, hasNext);
    }
}
