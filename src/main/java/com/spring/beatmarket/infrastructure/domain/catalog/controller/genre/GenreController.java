package com.spring.beatmarket.infrastructure.domain.catalog.controller.genre;

import com.spring.beatmarket.domain.catalog.CatalogFacade;
import com.spring.beatmarket.domain.catalog.dto.LegacyGenreDto;
import com.spring.beatmarket.domain.catalog.dto.SaveGenreDto;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.genre.dto.request.GenreRequest;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.genre.dto.response.GenreResponse;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.genre.dto.response.GetAllGenresResponse;
import com.spring.beatmarket.infrastructure.error.SingleStringErrorResponseDto;
import com.spring.beatmarket.infrastructure.error.ValidationErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Tag(name = "5. Genres", description = "Endpoints for managing musical genres.")
@RestController
@AllArgsConstructor
@RequestMapping("/genres")
class GenreController {
    private final CatalogFacade facade;
    private final GenreControllerMapper genreControllerMapper;

    @Operation(summary = "Create a new genre", description = "Adds a new musical genre to the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Genre created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., too short).",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDto.class)))
    })
    @PostMapping
    ResponseEntity<GenreResponse> postGenre(@RequestBody @Valid GenreRequest genreRequest) {
        SaveGenreDto saveGenreDto = genreControllerMapper.toDomain(genreRequest);
        LegacyGenreDto legacyGenreDto = facade.addGenre(saveGenreDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(genreControllerMapper.toResponse(legacyGenreDto));
    }

    @Operation(summary = "Get all genres", description = "Returns a paginated list of all genres available in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of genres retrieved successfully.")
    })
    @GetMapping
    ResponseEntity<GetAllGenresResponse> getGenres(
          @ParameterObject @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Slice<LegacyGenreDto> allGenresSlice = facade.findAllGenres(pageable);
        return ResponseEntity.ok(genreControllerMapper.toGetAllGenresResponse(allGenresSlice));
    }

    @Operation(summary = "Get genre by ID", description = "Retrieves details of a specific genre by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Genre found and returned successfully."),
            @ApiResponse(responseCode = "404", description = "Genre with the provided ID does not exist.",
                    content = @Content(schema = @Schema(implementation = SingleStringErrorResponseDto.class)))
    })
    @GetMapping("/{genreId}")
    ResponseEntity<GenreResponse> getGenreById(@PathVariable Long genreId) {
        LegacyGenreDto dto = facade.findGenreById(genreId);
        return ResponseEntity.ok(genreControllerMapper.toResponse(dto));
    }

    @Operation(summary = "Delete genre", description = "Removes a genre from the database by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Genre deleted successfully (No Content)."),
            @ApiResponse(responseCode = "404", description = "Genre not found.",
                    content = @Content(schema = @Schema(implementation = SingleStringErrorResponseDto.class)))
    })
    @DeleteMapping("/{genreId}")
    ResponseEntity<Void> deleteGenreById(@PathVariable Long genreId) {
        facade.deleteGenreById(genreId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update genre name", description = "Updates name of the genre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Genre updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Genre not found.",
                    content = @Content(schema = @Schema(implementation = SingleStringErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Genre name must be unique.",
                    content = @Content(schema = @Schema(implementation = SingleStringErrorResponseDto.class)))
    })
    @PatchMapping("/{id}")
    ResponseEntity<GenreResponse> updateGenre(@PathVariable Long id,
                                            @RequestBody @Valid GenreRequest request) {
        SaveGenreDto dto = genreControllerMapper.toDomain(request);
        LegacyGenreDto update = facade.update(id, dto);
        return ResponseEntity.ok(genreControllerMapper.toResponse(update));
    }
}
