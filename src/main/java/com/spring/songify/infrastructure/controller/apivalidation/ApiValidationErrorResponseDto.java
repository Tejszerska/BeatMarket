package com.spring.songify.infrastructure.controller.apivalidation;

import org.springframework.http.HttpStatus;

import java.util.List;

record ApiValidationErrorResponseDto(List<String> errors, HttpStatus status) {
}
