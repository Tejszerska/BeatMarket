package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.GenreDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface GenreMapper {
    GenreDto mapFromEntityToGenreDto(Genre genre);
}
