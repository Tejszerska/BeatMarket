package com.spring.songify.infrastructure.usercrud.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequestDto(
        @NotBlank(message = "Email is required")
        String email,
        @NotBlank(message = "Password is required")
        String password
) {
}
