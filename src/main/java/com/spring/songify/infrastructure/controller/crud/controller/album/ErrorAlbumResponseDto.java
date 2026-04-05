package com.spring.songify.infrastructure.controller.crud.controller.album;

import org.springframework.http.HttpStatus;
public record ErrorAlbumResponseDto(String message, HttpStatus status) {
}
