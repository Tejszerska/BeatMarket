package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.song.LegacySongDto;
import com.spring.beatmarket.domain.catalog.dto.song.PriceWithCurrencyDto;
import com.spring.beatmarket.domain.catalog.dto.song.SongDetailsDto;
import com.spring.beatmarket.domain.catalog.dto.song.SongSummaryDto;
import com.spring.beatmarket.domain.licensing.dto.SongPriceDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
    SongSummaryDto toSummaryDto(Song song, List<SongPriceDto> songPricesDto);

    @Mapping(source = "songPricesDto", target = "pricing")
    SongDetailsDto toDetailsDto(Song song, List<SongPriceDto> songPricesDto);

    LegacySongDto toDto(Song song);


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
}
