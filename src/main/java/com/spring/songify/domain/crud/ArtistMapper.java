package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.ArtistDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface ArtistMapper {
    ArtistDto mapFromEntityToArtistDto(Artist artist);
}
