package com.spring.songify.infrastructure.usercrud.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response payload after successful user registration")
public record RegisterUserResponseDto(
        @Schema(description = "Confirmation message", example = "User created")
        String message
) {
}
