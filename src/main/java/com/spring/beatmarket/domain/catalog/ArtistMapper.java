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

@Mapper(componentModel = "spring",
        uses = FileStorageUrlMapper.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface ArtistMapper {

    @Mapping(source = "imageFileKey", target = "imageUrl", qualifiedByName = "fileStorageUrl")
    ArtistDto.Summary toSummaryDto(Artist artist);

    @Mapping(source = "imageFileKey", target = "imageUrl", qualifiedByName = "fileStorageUrl")
    ArtistDto.Details toDetailsDto(Artist artist);

    @Mapping(source = "imageFileKey", target = "imageUrl", qualifiedByName = "fileStorageUrl")
    ArtistDto.Info toInfoDto(Artist artist);

    @Mapping(source = "artist.imageFileKey", target = "imageUrl", qualifiedByName = "fileStorageUrl")
    @Mapping(target = "displayOrder", source = "orderIndex")
    ArtistDto.Basic toBasicDto(Artist artist, Integer orderIndex);

    default List<AlbumDto.Reference> toActiveAlbumReferenceList(Set<Album> albums) {
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