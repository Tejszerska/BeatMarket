package com.spring.songify.infrastructure.controller.crud.song.controller.artist;

import com.spring.songify.domain.crud.dto.ArtistDto;
import com.spring.songify.infrastructure.controller.crud.song.controller.artist.dto.response.GetAllArtistsResponseDto;
import org.springframework.data.domain.Slice;

import java.util.List;

class ArtistControllerMapper {
    static GetAllArtistsResponseDto mapSliceToGetAllArtistsResponseDto (Slice <ArtistDto> slice){
        List<ArtistDto> content = slice.getContent();
        boolean hasNext = slice.hasNext();
        return new GetAllArtistsResponseDto(content, hasNext);
    }
}
