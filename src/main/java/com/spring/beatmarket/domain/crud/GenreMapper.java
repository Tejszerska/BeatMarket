package com.spring.beatmarket.domain.crud;

import com.spring.beatmarket.domain.crud.dto.GenreDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface GenreMapper {
    GenreDto mapFromEntityToGenreDto(Genre genre);
}
