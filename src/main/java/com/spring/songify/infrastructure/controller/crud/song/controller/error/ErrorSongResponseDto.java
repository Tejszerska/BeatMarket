package com.spring.songify.infrastructure.controller.crud.song.controller.error;

import org.springframework.http.HttpStatus;
public record ErrorSongResponseDto(String message, HttpStatus status) {
}
