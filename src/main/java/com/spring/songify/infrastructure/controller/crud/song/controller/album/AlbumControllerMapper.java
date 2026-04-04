package com.spring.songify.infrastructure.controller.crud.song.controller.album;

import com.spring.songify.domain.crud.dto.AlbumDto;
import com.spring.songify.infrastructure.controller.crud.song.controller.album.response.GetAllAlbumsResponseDto;
import org.springframework.data.domain.Slice;

import java.util.List;

class AlbumControllerMapper {
    static GetAllAlbumsResponseDto mapSliceToGetAllAlbumsResponseDto (Slice<AlbumDto> slice){
        List<AlbumDto> content = slice.getContent();
        boolean hasNext = slice.hasNext();
        return new GetAllAlbumsResponseDto(content, hasNext);
    }
}
