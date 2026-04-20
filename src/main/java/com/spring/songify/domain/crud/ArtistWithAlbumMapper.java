package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.ArtistWithAlbumDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = {ArtistMapper.class, AlbumMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface  ArtistWithAlbumMapper {
        ArtistWithAlbumDto mapToDto(Artist artist, Album album);
}
