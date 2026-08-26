package com.spring.beatmarket.domain.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(name = "RoleConflictErrorResponse")
public record RoleConflictErrorResponse(
        @Schema(description = "General error message", example = "Artist cannot be both main and featured on the same Song")
        String message,

        @Schema(description = "Target resource type with conflicts", example = "Song")
        String entity,

        @Schema(description = "Set of conflicting IDs provided in request", example = "[2, 7]")
        Set<Long> conflictingIds
) {
}