package com.spring.beatmarket.infrastructure.catalog.controller.album;

import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import com.spring.beatmarket.domain.catalog.dto.AlbumInfo;
import com.spring.beatmarket.domain.catalog.dto.AlbumRequestDto;
import com.spring.beatmarket.domain.catalog.dto.AlbumSongsDto;
import com.spring.beatmarket.infrastructure.catalog.controller.album.dto.request.CreateAlbumRequest;
import com.spring.beatmarket.infrastructure.catalog.controller.album.dto.response.AssignAlbumSongResponseDto;
import com.spring.beatmarket.infrastructure.catalog.controller.album.dto.response.CreateAlbumResponse;
import com.spring.beatmarket.infrastructure.catalog.controller.album.dto.response.GetAlbumDetailsResponse;
import com.spring.beatmarket.infrastructure.catalog.controller.album.dto.response.GetAllAlbumsResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Slice;

@Mapper(componentModel = "spring")
public interface AlbumControllerMapper {

    AlbumRequestDto mapFromCreateAlbumRequestToDomainDto(CreateAlbumRequest createAlbumRequest);

    CreateAlbumResponse mapFromAlbumDtoToCreateAlbumResponse(AlbumDto albumDto);

    default GetAllAlbumsResponseDto mapSliceToGetAllAlbumsResponseDto(Slice<AlbumDto> slice) {
        return new GetAllAlbumsResponseDto(slice.getContent(), slice.hasNext());
    }

    GetAlbumDetailsResponse mapFromAlbumInfoToGetAlbumDetailsResponse(AlbumInfo albumInfo);

    GetAlbumDetailsResponse.ArtistSummary mapArtistInfoToArtistSummary(AlbumInfo.ArtistInfo artistInfo);

    GetAlbumDetailsResponse.SongSummary mapSongInfoToSongSummary(AlbumInfo.SongInfo songInfo);

    @Mapping(source = "album.id", target = "albumId")
    @Mapping(source = "album.title", target = "albumTitle")
    @Mapping(source = "song.id", target = "songId")
    @Mapping(source = "song.title", target = "songTitle")
    AssignAlbumSongResponseDto mapFromAlbumSongsDtoToAssignAlbumSongResponseDto(AlbumSongsDto albumSongsDto);
}