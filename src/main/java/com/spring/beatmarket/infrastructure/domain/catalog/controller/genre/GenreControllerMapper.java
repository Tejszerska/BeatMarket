package com.spring.beatmarket.infrastructure.domain.catalog.controller.genre;

import com.spring.beatmarket.domain.catalog.dto.LegacyGenreDto;
import com.spring.beatmarket.domain.catalog.dto.SaveGenreDto;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.genre.dto.request.GenreRequest;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.genre.dto.response.GenreResponse;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.genre.dto.response.GetAllGenresResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Slice;

import java.util.List;

@Mapper(componentModel = "spring")
interface GenreControllerMapper {

    SaveGenreDto toDomain(GenreRequest genreRequest);

    GenreResponse toResponse(LegacyGenreDto legacyGenreDto);

    List<GenreResponse> toResponse(List<LegacyGenreDto> list);

    default GetAllGenresResponse toGetAllGenresResponse(Slice<LegacyGenreDto> slice) {
        return new GetAllGenresResponse(toResponse(slice.getContent()), slice.hasNext());    }
}
