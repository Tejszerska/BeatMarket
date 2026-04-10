package com.spring.songify.infrastructure.error;

import org.springframework.http.HttpStatus;

import java.util.List;

record ListOfStringsErrorResponseDto(List<String> errors, HttpStatus status) {
}
