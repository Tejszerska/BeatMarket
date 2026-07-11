package com.spring.beatmarket.domain.crud;

import com.spring.beatmarket.domain.crud.dto.AlbumDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface AlbumMapper {
    AlbumDto mapFromEntityToAlbumDto(Album album);
}
