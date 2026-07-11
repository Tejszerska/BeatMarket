package com.spring.beatmarket.domain.crud;

import com.spring.beatmarket.domain.crud.dto.ArtistDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface ArtistMapper {
    ArtistDto mapFromEntityToArtistDto(Artist artist);
}
