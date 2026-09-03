package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.domain.licensing.dto.SongPriceDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.IterableMapping;
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

    @IterableMapping(qualifiedByName = "activeSummary")
    @Mapping(source = "songPricesDto", target = "pricing")
    SongDto.Summary toSummaryDto(Song song, List<SongPriceDto> songPricesDto);

    @IterableMapping(qualifiedByName = "activeSummary")
    @Mapping(source = "songPricesDto", target = "pricing")
    SongDto.Details toDetailsDto(Song song, List<SongPriceDto> songPricesDto);

    SongDto.Info toInfoDto(Song song);

    default Map<String, SongDto.Price> mapPricing(List<SongPriceDto> songPriceDtos) {
        if (songPriceDtos == null || songPriceDtos.isEmpty()) return Collections.emptyMap();
        Map<String, SongDto.Price> mappedPricing = new HashMap<>();
        for (SongPriceDto dto : songPriceDtos) {
            mappedPricing.put(dto.tier(), new SongDto.Price(dto.price(), dto.currency()));
        }
        return mappedPricing;
    }

    default SongDto.Reference toActiveReferenceDto(Song song) {
        if (song == null || !song.isActive()) {
            return null;
        }
        return new SongDto.Reference(song.getId(), song.getTitle());
    }
}
