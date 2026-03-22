package com.spring.songify.infrastructure.controller.crud.song.controller.dto.response;


import com.spring.songify.domain.crud.song.dto.SongDto;

public record PartiallyUpdateSongResponseDto(SongDto updatedSong) {
}
