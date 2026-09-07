package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.domain.licensing.dto.SongPriceDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Mapper(componentModel = "spring",
        uses = {GenreMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
interface SongMapper {

    @Mapping(source = "songPricesDto", target = "pricing")
    SongDto.Summary toSummaryDto(Song song, List<SongPriceDto> songPricesDto);

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
        if (song == null || !song.isActive()) return null;
        return new SongDto.Reference(song.getId(), song.getTitle());
    }

    default AlbumDto.Summary toActiveAlbumSummary(Album album) {
        if (album == null || !album.isActive()) return null;
        List<ArtistDto.Reference> artists = toActiveArtistReferenceList(album.getArtists());
        return new AlbumDto.Summary(album.getId(), album.getTitle(), album.getCoverUrl(), artists);
    }

    default AlbumDto.Reference toActiveAlbumReference(Album album) {
        if (album == null || !album.isActive()) return null;
        return new AlbumDto.Reference(album.getId(), album.getTitle());
    }

    default List<ArtistDto.Reference> toActiveArtistReferenceList(List<Artist> artists) {
        if (artists == null) return Collections.emptyList();
        return artists.stream()
                .filter(Artist::isActive)
                .map(artist -> new ArtistDto.Reference(artist.getId(), artist.getName()))
                .toList();
    }

    default List<ArtistDto.Basic> toActiveArtistBasicList(List<Artist> artists) {
        if (artists == null) return Collections.emptyList();
        return IntStream.range(0, artists.size())
                .filter(i -> artists.get(i).isActive())
                .mapToObj(i -> new ArtistDto.Basic(
                        artists.get(i).getId(),
                        artists.get(i).getName(),
                        artists.get(i).getImageUrl(),
                        i
                ))
                .toList();
    }
}