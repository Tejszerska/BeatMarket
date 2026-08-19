package com.spring.beatmarket.infrastructure.domain.catalog.controller.genre;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public interface GenreApiDto {

    @Schema(name = "GenreRequest")
    record Request(
            @NotBlank(message = "Genre name cannot be blank")
            @Size(min = 3, max = 255, message = "Genre name must be between 3 and 255 characters long")
            String name
    ) {
    }

    @Schema(name = "GenreDetailsResponse")
    record DetailsResponse(
            Long id,
            String name
    ) {
    }

    @Schema(name = "GenreSummaryResponse")
    record SummaryResponse(
            Long id,
            String name
    ) {
    }

    @Schema(name = "GenreInfoResponse")
    record InfoResponse(
            Long id,
            String name
    ) {
    }

    @Schema(name = "GetAllGenresResponse")
    record GetAllResponse(
            List<SummaryResponse> genres,
            boolean hasNext
    ) {
    }

    @Schema(name = "GenreReference")
    record Reference(
            Long id,
            String name
    ) {
    }
}
