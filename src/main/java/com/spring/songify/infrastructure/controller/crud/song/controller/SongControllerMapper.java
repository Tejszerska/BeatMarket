package com.spring.songify.infrastructure.controller.crud.song.controller;

import com.spring.songify.domain.crud.song.dto.SongDto;
import com.spring.songify.infrastructure.controller.crud.song.controller.dto.request.CreateSongRequestDto;
import com.spring.songify.infrastructure.controller.crud.song.controller.dto.request.PartiallyUpdateSongRequestDto;
import com.spring.songify.infrastructure.controller.crud.song.controller.dto.request.UpdateSongRequestDto;
import com.spring.songify.infrastructure.controller.crud.song.controller.dto.response.CreateSongResponseDto;
import com.spring.songify.infrastructure.controller.crud.song.controller.dto.response.DeleteSongResponseDto;
import com.spring.songify.infrastructure.controller.crud.song.controller.dto.response.GetAllSongsResponseDto;
import com.spring.songify.infrastructure.controller.crud.song.controller.dto.response.GetSongResponseDto;
import com.spring.songify.infrastructure.controller.crud.song.controller.dto.response.PartiallyUpdateSongResponseDto;
import com.spring.songify.infrastructure.controller.crud.song.controller.dto.response.UpdateSongResponseDto;
import org.springframework.http.HttpStatus;

import java.util.List;

class SongControllerMapper {

    static SongDto mapFromCreateSongRequestDtoToSongDto(CreateSongRequestDto dto) {
        return SongDto
                .builder()
                .name(dto.songName())
                .build();
    }

    static SongDto mapFromUpdateSongRequestDtoToSongDto(UpdateSongRequestDto dto) {
        return SongDto
                .builder()
                .name(dto.songName())
                .build();
    }

    static SongDto mapFromPartiallyUpdateSongRequestDtoToSong(PartiallyUpdateSongRequestDto dto) {
        return SongDto
                .builder()
                .name(dto.songName())
                .build();
    }

    static CreateSongResponseDto mapFromSongToCreateSongResponseDto(SongDto songDto) {
        return new CreateSongResponseDto(songDto);
    }

    static DeleteSongResponseDto mapFromSongToDeleteSongResponseDto(Long id) {
        return new DeleteSongResponseDto("You deleted song with id: " + id, HttpStatus.OK);
    }

    static UpdateSongResponseDto mapFromSongToUpdateSongResponseDto(SongDto newSong) {
        return new UpdateSongResponseDto(newSong.name(), "testt");
    }

    static PartiallyUpdateSongResponseDto mapFromSongDtoToPartiallyUpdateSongResponseDto(SongDto songDto) {
        return new PartiallyUpdateSongResponseDto(songDto);
    }

    static GetSongResponseDto mapFromSongToGetSongResponseDto(SongDto songDto) {
        return new GetSongResponseDto(songDto);
    }

    static GetAllSongsResponseDto mapFromSongToGetAllSongsResponseDto(List<SongDto> songs) {
        return new GetAllSongsResponseDto(songs);
    }
}
