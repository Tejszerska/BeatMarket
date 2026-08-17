package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.LegacyArtistDto;
import com.spring.beatmarket.domain.catalog.dto.ArtistSummaryDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.IntStream;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface ArtistMapper {
    LegacyArtistDto mapFromEntityToArtistDto(Artist artist);

    @Mapping(target = "order", source = "orderIndex")
    ArtistSummaryDto mapFromEntityToSummaryDto (Artist artist, Integer orderIndex);

    default List<ArtistSummaryDto> mapFromArtistsToSummaryList (List<Artist> artists){
        if(artists == null)  return null;

        return IntStream.range(0, artists.size())
                .mapToObj(i -> mapFromEntityToSummaryDto(artists.get(i), i))
                .toList();
    }

}
