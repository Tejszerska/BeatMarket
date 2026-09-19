package com.spring.beatmarket.infrastructure.domain.catalog.controller.genre;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public interface GenreApiDto {

    @Schema(name = "GenreRequest")
    record Request(
            @Schema(description = "Name of the genre", example = "Synthwave")
            @NotBlank(message = "Genre name cannot be blank")
            @Size(min = 3, max = 255, message = "Genre name must be between 3 and 255 characters long")
            String name
    ) {
    }

    @Schema(name = "GenreDetailsResponse")
    record DetailsResponse(
            @Schema(description = "Unique ID of the genre", example = "42")
            Long id,
            @Schema(description = "Name of the genre", example = "Synthwave")
            String name
    ) {
    }

    @Schema(name = "GenreSummaryResponse")
    record SummaryResponse(
            @Schema(description = "Unique ID of the genre", example = "42")
            Long id,
            @Schema(description = "Name of the genre", example = "Synthwave")
            String name
    ) {
    }

    @Schema(name = "GenreInfoResponse")
    record InfoResponse(
            @Schema(description = "Unique ID of the genre", example = "42")
            Long id,
            @Schema(description = "Name of the genre", example = "Synthwave")
            String name
    ) {
    }

    @Schema(name = "GenreTransferResponse")
    record TransferResponse(
            @Schema(description = "Number of songs updated to the new genre", example = "150")
            Integer updatedSongsCount,
            @Schema(description = "ID of the old genre from which songs were transferred", example = "10")
            Long oldGenreId,
            @Schema(description = "ID of the new genre to which songs were transferred", example = "42")
            Long newGenreId
    ) {
    }

    @Schema(name = "GetAllGenresResponse")
    record GetAllResponse(
            @Schema(description = "List of genres for the current page")
            List<SummaryResponse> genres,
            @Schema(description = "Indicates if there is a next page of genres available", example = "false")
            boolean hasNext
    ) {
    }

    @Schema(name = "GenreReference")
    record Reference(
            @Schema(description = "Unique ID of the genre", example = "42")
            Long id,
            @Schema(description = "Name of the genre", example = "Synthwave")
            String name
    ) {
    }
}