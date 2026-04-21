package com.spring.songify.infrastructure.crud.controller.artist;

import com.spring.songify.domain.crud.dto.ArtistDto;
import com.spring.songify.domain.crud.dto.ArtistRequestDto;
import com.spring.songify.domain.crud.dto.ArtistWithAlbumDto;
import com.spring.songify.infrastructure.crud.controller.artist.dto.request.CreateArtistRequest;
import com.spring.songify.infrastructure.crud.controller.artist.dto.request.CreateArtistWithDefaultAlbumAndSongRequest;
import com.spring.songify.infrastructure.crud.controller.artist.dto.response.ArtistUpdateNameResponseDto;
import com.spring.songify.infrastructure.crud.controller.artist.dto.response.ArtistWithAlbumResponseDto;
import com.spring.songify.infrastructure.crud.controller.artist.dto.response.CreateArtistResponse;
import com.spring.songify.infrastructure.crud.controller.artist.dto.response.CreateArtistWithDefaultAlbumAndSongResponse;
import com.spring.songify.infrastructure.crud.controller.artist.dto.response.GetAllArtistsResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Slice;

@Mapper(componentModel = "spring")
interface ArtistControllerMapper {
    default GetAllArtistsResponseDto mapSliceToGetAllArtistsResponseDto(Slice<ArtistDto> slice){
        return new GetAllArtistsResponseDto(slice.getContent(), slice.hasNext());
    }

    ArtistRequestDto mapFromCreateArtistRequestToDomainDto(CreateArtistRequest createArtistRequest);

    CreateArtistResponse mapFromArtistDtoToCreateArtistResponse(ArtistDto artistDto);

    @Mapping(source = "artist.id", target = "artistId")
    @Mapping(source = "artist.name", target = "artistName")
    @Mapping(source = "album.id", target = "albumId")
    @Mapping(source = "album.title", target = "albumTitle")
    ArtistWithAlbumResponseDto mapFromDomainDtoToArtistWithAlbumResponseDto(ArtistWithAlbumDto domain);

    ArtistUpdateNameResponseDto mapFromArtistDtoToArtistUpdateNameResponseDto(ArtistDto artistDto);

    ArtistRequestDto mapFromCreateArtistWithDefaultAlbumAndSongRequestToDomainDto(CreateArtistWithDefaultAlbumAndSongRequest createArtistWithDefaultAlbumAndSongRequest);

    CreateArtistWithDefaultAlbumAndSongResponse mapFromArtistDtoToCreateArtistWithDefaultAlbumAndSongResponse(ArtistDto artistDto);

}