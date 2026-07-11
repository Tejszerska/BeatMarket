package com.spring.beatmarket.infrastructure.security.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload for traditional user authentication (username and password)")
public record TokenRequestDto(
        @Schema(description = "User's email address, acting as the username", example = "a@dmin")
        @NotBlank(message = "Email is required")
        String username,

        @Schema(description = "User's password", example = "123")
        @NotBlank(message = "Password is required")
        String password
) {}
