package com.spring.songify.infrastructure.crud.controller.album;

import com.spring.songify.domain.crud.SongifyCrudFacade;
import com.spring.songify.domain.crud.dto.AlbumDto;
import com.spring.songify.domain.crud.dto.AlbumInfo;
import com.spring.songify.domain.crud.dto.AlbumRequestDto;
import com.spring.songify.domain.crud.dto.AlbumSongsDto;
import com.spring.songify.infrastructure.crud.controller.album.dto.request.CreateAlbumRequest;
import com.spring.songify.infrastructure.crud.controller.album.dto.response.AssignAlbumSongResponseDto;
import com.spring.songify.infrastructure.crud.controller.album.dto.response.CreateAlbumResponse;
import com.spring.songify.infrastructure.crud.controller.album.dto.response.GetAlbumDetailsResponse;
import com.spring.songify.infrastructure.crud.controller.album.dto.response.GetAllAlbumsResponseDto;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Tag(name = "3. Albums", description = "Endpoints for managing music albums and their relations with songs.")
@RestController
@AllArgsConstructor
@RequestMapping("/albums")
class AlbumController {
    private final SongifyCrudFacade songifyCrudFacade;
    private final AlbumControllerMapper albumControllerMapper;

    @Operation(summary = "Create a new album", description = "Creates an album and assigns an initial song to it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Album created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping
    ResponseEntity<CreateAlbumResponse> postAlbum(@RequestBody CreateAlbumRequest createAlbumRequest) {
        AlbumRequestDto albumRequestDto = albumControllerMapper.mapFromCreateAlbumRequestToDomainDto(createAlbumRequest);
        AlbumDto albumDto = songifyCrudFacade.addAlbumWithSong(albumRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(albumControllerMapper.mapFromAlbumDtoToCreateAlbumResponse(albumDto));
    }

    @Operation(summary = "Get album by ID", description = "Retrieves full details of an album, including its artists and tracklist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Album found and returned successfully."),
            @ApiResponse(responseCode = "404", description = "Album with the provided ID does not exist.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/{albumId}")
    ResponseEntity<GetAlbumDetailsResponse> getAlbumById(@PathVariable Long albumId) {
        AlbumInfo albumInfo = songifyCrudFacade.findAlbumByIdReturnAlbumInfo(albumId);
        return ResponseEntity.ok(albumControllerMapper.mapFromAlbumInfoToGetAlbumDetailsResponse(albumInfo));
    }

    @Operation(summary = "Get all albums", description = "Returns a paginated list of all albums available in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of albums retrieved successfully (can be empty).")
    })
    @GetMapping
    ResponseEntity<GetAllAlbumsResponseDto> getAllAlbums(@ParameterObject @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Slice<AlbumDto> allAlbumsSlice = songifyCrudFacade.findAllAlbums(pageable);
        return ResponseEntity.ok(albumControllerMapper.mapSliceToGetAllAlbumsResponseDto(allAlbumsSlice));
    }

    @Operation(summary = "Assign song to album", description = "Links an existing song to a specific album.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Song successfully assigned to the album."),
            @ApiResponse(responseCode = "404", description = "Either the album or the song with the given ID was not found.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping("{albumId}/songs/{songId}")
    ResponseEntity<AssignAlbumSongResponseDto> assignSongToAlbum(@PathVariable Long albumId, @PathVariable Long songId) {
        AlbumSongsDto albumSongsDto = songifyCrudFacade.assignSongByIdToAlbumById(albumId, songId);
        return ResponseEntity.ok(albumControllerMapper.mapFromAlbumSongsDtoToAssignAlbumSongResponseDto(albumSongsDto));
    }
}
