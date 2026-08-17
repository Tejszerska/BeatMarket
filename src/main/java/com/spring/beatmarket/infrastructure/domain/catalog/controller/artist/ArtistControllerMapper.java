package com.spring.beatmarket.infrastructure.domain.catalog.controller.artist;

import com.spring.beatmarket.domain.catalog.dto.LegacyArtistDto;
import com.spring.beatmarket.domain.catalog.dto.ArtistRequestDto;
import com.spring.beatmarket.domain.catalog.dto.ArtistWithAlbumDto;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.artist.dto.request.CreateArtistRequest;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.artist.dto.request.CreateArtistWithDefaultAlbumAndSongRequest;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.artist.dto.response.ArtistUpdateNameResponseDto;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.artist.dto.response.ArtistWithAlbumResponseDto;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.artist.dto.response.CreateArtistResponse;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.artist.dto.response.CreateArtistWithDefaultAlbumAndSongResponse;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.artist.dto.response.GetAllArtistsResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Slice;

@Mapper(componentModel = "spring")
interface ArtistControllerMapper {
    default GetAllArtistsResponseDto mapSliceToGetAllArtistsResponseDto(Slice<LegacyArtistDto> slice){
        return new GetAllArtistsResponseDto(slice.getContent(), slice.hasNext());
    }

    ArtistRequestDto mapFromCreateArtistRequestToDomainDto(CreateArtistRequest createArtistRequest);

    CreateArtistResponse mapFromArtistDtoToCreateArtistResponse(LegacyArtistDto legacyArtistDto);

    @Mapping(source = "artist.id", target = "artistId")
    @Mapping(source = "artist.name", target = "artistName")
    @Mapping(source = "album.id", target = "albumId")
    @Mapping(source = "album.title", target = "albumTitle")
    ArtistWithAlbumResponseDto mapFromDomainDtoToArtistWithAlbumResponseDto(ArtistWithAlbumDto domain);

    ArtistUpdateNameResponseDto mapFromArtistDtoToArtistUpdateNameResponseDto(LegacyArtistDto legacyArtistDto);

    ArtistRequestDto mapFromCreateArtistWithDefaultAlbumAndSongRequestToDomainDto(CreateArtistWithDefaultAlbumAndSongRequest createArtistWithDefaultAlbumAndSongRequest);

    CreateArtistWithDefaultAlbumAndSongResponse mapFromArtistDtoToCreateArtistWithDefaultAlbumAndSongResponse(LegacyArtistDto legacyArtistDto);

}