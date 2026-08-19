package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import com.spring.beatmarket.domain.catalog.dto.AlbumSummaryDto;
import com.spring.beatmarket.domain.catalog.dto.LegacyAlbumDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface AlbumMapper {
    LegacyAlbumDto mapFromEntityToAlbumDto(Album album);

    AlbumSummaryDto mapFromEntityToSummaryDto(Album album);

    default AlbumDto.Summary toActiveSummaryDto(Album album) {
        if (album == null || !album.isActive()) {
            return null;
        }
        return new AlbumDto.Summary(
                album.getId(),
                album.getTitle(),
                album.getCoverUrl()
        );
    }

    default AlbumDto.Reference toActiveReferenceDto(Album album) {
        if (album == null || !album.isActive()) {
            return null;
        }
        return new AlbumDto.Reference(album.getId(), album.getTitle());
    }
}
