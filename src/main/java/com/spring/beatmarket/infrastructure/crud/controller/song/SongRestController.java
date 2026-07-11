package com.spring.beatmarket.infrastructure.crud.controller.song;

import com.spring.beatmarket.domain.crud.BeatMarketCrudFacade;
import com.spring.beatmarket.domain.crud.dto.SongDto;
import com.spring.beatmarket.domain.crud.dto.SongRequestDto;
import com.spring.beatmarket.infrastructure.crud.controller.song.dto.request.CreateSongRequestDto;
import com.spring.beatmarket.infrastructure.crud.controller.song.dto.request.PartiallyUpdateSongRequestDto;
import com.spring.beatmarket.infrastructure.crud.controller.song.dto.response.AssignGenreToSongResponseDto;
import com.spring.beatmarket.infrastructure.crud.controller.song.dto.response.CreateSongResponseDto;
import com.spring.beatmarket.infrastructure.crud.controller.song.dto.response.GetAllSongsResponseDto;
import com.spring.beatmarket.infrastructure.crud.controller.song.dto.response.GetSongResponseDto;
import com.spring.beatmarket.infrastructure.crud.controller.song.dto.response.PartiallyUpdateSongResponseDto;
import com.spring.beatmarket.infrastructure.error.ErrorResponseDto;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "6. Songs", description = "Endpoints for managing songs, their details, and genre assignments.")
@RestController
@Log4j2
@RequestMapping("/songs")
@AllArgsConstructor
public
class SongRestController {

    private final BeatMarketCrudFacade songFacade;
    private final SongControllerMapper songControllerMapper;

    @Operation(summary = "Get all songs", description = "Returns a paginated list of all songs in the database, with an option to limit results.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of songs retrieved successfully.")
    })
    @GetMapping
    ResponseEntity<GetAllSongsResponseDto> getAllSongs(
            @ParameterObject @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Slice<SongDto> allSongs = songFacade.findAllSongs(pageable);
        return ResponseEntity.ok(songControllerMapper.mapFromSongToGetAllSongsResponseDto(allSongs));
    }

    @Operation(summary = "Get song by ID", description = "Retrieves detailed information about a specific song by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Song found and returned successfully."),
            @ApiResponse(responseCode = "404", description = "Song with the provided ID does not exist.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @GetMapping("/{id}")
    ResponseEntity<GetSongResponseDto> getSongById(@PathVariable Long id, @RequestHeader(required = false) String requestId) {
        log.info(requestId);
        SongDto song = songFacade.findSongDtoById(id);
        return ResponseEntity.ok(songControllerMapper.mapFromSongToGetSongResponseDto(song));
    }

    @Operation(summary = "Create a new song", description = "Adds a new song to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Song created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., negative duration).",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @PostMapping
    ResponseEntity<CreateSongResponseDto> postSong(@RequestBody @Valid CreateSongRequestDto createSongRequestDto) {
        SongRequestDto domainRequest = songControllerMapper.mapFromSongCreateSongRequestDtoToDomainRequest(createSongRequestDto);
        SongDto savedSong = songFacade.addSong(domainRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(songControllerMapper.mapFromSongDtoToCreateSongResponseDto(savedSong));
    }

    @Operation(summary = "Delete song", description = "Removes a song from the database by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Song deleted successfully (No Content).",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Song not found.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteSongByIdUsingPathVariable(@PathVariable Long id) {
        songFacade.deleteSongById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Partially update song", description = "Updates specific fields of an existing song (e.g., changing only the title).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Song updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Song not found.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PatchMapping("/{id}")
    ResponseEntity<PartiallyUpdateSongResponseDto> partiallyUpdateSong(@PathVariable Long id,
                                                                       @RequestBody PartiallyUpdateSongRequestDto request) {
        SongDto updatedSong = songControllerMapper.mapFromPartiallyUpdateSongRequestDtoToSong(request);
        SongDto savedSong = songFacade.updateSongPartiallyById(id, updatedSong);
        return ResponseEntity.ok(songControllerMapper.mapFromSongDtoToPartiallyUpdateSongResponseDto(savedSong));
    }

    @Operation(summary = "Assign genre to song", description = "Links an existing musical genre to a specific song.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Genre successfully assigned to the song."),
            @ApiResponse(responseCode = "404", description = "Song or Genre not found.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping("/{songId}/genre/{genreId}")
    ResponseEntity<AssignGenreToSongResponseDto> assignGenreByIdToSongById(@PathVariable Long songId, @PathVariable Long genreId) {
        SongDto songDto = songFacade.assignGenreByIdToSongById(songId, genreId);
        return ResponseEntity.ok(songControllerMapper.mapFromSongDtoToAssignGenreToSongResponseDto(songDto));
    }
}
