package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.PriceWithCurrencyDto;
import com.spring.beatmarket.domain.catalog.dto.SongCreatedDto;
import com.spring.beatmarket.domain.catalog.dto.SongDetailsDto;
import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.domain.catalog.dto.SongSummaryDto;
import com.spring.beatmarket.domain.licensing.dto.SongPriceDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring",
        uses = {GenreMapper.class, AlbumMapper.class, ArtistMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
interface SongMapper {
    @Mapping(source = "song.genre.name", target = "genre")
    @Mapping(source = "song.album.title", target = "album")
    @Mapping(source = "song.artists", target = "artists")
    @Mapping(source = "songPricesDto", target = "pricing")
    SongSummaryDto mapFromEntityToSongSummaryDto (Song song, List<SongPriceDto> songPricesDto);

    default List<String> mapArtists (List<Artist> artists){
        if(artists == null || artists.isEmpty()) return Collections.emptyList();
        return artists.stream()
                .map(Artist::getName)
                .toList();
    }

    default Map<String, PriceWithCurrencyDto> mapPricing(List<SongPriceDto> songPriceDtos){
        if (songPriceDtos == null || songPriceDtos.isEmpty()) return Collections.emptyMap();
        Map<String, PriceWithCurrencyDto> mappedPricing = new HashMap<>();
        for (SongPriceDto dto : songPriceDtos ) {
            mappedPricing.put(dto.tier(), new PriceWithCurrencyDto(dto.price(), dto.currency()));
        }
        return mappedPricing;
    }

    SongDetailsDto mapFromEntityToDetailsDto(Song song);

    SongDto mapFromEntityToSongDto (Song song);

    SongCreatedDto mapFromEntityToSongCreatedDto(Song song);

    void updateSongFromDto(SongDto dto, @MappingTarget Song song);
}
