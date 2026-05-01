package com.spring.songify.infrastructure.crud.controller.genre;

import com.spring.songify.domain.crud.SongifyCrudFacade;
import com.spring.songify.domain.crud.dto.GenreDto;
import com.spring.songify.domain.crud.dto.GenreRequestDto;
import com.spring.songify.infrastructure.crud.controller.genre.dto.request.CreateGenreRequest;
import com.spring.songify.infrastructure.crud.controller.genre.dto.response.CreateGenreResponse;
import com.spring.songify.infrastructure.crud.controller.genre.dto.response.GetAllGenresResponseDto;
import com.spring.songify.infrastructure.crud.controller.genre.dto.response.GetGenreResponseDto;
import com.spring.songify.infrastructure.error.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    private final SongifyCrudFacade songifyCrudFacade;
    private final GenreControllerMapper genreControllerMapper;

    @Operation(summary = "Create a new genre", description = "Adds a new musical genre to the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Genre created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., missing name).",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping
    ResponseEntity<CreateGenreResponse> postGenre(@RequestBody CreateGenreRequest createGenreRequest) {
        GenreRequestDto genreRequestDto = genreControllerMapper.mapFromCreateGenreRequestDtoToDomainRequest(createGenreRequest);
        GenreDto genreDto = songifyCrudFacade.addGenre(genreRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(genreControllerMapper.mapFromGenreDtoToCreateGenreResponse(genreDto));
    }

    @Operation(summary = "Get all genres", description = "Returns a paginated list of all genres available in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of genres retrieved successfully.")
    })
    @GetMapping
    ResponseEntity<GetAllGenresResponseDto> getGenres(
          @ParameterObject @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Slice<GenreDto> allGenresSlice = songifyCrudFacade.findAllGenres(pageable);
        return ResponseEntity.ok(genreControllerMapper.mapSliceToGetAllGenresResponseDto(allGenresSlice));
    }

    @Operation(summary = "Get genre by ID", description = "Retrieves details of a specific genre by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Genre found and returned successfully."),
            @ApiResponse(responseCode = "404", description = "Genre with the provided ID does not exist.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/{genreId}")
    ResponseEntity<GetGenreResponseDto> getGenreById(@PathVariable Long genreId) {
        GenreDto dto = songifyCrudFacade.findGenreById(genreId);
        return ResponseEntity.ok(genreControllerMapper.mapGenreDtoToGetGenreResponseDto(dto));
    }

    @Operation(summary = "Delete genre", description = "Removes a genre from the database by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Genre deleted successfully (No Content)."),
            @ApiResponse(responseCode = "404", description = "Genre not found.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @DeleteMapping("/{genreId}")
    ResponseEntity<Void> deleteGenreById(@PathVariable Long genreId) {
        songifyCrudFacade.deleteGenreById(genreId);
        return ResponseEntity.noContent().build();
    }
}
