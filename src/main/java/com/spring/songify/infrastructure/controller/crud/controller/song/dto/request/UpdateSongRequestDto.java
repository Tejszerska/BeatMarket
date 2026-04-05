package com.spring.songify.infrastructure.controller.crud.controller.song.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
public record UpdateSongRequestDto(
        @NotNull(message = "title must not be null")
        @NotEmpty(message = "title must not be empty")
        String title) {
}

