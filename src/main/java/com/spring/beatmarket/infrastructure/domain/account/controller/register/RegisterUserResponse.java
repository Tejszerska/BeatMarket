package com.spring.beatmarket.infrastructure.domain.account.controller.register;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response payload after successful user registration")
public record RegisterUserResponse(
        @Schema(description = "Confirmation message", example = "User created")
        String message
) {
}
