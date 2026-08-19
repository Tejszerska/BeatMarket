package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import com.spring.beatmarket.domain.catalog.dto.LegacyArtistDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.IntStream;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface ArtistMapper {

    // --- Single mapping ---

    @Mapping(target = "displayOrder", source = "orderIndex")
    ArtistDto.Basic toBasicDto(Artist artist, Integer orderIndex);

    ArtistDto.Reference toReferenceDto(Artist artist);

    // --- Collection mapping (used by SongMapper) ---

    default List<ArtistDto.Basic> toBasicList(List<Artist> artists) {
        if (artists == null) return null;

        return IntStream.range(0, artists.size())
                .filter(i -> artists.get(i).isActive())
                .mapToObj(i -> toBasicDto(artists.get(i), i))
                .toList();
    }

    default List<ArtistDto.Reference> toReferenceList(List<Artist> artists) {
        if (artists == null) return null;

        return artists.stream()
                .filter(Artist::isActive)
                .map(this::toReferenceDto)
                .toList();
    }

    LegacyArtistDto mapFromEntityToArtistDto(Artist artist);
//
//    LegacyArtistDto mapFromEntityToArtistDto(Artist artist);
//
//    @Mapping(target = "order", source = "orderIndex")
//    ArtistSummaryDto mapFromEntityToSummaryDto (Artist artist, Integer orderIndex);
//
//    @Mapping(target = "displayOrder", source = "orderIndex")
//    ArtistDto.Basic mapToBasicDto (Artist artist, Integer orderIndex);
//
//    default List<ArtistSummaryDto> mapFromArtistsToSummaryList (List<Artist> artists){
//        if(artists == null)  return null;
//
//        return IntStream.range(0, artists.size())
//                .mapToObj(i -> mapFromEntityToSummaryDto(artists.get(i), i))
//                .toList();
//    }
//
//    default List<ArtistDto.Basic> mapFromArtistsToBasicList (List<Artist> artists){
//        if(artists == null)  return null;
//
//        return IntStream.range(0, artists.size())
//                .mapToObj(i -> mapToBasicDto(artists.get(i), i))
//                .toList();
//    }

}
