package com.spring.songify.infrastructure.controller.crud.song.controller.song.dto.request;

public record PartiallyUpdateSongRequestDto(
        String songName,
        String artist
) {
}
