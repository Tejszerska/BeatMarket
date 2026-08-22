package com.spring.beatmarket.infrastructure.domain.catalog.controller.genre;

import com.spring.beatmarket.domain.catalog.CatalogFacade;
import com.spring.beatmarket.domain.catalog.dto.GenreDto;
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
    private final GenreControllerMapper mapper;

    @Operation(summary = "Create a new genre", description = "Adds a new musical genre to the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Genre created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., too short).",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDto.class)))
    })
    @PostMapping
    ResponseEntity<GenreApiDto.InfoResponse> postGenre(@RequestBody @Valid GenreApiDto.Request genreRequest) {
        GenreDto.Create createDto = mapper.toCreateDto(genreRequest);
        GenreDto.Info genreForResponse = facade.addGenre(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toInfoResponse(genreForResponse));
    }

    @Operation(summary = "Get all genres", description = "Returns a slice of all genres available in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of genres retrieved successfully.")
    })
    @GetMapping
    ResponseEntity<GenreApiDto.GetAllResponse> getGenres(
            @ParameterObject @PageableDefault(size = 20, sort = "editedOn", direction = Sort.Direction.ASC) Pageable pageable) {
        Slice<GenreDto.Summary> allGenresSlice = facade.findAllGenres(pageable);
        return ResponseEntity.ok(mapper.toGetAllResponse(allGenresSlice));
    }

    @Operation(summary = "Get genre by ID", description = "Retrieves details of a specific genre by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Genre found and returned successfully."),
            @ApiResponse(responseCode = "404", description = "Genre with the provided ID does not exist.",
                    content = @Content(schema = @Schema(implementation = SingleStringErrorResponseDto.class)))
    })
    @GetMapping("/{genreId}")
    ResponseEntity<GenreApiDto.DetailsResponse> getGenreById(@PathVariable Long genreId) {
        GenreDto.Details dto = facade.getGenreDetails(genreId);
        return ResponseEntity.ok(mapper.toDetailsResponse(dto));
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

    @Operation(summary = "Transfer genres in songs", description = "The service bulk-updates the genre_id field in the associated songs to the new ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Genre transferred successfully."),
            @ApiResponse(responseCode = "400", description = "Provided IDs are identical.",
                    content = @Content(schema = @Schema(implementation = SingleStringErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Genre not found.",
                    content = @Content(schema = @Schema(implementation = SingleStringErrorResponseDto.class)))
    })
    @PatchMapping("/{oldId}/transfer-to/{newId}")
    ResponseEntity<GenreApiDto.TransferResponse> transferGenre(@PathVariable Long oldId, @PathVariable Long newId) {

        GenreDto.Transfer update = facade.transferGenre(oldId, newId);
        return ResponseEntity.ok(mapper.toResponse(update));
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
    ResponseEntity<GenreApiDto.InfoResponse> updateGenre(@PathVariable Long id,
                                                         @RequestBody @Valid GenreApiDto.Request request) {
        GenreDto.Update dto = mapper.toUpdateDto(request);
        GenreDto.Info update = facade.update(id, dto);
        return ResponseEntity.ok(mapper.toInfoResponse(update));
    }
}