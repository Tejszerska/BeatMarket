package com.spring.beatmarket.infrastructure.domain.catalog.controller.album;

import com.spring.beatmarket.domain.catalog.AlbumFacade;
import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import com.spring.beatmarket.infrastructure.error.SingleStringErrorResponseDto;
import com.spring.beatmarket.infrastructure.error.ValidationErrorResponseDto;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "3. Albums", description = "Endpoints for managing music albums and their relations with songs.")
@RestController
@AllArgsConstructor
@RequestMapping("/albums")
class AlbumController {
    private final AlbumFacade facade;
    private final AlbumControllerMapper mapper;


    @Operation(summary = "Get all albums", description = "Returns a paginated list of all albums available in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of albums retrieved successfully (can be empty).")
    })
    @GetMapping
    ResponseEntity<AlbumApiDto.GetAllResponse> searchAlbums(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long artistId,
            @ParameterObject @PageableDefault(size = 20, sort = "editedOn", direction = Sort.Direction.ASC) Pageable pageable) {
        Slice<AlbumDto.Summary> albumsSlice = facade.findAllAlbums(artistId, title, pageable);

        return ResponseEntity.ok(mapper.toGetAllResponse(albumsSlice));
    }

    @GetMapping("/{albumId}")
    ResponseEntity<AlbumApiDto.DetailsResponse> getAlbumById(@PathVariable Long albumId) {
        AlbumDto.Details albumDetails = facade.getAlbumDetails(albumId);
        return ResponseEntity.ok(mapper.toDetailsResponse(albumDetails));
    }

    @Operation(summary = "Create a new album", description = "Creates an album and assigns an initial song to it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Album created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDto.class)))
    })
    @PostMapping
    ResponseEntity<AlbumApiDto.InfoResponse> createAlbum(@RequestBody AlbumApiDto.CreateRequest createAlbumRequest) {
        AlbumDto.Create domainRequest = mapper.toDomainCreate(createAlbumRequest);
        AlbumDto.Info addedAlbum = facade.addAlbum(domainRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toInfoResponse(addedAlbum));
    }

    @Operation(summary = "Partially update album", description = "Updates specific fields of an existing album (e.g., changing only the title).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Album updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Album not found.",
                    content = @Content(schema = @Schema(implementation = SingleStringErrorResponseDto.class)))
    })
    @PatchMapping("/{id}")
    ResponseEntity<AlbumApiDto.InfoResponse> updateSong(@PathVariable Long id,
                                                       @RequestBody AlbumApiDto.UpdateRequest request) {
        AlbumDto.Update updateAlbumDto = mapper.toDomainUpdate(request);
        AlbumDto.Info albumDto = facade.updateAlbum(id, updateAlbumDto);
        return ResponseEntity.ok(mapper.toInfoResponse(albumDto));
    }
}