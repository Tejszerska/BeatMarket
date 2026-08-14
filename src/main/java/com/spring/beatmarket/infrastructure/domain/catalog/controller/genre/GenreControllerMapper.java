package com.spring.beatmarket.infrastructure.domain.catalog.controller.genre;

import com.spring.beatmarket.domain.catalog.dto.SaveGenreDto;
import com.spring.beatmarket.domain.catalog.dto.GenreDto;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.genre.dto.request.GenreRequest;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.genre.dto.response.GenreResponse;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.genre.dto.response.GetAllGenresResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Slice;

import java.util.List;

@Mapper(componentModel = "spring")
interface GenreControllerMapper {

    SaveGenreDto toDomain(GenreRequest genreRequest);

    GenreResponse toResponse(GenreDto genreDto);

    List<GenreResponse> toResponse(List<GenreDto> list);

    default GetAllGenresResponse toGetAllGenresResponse(Slice<GenreDto> slice) {
        return new GetAllGenresResponse(toResponse(slice.getContent()), slice.hasNext());    }
}
