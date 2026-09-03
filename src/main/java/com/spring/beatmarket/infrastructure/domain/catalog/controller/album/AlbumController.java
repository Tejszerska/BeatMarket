package com.spring.beatmarket.infrastructure.domain.catalog.controller.album;

import com.spring.beatmarket.domain.catalog.AlbumFacade;
import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.album.dto.response.CreateAlbumResponse;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.album.dto.response.GetAlbumDetailsResponse;
import com.spring.beatmarket.infrastructure.error.ErrorResponseDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    ResponseEntity<GetAlbumDetailsResponse> getAlbumById(@PathVariable Long albumId) {
//        AlbumInfo albumInfo = facade.findAlbumByIdReturnAlbumInfo(albumId);
//        return ResponseEntity.ok(albumControllerMapper.mapFromAlbumInfoToGetAlbumDetailsResponse(albumInfo));
        return null;
    }

    @Operation(summary = "Create a new album", description = "Creates an album and assigns an initial song to it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Album created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping
    ResponseEntity<CreateAlbumResponse> postAlbum(@RequestBody AlbumApiDto.CreateRequest createAlbumRequest) {
//        LegacyAlbumDto legacyAlbumDto = facade.addAlbum(albumRequestDto);
//        return ResponseEntity.status(HttpStatus.CREATED).body(albumControllerMapper.mapFromAlbumDtoToCreateAlbumResponse(legacyAlbumDto));
        return null;
    }
}
