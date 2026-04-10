package com.spring.songify.infrastructure.error;

import org.springframework.http.HttpStatus;
public record SingleStringErrorResponseDto(String message, HttpStatus status) {
}
