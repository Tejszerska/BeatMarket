package com.spring.songify.infrastructure.controller.crud.song.controller.song.dto.response;

import org.springframework.http.HttpStatus;

public record DeleteSongResponseDto(String message, HttpStatus status) {
}
