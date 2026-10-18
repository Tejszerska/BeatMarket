package com.spring.beatmarket.infrastructure.domain.catalog.controller.album;

import com.spring.beatmarket.domain.catalog.AlbumFacade;
import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import com.spring.beatmarket.infrastructure.upload.ImageUploadPolicies;
import com.spring.beatmarket.infrastructure.upload.UploadFileValidator;
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
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "3. Albums", description = "Endpoints for managing music albums and their relations with songs.")
@RestController
@AllArgsConstructor
@RequestMapping("/catalog/albums")
class AlbumController {
    private final AlbumFacade facade;
    private final AlbumControllerMapper mapper;
    private final UploadFileValidator uploadFileValidator;


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

    @Operation(summary = "Get album by ID", description = "Retrieves detailed information about a specific album by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Album found and returned successfully."),
            @ApiResponse(responseCode = "404", description = "Album with the provided ID does not exist.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
    })
    @GetMapping("/{albumId}")
    ResponseEntity<AlbumApiDto.DetailsResponse> getAlbumById(@PathVariable Long albumId) {
        AlbumDto.Details albumDetails = facade.getAlbumDetails(albumId);
        return ResponseEntity.ok(mapper.toDetailsResponse(albumDetails));
    }

    @Operation(summary = "Create a new album", description = "Creates an album and assigns an initial song to it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Album created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    ResponseEntity<AlbumApiDto.InfoResponse> createAlbum(@RequestBody @Valid  AlbumApiDto.CreateRequest createAlbumRequest) {
        AlbumDto.Create domainRequest = mapper.toDomainCreate(createAlbumRequest);
        AlbumDto.Info addedAlbum = facade.addAlbum(domainRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toInfoResponse(addedAlbum));
    }

    @Operation(summary = "Upload album cover", description = "Uploads a JPEG, PNG, or WebP cover image up to 10 MB and 40 megapixels. The image is center-cropped, resized to 150x150 pixels, converted to WebP, and stored in S3.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Album cover uploaded successfully."),
            @ApiResponse(responseCode = "400", description = "The file is empty, corrupt, or not a supported image.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Album not found.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "413", description = "The image exceeds the 10 MB upload limit.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<AlbumApiDto.CoverResponse> uploadAlbumCover(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) throws IOException {
        uploadFileValidator.validate(file, ImageUploadPolicies.ALBUM_COVER);

        AlbumDto.Cover cover = facade.updateAlbumCover(id, file.getBytes());
        return ResponseEntity.ok(mapper.toCoverResponse(cover));
    }

    @Operation(summary = "Partially update album", description = "Updates specific fields of an existing album (e.g., changing only the title).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Album updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Album not found.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PatchMapping("/{id}")
    ResponseEntity<AlbumApiDto.InfoResponse> updateAlbum(@PathVariable Long id,
                                                       @RequestBody @Valid AlbumApiDto.UpdateRequest request) {
        AlbumDto.Update updateAlbumDto = mapper.toDomainUpdate(request);
        AlbumDto.Info albumDto = facade.updateAlbum(id, updateAlbumDto);
        return ResponseEntity.ok(mapper.toInfoResponse(albumDto));
    }


    @Operation(summary = "Delete album", description = "Removes a album from the database by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Album deleted successfully (No Content)."),
            @ApiResponse(responseCode = "404", description = "Album not found.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteAlbum(@PathVariable Long id) {
        facade.deactivateAlbum(id);
        return ResponseEntity.noContent().build();
    }
}