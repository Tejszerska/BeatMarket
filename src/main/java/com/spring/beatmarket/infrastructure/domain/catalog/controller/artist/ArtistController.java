package com.spring.beatmarket.infrastructure.domain.catalog.controller.artist;

import com.spring.beatmarket.domain.catalog.CatalogFacade;
import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import com.spring.beatmarket.infrastructure.error.SingleStringErrorResponseDto;
import com.spring.beatmarket.infrastructure.error.ValidationErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "4. Artists", description = "Endpoints for managing artists, their profiles, and relationships with albums.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/artists")
class ArtistController {

    private final CatalogFacade facade;
    private final ArtistControllerMapper mapper;

    @Operation(summary = "Get all artists", description = "Retrieves a chunked list of available artists.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of artists retrieved successfully.")
    })
    @GetMapping
    ResponseEntity<ArtistApiDto.GetAllResponse> getAllArtists(
            @RequestParam(required = false) String name,
            @ParameterObject @PageableDefault(size = 20, sort = "editedOn", direction = Sort.Direction.ASC) Pageable pageable) {
        Slice<ArtistDto.Summary> artistsSlice = facade.findAllArtists(name, pageable);
        return ResponseEntity.ok(mapper.toGetAllResponse(artistsSlice));
    }

    // @TODO Expand logic - implement missing GET by ID from api-contracts
    @Operation(summary = "Get artist by ID", description = "Retrieves detailed information about a specific artist by their ID, including their albums and songs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artist found and returned successfully."),
            @ApiResponse(responseCode = "404", description = "Artist with the provided ID does not exist.",
                    content = @Content(schema = @Schema(implementation = SingleStringErrorResponseDto.class)))
    })
    @GetMapping("/{artistId}")
    ResponseEntity<ArtistApiDto.DetailsResponse> getArtistById(@PathVariable Long artistId) {
        ArtistDto.Details dto = facade.getArtistDetails(artistId);
        return ResponseEntity.ok(mapper.toDetailsResponse(dto));
    }

    @Operation(summary = "Create a new artist", description = "Adds a new artist to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Artist created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Song or Album not found.",
                    content = @Content(schema = @Schema(implementation = SingleStringErrorResponseDto.class)))
    })
    @PostMapping
    ResponseEntity<ArtistApiDto.InfoResponse> createArtist(@Valid @RequestBody ArtistApiDto.CreateRequest request) {
        ArtistDto.Info createdArtist = facade.addArtist(mapper.toDomainCreate(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toWebInfo(createdArtist));
    }

    // @TODO expand logic - implement deletion strategy from docs
    @Operation(summary = "Delete artist", description = "Removes an artist from the database by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Artist deleted successfully (No Content)."),
            @ApiResponse(responseCode = "404", description = "Artist not found.",
                    content = @Content(schema = @Schema(implementation = SingleStringErrorResponseDto.class)))
    })
    @DeleteMapping("/{artistId}")
    ResponseEntity<Void> deleteArtist(@PathVariable Long artistId) {
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update artist details", description = "Partially updates an existing artist's metadata and its relationships.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artist successfully updated."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Song, Album, or Artist(s) not found.",
                    content = @Content(schema = @Schema(implementation = SingleStringErrorResponseDto.class)))
    })
    @PatchMapping("/{id}")
    ResponseEntity<ArtistApiDto.InfoResponse> updateArtist(
            @PathVariable Long id,
            @Valid @RequestBody ArtistApiDto.UpdateRequest request) {
        ArtistDto.Update domainUpdate = mapper.toDomainUpdate(request);
        ArtistDto.Info updatedArtist = facade.updateArtist(id, domainUpdate);
        return ResponseEntity.ok(mapper.toWebInfo(updatedArtist));
    }

    // @TODO check logic, AI refactored
    @Operation(summary = "Assign artist to album", description = "Assigns an existing album to the specified artist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Artist successfully updated."),
            @ApiResponse(responseCode = "404", description = "Album or Artist not found.",
                    content = @Content(schema = @Schema(implementation = SingleStringErrorResponseDto.class)))
    })
    @PutMapping("/{artistId}/albums/{albumId}")
    ResponseEntity<Void> addArtistToAlbum(
            @PathVariable Long artistId,
            @PathVariable Long albumId) {
        // facade.addArtistToAlbum(artistId, albumId);
        return ResponseEntity.noContent().build();
    }
}