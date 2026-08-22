package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.IntStream;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface ArtistMapper {

    ArtistDto.Summary toSummaryDto(Artist artist);

    ArtistDto.Details toDetailsDto(Artist artist);

    ArtistDto.Info toInfoDto(Artist artist);

    @Mapping(target = "displayOrder", source = "orderIndex")
    ArtistDto.Basic toBasicDto(Artist artist, Integer orderIndex);

    ArtistDto.Reference toReferenceDto(Artist artist);

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
}