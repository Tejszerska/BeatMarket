package com.spring.songify.infrastructure.error;

import org.springframework.http.HttpStatus;
public record ErrorResponseDto(String message, HttpStatus status) {
}
