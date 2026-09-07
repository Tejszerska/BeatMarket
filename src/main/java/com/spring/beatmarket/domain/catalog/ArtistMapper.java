package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import com.spring.beatmarket.domain.catalog.dto.SongDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface ArtistMapper {

    ArtistDto.Summary toSummaryDto(Artist artist);

    ArtistDto.Details toDetailsDto(Artist artist);

    ArtistDto.Info toInfoDto(Artist artist);

    @Mapping(target = "displayOrder", source = "orderIndex")
    ArtistDto.Basic toBasicDto(Artist artist, Integer orderIndex);

    default ArtistDto.Reference toReferenceDto(Artist artist) {
        if (artist == null || !artist.isActive()) return null;
        return new ArtistDto.Reference(artist.getId(), artist.getName());
    }

    default List<ArtistDto.Basic> toBasicList(List<Artist> artists) {
        if (artists == null) return Collections.emptyList();
        return IntStream.range(0, artists.size())
                .filter(i -> artists.get(i).isActive())
                .mapToObj(i -> toBasicDto(artists.get(i), i))
                .toList();
    }

    default List<ArtistDto.Reference> toReferenceList(List<Artist> artists) {
        if (artists == null) return Collections.emptyList();
        return artists.stream()
                .filter(Artist::isActive)
                .map(this::toReferenceDto)
                .toList();
    }

    default List<AlbumDto.Reference> toActiveAlbumReferenceList(List<Album> albums) {
        if (albums == null) return Collections.emptyList();
        return albums.stream()
                .filter(Album::isActive)
                .map(album -> new AlbumDto.Reference(album.getId(), album.getTitle()))
                .toList();
    }

    default List<SongDto.Reference> toActiveSongReferenceList(Set<Song> songs) {
        if (songs == null) return Collections.emptyList();
        return songs.stream()
                .filter(Song::isActive)
                .map(song -> new SongDto.Reference(song.getId(), song.getTitle()))
                .toList();
    }
}