package com.spring.beatmarket.infrastructure.error;

import java.util.Map;

public record MessageAndErrorsResponseDto(String message, Map<String, String> errors) {
}
