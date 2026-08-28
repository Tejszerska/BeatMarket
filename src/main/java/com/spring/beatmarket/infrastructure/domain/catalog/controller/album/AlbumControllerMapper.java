package com.spring.beatmarket.infrastructure.domain.catalog.controller.album;

import com.spring.beatmarket.domain.catalog.dto.AlbumInfo;
import com.spring.beatmarket.domain.catalog.dto.AlbumSongsDto;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.album.dto.response.AssignAlbumSongResponseDto;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.album.dto.response.GetAlbumDetailsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AlbumControllerMapper {
    GetAlbumDetailsResponse.ArtistSummary mapArtistInfoToArtistSummary(AlbumInfo.ArtistInfo artistInfo);

    GetAlbumDetailsResponse.SongSummary mapSongInfoToSongSummary(AlbumInfo.SongInfo songInfo);

    @Mapping(source = "album.id", target = "albumId")
    @Mapping(source = "album.title", target = "albumTitle")
    @Mapping(source = "song.id", target = "songId")
    @Mapping(source = "song.title", target = "songTitle")
    AssignAlbumSongResponseDto mapFromAlbumSongsDtoToAssignAlbumSongResponseDto(AlbumSongsDto albumSongsDto);
}