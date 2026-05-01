package com.spring.songify.infrastructure.security.jwt;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing the identity of the currently authenticated user")
public record GetEmailDto(
        @Schema(description = "The email address associated with the active session or token", example = "user@gmail.com")
        String email
) {}