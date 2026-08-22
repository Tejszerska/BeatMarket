package com.spring.beatmarket.domain.catalog;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = {ArtistMapper.class, AlbumMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface  ArtistWithAlbumMapper {
       // ArtistDto.Details mapToDto(Artist artist, Album album);
}
