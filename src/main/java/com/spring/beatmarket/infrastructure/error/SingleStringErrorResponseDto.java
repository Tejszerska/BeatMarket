package com.spring.beatmarket.infrastructure.error;

import org.springframework.http.HttpStatus;
public record SingleStringErrorResponseDto(String message, HttpStatus status) {
}
