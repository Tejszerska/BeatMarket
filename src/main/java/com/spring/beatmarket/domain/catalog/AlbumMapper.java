package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumSummaryDto;
import com.spring.beatmarket.domain.catalog.dto.LegacyAlbumDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface AlbumMapper {
    LegacyAlbumDto mapFromEntityToAlbumDto(Album album);

    AlbumSummaryDto mapFromEntityToSummaryDto(Album album);
}
