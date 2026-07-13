package com.spring.beatmarket.infrastructure.account.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload for registering a new user")
public record RegisterUserRequestDto(

        @Schema(description = "User's email address, used for registration and account verification", example = "test@gmail.com")
        @NotBlank(message = "Email is required")
        String email,

        @Schema(description = "Strong password for the new account", example = "SuperSecret123!")
        @NotBlank(message = "Password is required")
        String password
) {
}
