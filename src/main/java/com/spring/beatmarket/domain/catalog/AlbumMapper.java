package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import com.spring.beatmarket.domain.catalog.dto.LegacyAlbumDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring",
        uses = {ArtistMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface AlbumMapper {
    LegacyAlbumDto mapFromEntityToAlbumDto(Album album);

    @Named("standardSummary")
    AlbumDto.Summary toSummaryDto(Album album);

    @Named("activeSummary")
    default AlbumDto.Summary toActiveSummaryDto(Album album) {
        if (album == null || !album.isActive()) {
            return null;
        }
        return toSummaryDto(album);
    }

    default AlbumDto.Reference toActiveReferenceDto(Album album) {
        if (album == null || !album.isActive()) {
            return null;
        }
        return new AlbumDto.Reference(album.getId(), album.getTitle());
    }
}
