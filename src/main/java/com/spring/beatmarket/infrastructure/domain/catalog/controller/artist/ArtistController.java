package com.spring.beatmarket.infrastructure.domain.catalog.controller.artist;

import com.spring.beatmarket.domain.catalog.CatalogFacade;
import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import com.spring.beatmarket.infrastructure.error.ErrorResponseDto;
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

    @Operation(summary = "Get all artists")
    @GetMapping
    ResponseEntity<ArtistApiDto.GetAllResponse> getAllArtists(
            @RequestParam(required = false) String name,
            @ParameterObject @PageableDefault(size = 20, sort = "editedOn", direction = Sort.Direction.ASC) Pageable pageable) {
        Slice<ArtistDto.Summary> artistsSlice = facade.findAllArtists(name, pageable);
        return ResponseEntity.ok(mapper.toGetAllResponse(artistsSlice));
    }



    @Operation(summary = "Create a new artist")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Artist created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping
    ResponseEntity<ArtistApiDto.InfoResponse> createArtist(@Valid @RequestBody ArtistApiDto.CreateRequest request) {
        ArtistDto.Info createdArtist = facade.addArtist(mapper.toDomainCreate(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toWebInfo(createdArtist));
    }



    // @TODO expand logic - implement deletion strategy from docs
    @Operation(summary = "Delete artist")
    @DeleteMapping("/{artistId}")
    ResponseEntity<Void> deleteArtist(@PathVariable Long artistId) {

        return ResponseEntity.noContent().build();
    }

    // @TODO expand logic -should update more thank just name now
    @Operation(summary = "Update artist's name")
    @PatchMapping("/{artistId}")
    ResponseEntity<ArtistApiDto.InfoResponse> updateArtistName(
            @PathVariable Long artistId,
            @Valid @RequestBody ArtistApiDto.UpdateRequest request) {

        ArtistDto.Info updatedArtist = facade.updateArtistNameById(artistId, request.name());
        return ResponseEntity.ok(mapper.toWebInfo(updatedArtist));
    }

//    // @TODO check logic, AI refactored
//    @Operation(summary = "Add artist to album")
//    @PutMapping("/{artistId}/albums/{albumId}")
//    ResponseEntity<ArtistApiDto.Details> addArtistToAlbum(
//            @PathVariable Long artistId,
//            @PathVariable Long albumId) {
//
//        ArtistDto.Details linkedArtist = facade.addArtistToAlbum(artistId, albumId);
//        return ResponseEntity.ok(mapper.toWebDetails(linkedArtist));
//    }
}