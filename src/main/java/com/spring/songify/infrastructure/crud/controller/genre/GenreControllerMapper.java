package com.spring.songify.infrastructure.crud.controller.genre;

import com.spring.songify.domain.crud.dto.GenreDto;
import com.spring.songify.domain.crud.dto.GenreRequestDto;
import com.spring.songify.infrastructure.crud.controller.genre.dto.request.CreateGenreRequest;
import com.spring.songify.infrastructure.crud.controller.genre.dto.response.CreateGenreResponse;
import com.spring.songify.infrastructure.crud.controller.genre.dto.response.GenreResponseDto;
import com.spring.songify.infrastructure.crud.controller.genre.dto.response.GetAllGenresResponseDto;
import com.spring.songify.infrastructure.crud.controller.genre.dto.response.GetGenreResponseDto;
import org.springframework.data.domain.Slice;

import java.util.List;

class GenreControllerMapper {
    static CreateGenreResponse mapFromGenreDtoToCreateGenreResponse(GenreDto genreDto){
    return new CreateGenreResponse(genreDto.id(), genreDto.name());
    }

    static GetGenreResponseDto mapGenreDtoToGetGenreResponseDto (GenreDto genreDto){
        return new GetGenreResponseDto(genreDto.id(), genreDto.name());
    }

    static GenreRequestDto mapFromCreateGenreRequestDtoToDomainRequest(CreateGenreRequest createGenreRequest){
        return new GenreRequestDto(createGenreRequest.name());
    }
    static GetAllGenresResponseDto mapSliceToGetAllGenresResponseDto (Slice<GenreDto> slice){
        List<GenreResponseDto> mappedContent = slice.getContent().stream()
                .map(GenreControllerMapper::mapFromDomainGenreDtoToGenreResponseDto)
                .toList();

        boolean hasNext = slice.hasNext();
        return new GetAllGenresResponseDto(mappedContent, hasNext);
    }

    static GenreResponseDto mapFromDomainGenreDtoToGenreResponseDto (GenreDto genreDto){
        return new GenreResponseDto(genreDto.id(), genreDto.name());
    }
}
