package com.spring.beatmarket.infrastructure.error;

import java.util.Map;

public record ValidationErrorResponseDto(String message, Map<String, String> errors) {
}
