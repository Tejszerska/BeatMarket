package com.spring.beatmarket.infrastructure.domain.account.controller.register.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response payload after successful user registration")
public record RegisterUserResponseDto(
        @Schema(description = "Confirmation message", example = "User created")
        String message
) {
}
