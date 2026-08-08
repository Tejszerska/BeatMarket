package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import com.spring.beatmarket.domain.catalog.dto.AlbumSummaryDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface AlbumMapper {
    AlbumDto mapFromEntityToAlbumDto(Album album);

    AlbumSummaryDto mapFromEntityToSummaryDto(Album album);
}
