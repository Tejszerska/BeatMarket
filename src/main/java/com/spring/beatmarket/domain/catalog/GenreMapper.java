package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.GenreDto;
import com.spring.beatmarket.domain.catalog.dto.LegacyGenreDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface GenreMapper {
    LegacyGenreDto legacyToDto(Genre genre);
    GenreDto.Info toInfoDto(Genre genre);
    GenreDto.Summary toSummary(Genre genre);
    GenreDto.Details toDetails(Genre genre);

}
