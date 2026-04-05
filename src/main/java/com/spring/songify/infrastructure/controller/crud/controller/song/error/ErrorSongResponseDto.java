package com.spring.songify.infrastructure.controller.crud.controller.song.error;

import org.springframework.http.HttpStatus;
public record ErrorSongResponseDto(String message, HttpStatus status) {
}
