package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.domain.catalog.dto.SongSummaryDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring",
        uses = {GenreMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
interface SongMapper {
    @Mapping(source = "genre.name", target = "genre")
    @Mapping(source = "album.title", target = "album")
    @Mapping(source = "artists", target = "artists")
    SongSummaryDto mapFromEntityToSongSummaryDto (Song song);

    default List<String> mapArtists (List<Artist> artists){
        if(artists == null || artists.isEmpty()) return Collections.emptyList();
        return artists.stream()
                .map(Artist::getName)
                .toList();
    }

    SongDto mapFromEntityToSongDto (Song song);
    void updateSongFromDto(SongDto dto, @MappingTarget Song song);
}
