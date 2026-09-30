package com.spring.beatmarket.infrastructure.error;

import java.util.Map;

public record MessageAndMapErrorResponse(String message, Map<String, String> errors) {
}
