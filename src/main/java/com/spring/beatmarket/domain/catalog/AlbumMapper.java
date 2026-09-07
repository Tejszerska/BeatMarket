package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import com.spring.beatmarket.domain.catalog.dto.LegacyAlbumDto;
import com.spring.beatmarket.domain.catalog.dto.SongDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
interface AlbumMapper {

    LegacyAlbumDto mapFromEntityToAlbumDto(Album album);

    AlbumDto.Summary toSummaryDto(Album album);

    AlbumDto.Details toDetailsDto(Album album);

    default AlbumDto.Reference toActiveReferenceDto(Album album) {
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

    default Set<SongDto.Reference> toActiveSongReferenceSet(Set<Song> songs) {
        if (songs == null) return Collections.emptySet();
        return songs.stream()
                .filter(Song::isActive)
                .map(song -> new SongDto.Reference(song.getId(), song.getTitle()))
                .collect(Collectors.toSet());
    }
}