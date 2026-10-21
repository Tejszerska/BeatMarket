package com.spring.beatmarket.infrastructure.domain.account.controller.login;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response payload for login and logout operations")
public record LoginResponse(
        @Schema(description = "Login success message", example = "Login successful.")
        String message
) {}
