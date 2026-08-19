package com.spring.beatmarket.infrastructure.domain.catalog.controller.song;

import com.spring.beatmarket.domain.catalog.CatalogFacade;
import com.spring.beatmarket.domain.catalog.dto.SongDto;
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
import lombok.extern.log4j.Log4j2;
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

@Tag(name = "2. Songs", description = "Endpoints for managing songs, their details, and genre assignments.")
@RestController
@Log4j2
@RequestMapping("/catalog/songs")
@AllArgsConstructor
public
class SongController {

    private final CatalogFacade facade;
    private final SongControllerMapper mapper;

    @Operation(summary = "Get all songs", description = "Returns a paginated list of all songs in the database, with an option to limit results.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of songs retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid query parameters.",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDto.class)))

    })
    @GetMapping
    ResponseEntity<SongApiDto.GetAllResponse> getAllSongs(
            SongApiDto.SearchRequest searchRequestDto,
            @ParameterObject @PageableDefault(size = 20, sort = "editedOn", direction = Sort.Direction.DESC) Pageable pageable) {
        if(searchRequestDto.maxPrice() != null){
            if(searchRequestDto.currency() == null || searchRequestDto.license() == null){
                throw new InvalidSearchCriteriaException("maxPrice",
                        "Filtering by maxPrice requires declaring currency and license.");
            }
        }
        SongDto.SearchCriteria searchCriteria = mapper.toDomain(searchRequestDto);

        Slice<SongDto.Summary> allSongs = facade.findAllSongs(searchCriteria, pageable);

        return ResponseEntity.ok(mapper.toResponse(allSongs));
    }

    @Operation(summary = "Get song by ID", description = "Retrieves detailed information about a specific song by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Song found and returned successfully."),
            @ApiResponse(responseCode = "404", description = "Song with the provided ID does not exist.",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDto.class))),
    })
    @GetMapping("/{id}")
    ResponseEntity<SongApiDto.DetailsResponse> getSongById(@PathVariable Long id) {
        SongDto.Details songDetails = facade.getSongDetailsById(id);
        return ResponseEntity.ok(mapper.toResponse(songDetails));
    }

    @Operation(summary = "Create a new song", description = "Adds a new song to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Song created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., negative duration).",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDto.class))),
    })
    @PostMapping
    ResponseEntity<SongApiDto.InfoResponse> postSong(@RequestBody @Valid SongApiDto.CreateRequest createSongRequest) {
        SongDto.Create domainRequest = mapper.toDomain(createSongRequest);
        SongDto.Info addedSong = facade.addSong(domainRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(addedSong));
    }

    @Operation(summary = "Delete song", description = "Removes a song from the database by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Song deleted successfully (No Content)."),
            @ApiResponse(responseCode = "404", description = "Song not found.",
                    content = @Content(schema = @Schema(implementation = SingleStringErrorResponseDto.class))),
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteSongById(@PathVariable Long id) {
        facade.deleteSongById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Partially update song", description = "Updates specific fields of an existing song (e.g., changing only the title).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Song updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Song not found.",
                    content = @Content(schema = @Schema(implementation = SingleStringErrorResponseDto.class)))
    })
    @PatchMapping("/{id}")
     ResponseEntity<SongApiDto.InfoResponse> updateSong(@PathVariable Long id,
                                                        @RequestBody SongApiDto.UpdateRequest request) {
        SongDto.Update updateSongDto = mapper.toDomain(request);
        SongDto.Info songDto = facade.updateSongById(id, updateSongDto);
        return ResponseEntity.ok(mapper.toResponse(songDto));
    }

}
