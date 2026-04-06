package com.spring.songify.infrastructure.controller.crud.controller.artist;

import com.spring.songify.domain.crud.dto.ArtistDto;
import com.spring.songify.domain.crud.dto.ArtistRequestDto;
import com.spring.songify.domain.crud.dto.ArtistWithAlbumDto;
import com.spring.songify.infrastructure.controller.crud.controller.artist.dto.request.CreateArtistRequest;
import com.spring.songify.infrastructure.controller.crud.controller.artist.dto.request.CreateArtistWithDefaultAlbumAndSongRequest;
import com.spring.songify.infrastructure.controller.crud.controller.artist.dto.response.ArtistUpdateNameResponseDto;
import com.spring.songify.infrastructure.controller.crud.controller.artist.dto.response.ArtistWithAlbumResponseDto;
import com.spring.songify.infrastructure.controller.crud.controller.artist.dto.response.CreateArtistResponse;
import com.spring.songify.infrastructure.controller.crud.controller.artist.dto.response.CreateArtistWithDefaultAlbumAndSongResponse;
import com.spring.songify.infrastructure.controller.crud.controller.artist.dto.response.GetAllArtistsResponseDto;
import org.springframework.data.domain.Slice;

import java.util.List;

class ArtistControllerMapper {
    static GetAllArtistsResponseDto mapSliceToGetAllArtistsResponseDto(Slice<ArtistDto> slice) {
        List<ArtistDto> content = slice.getContent();
        boolean hasNext = slice.hasNext();
        return new GetAllArtistsResponseDto(content, hasNext);
    }

    static ArtistRequestDto mapFromCreateArtistRequestToDomainDto(CreateArtistRequest createArtistRequest) {
        return new ArtistRequestDto(createArtistRequest.name());
    }

    static CreateArtistResponse mapFromArtistDtoToCreateArtistResponse(ArtistDto artistDto) {
        return new CreateArtistResponse(artistDto.id(), artistDto.name());
    }

    static ArtistWithAlbumResponseDto mapFromDomainDtoToArtistWithAlbumResponseDto(ArtistWithAlbumDto domain) {
        return ArtistWithAlbumResponseDto.builder()
                .artistId(domain.artistDto().id())
                .artistName(domain.artistDto().name())
                .albumId(domain.albumDto().id())
                .albumTitle(domain.albumDto().title())
                .build();
    }

    static ArtistUpdateNameResponseDto mapFromArtistDtoToArtistUpdateNameResponseDto(ArtistDto artistDto) {
        return new ArtistUpdateNameResponseDto(artistDto.id(), artistDto.name());
    }

    static ArtistRequestDto mapFromCreateArtistWithDefaultAlbumAndSongRequestToDomainDto(CreateArtistWithDefaultAlbumAndSongRequest createArtistWithDefaultAlbumAndSongRequest) {
        return new ArtistRequestDto(createArtistWithDefaultAlbumAndSongRequest.name());
    }

    static CreateArtistWithDefaultAlbumAndSongResponse mapFromArtistDtoToCreateArtistWithDefaultAlbumAndSongResponse(ArtistDto artistDto){
        return new CreateArtistWithDefaultAlbumAndSongResponse(artistDto.id(), artistDto.name());
    }
}
