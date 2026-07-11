package com.spring.beatmarket.infrastructure.error;

import org.springframework.http.HttpStatus;
public record ErrorResponseDto(String message, HttpStatus status) {
}
