package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import com.spring.beatmarket.domain.catalog.dto.SongDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = FileStorageUrlMapper.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
interface AlbumMapper {

    @Mapping(source = "coverFileKey", target = "coverUrl", qualifiedByName = "fileStorageUrl")
    AlbumDto.Summary toSummaryDto(Album album);

    @Mapping(source = "coverFileKey", target = "coverUrl", qualifiedByName = "fileStorageUrl")
    AlbumDto.Details toDetailsDto(Album album);


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

    @Mapping(source = "coverFileKey", target = "coverUrl", qualifiedByName = "fileStorageUrl")
    AlbumDto.Info toInfoDto(Album album);
}