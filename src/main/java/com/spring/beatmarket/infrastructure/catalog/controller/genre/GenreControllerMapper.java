package com.spring.beatmarket.infrastructure.catalog.controller.genre;

import com.spring.beatmarket.domain.catalog.dto.GenreDto;
import com.spring.beatmarket.domain.catalog.dto.GenreRequestDto;
import com.spring.beatmarket.infrastructure.catalog.controller.genre.dto.request.CreateGenreRequest;
import com.spring.beatmarket.infrastructure.catalog.controller.genre.dto.response.CreateGenreResponse;
import com.spring.beatmarket.infrastructure.catalog.controller.genre.dto.response.GenreResponseDto;
import com.spring.beatmarket.infrastructure.catalog.controller.genre.dto.response.GetAllGenresResponseDto;
import com.spring.beatmarket.infrastructure.catalog.controller.genre.dto.response.GetGenreResponseDto;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Slice;

import java.util.List;

@Mapper(componentModel = "spring")
interface GenreControllerMapper {

    CreateGenreResponse mapFromGenreDtoToCreateGenreResponse(GenreDto genreDto);

    GetGenreResponseDto mapGenreDtoToGetGenreResponseDto (GenreDto genreDto);

    GenreRequestDto mapFromCreateGenreRequestDtoToDomainRequest(CreateGenreRequest createGenreRequest);

    GenreResponseDto mapToGenreResponseDto(GenreDto genreDto);
    List<GenreResponseDto> mapToListOfGenreResponseDto(List<GenreDto> list);
    default GetAllGenresResponseDto mapSliceToGetAllGenresResponseDto (Slice<GenreDto> slice) {
        return new GetAllGenresResponseDto(mapToListOfGenreResponseDto(slice.getContent()), slice.hasNext());
    }


}
