package com.spring.songify.infrastructure.controller.crud.controller.album;

import com.spring.songify.domain.crud.dto.AlbumDto;
import com.spring.songify.domain.crud.dto.AlbumInfo;
import com.spring.songify.domain.crud.dto.AlbumRequestDto;
import com.spring.songify.domain.crud.dto.AlbumSongsDto;
import com.spring.songify.infrastructure.controller.crud.controller.album.dto.request.CreateAlbumRequest;
import com.spring.songify.infrastructure.controller.crud.controller.album.dto.response.AssignAlbumSongResponseDto;
import com.spring.songify.infrastructure.controller.crud.controller.album.dto.response.CreateAlbumResponse;
import com.spring.songify.infrastructure.controller.crud.controller.album.dto.response.GetAlbumDetailsResponse;
import com.spring.songify.infrastructure.controller.crud.controller.album.dto.response.GetAllAlbumsResponseDto;
import org.springframework.data.domain.Slice;

import java.util.List;

class AlbumControllerMapper {
    static GetAllAlbumsResponseDto mapSliceToGetAllAlbumsResponseDto(Slice<AlbumDto> slice) {
        List<AlbumDto> content = slice.getContent();
        boolean hasNext = slice.hasNext();
        return new GetAllAlbumsResponseDto(content, hasNext);
    }

    static AlbumRequestDto mapFromCreateAlbumRequestToDomainDto(CreateAlbumRequest createAlbumRequest) {
        return AlbumRequestDto.builder()
                .songId(createAlbumRequest.songId())
                .title(createAlbumRequest.title())
                .releaseDate(createAlbumRequest.releaseDate()).build();
    }

    static CreateAlbumResponse mapFromAlbumDtoToCreateAlbumResponse(AlbumDto albumDto) {
        return new CreateAlbumResponse(albumDto.id(), albumDto.title());
    }

    static GetAlbumDetailsResponse mapFromAlbumInfoToGetAlbumDetailsResponse(AlbumInfo albumInfo) {
        List<GetAlbumDetailsResponse.SongSummary> songSummaryList = albumInfo.getSongs()
                .stream()
                .map(AlbumControllerMapper::mapSongInfoToSongSummary)
                .toList();

        List<GetAlbumDetailsResponse.ArtistSummary> artistSummaryList = albumInfo.getArtists()
                .stream()
                .map(AlbumControllerMapper::mapArtistInfoToArtistSummary)
                .toList();

        return GetAlbumDetailsResponse.builder()
                .id(albumInfo.getId())
                .title(albumInfo.getTitle())
                .songs(songSummaryList)
                .artists(artistSummaryList)
                .build();
    }

    static GetAlbumDetailsResponse.ArtistSummary mapArtistInfoToArtistSummary(AlbumInfo.ArtistInfo artistInfo) {
        return new GetAlbumDetailsResponse.ArtistSummary(artistInfo.getId(), artistInfo.getName());
    }

    static GetAlbumDetailsResponse.SongSummary mapSongInfoToSongSummary(AlbumInfo.SongInfo songInfo) {
        return new GetAlbumDetailsResponse.SongSummary(songInfo.getId(), songInfo.getTitle());
    }

    static AssignAlbumSongResponseDto mapFromAlbumSongsDtoToAssignAlbumSongResponseDto (AlbumSongsDto albumSongsDto){
        return AssignAlbumSongResponseDto.builder()
                .albumId(albumSongsDto.album().id())
                .albumTitle(albumSongsDto.album().title())
                .songId(albumSongsDto.song().id())
                .songTitle(albumSongsDto.song().title())
                .build();
    }
}
