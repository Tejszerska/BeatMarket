package com.spring.beatmarket.infrastructure.domain.catalog.controller.song;

import com.spring.beatmarket.domain.catalog.CatalogFacade;
import com.spring.beatmarket.domain.catalog.dto.SongDetailsDto;
import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.domain.catalog.dto.SongDtoOld;
import com.spring.beatmarket.domain.catalog.dto.SongRequestDto;
import com.spring.beatmarket.domain.catalog.dto.SongSearchCriteria;
import com.spring.beatmarket.domain.catalog.dto.SongSummaryDto;
import com.spring.beatmarket.domain.catalog.dto.UpdateSongDto;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.request.CreateSongRequest;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.request.SongSearchRequestDto;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.request.UpdateSongRequest;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.response.AssignGenreToSongResponseDto;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.response.GetAllSongsResponse;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.response.SongDetailsResponse;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.response.SongResponse;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "2. Songs", description = "Endpoints for managing songs, their details, and genre assignments.")
@RestController
@Log4j2
@RequestMapping("/catalog/songs")
@AllArgsConstructor
public
class SongController {

    private final CatalogFacade songFacade;
    private final SongControllerMapper songControllerMapper;

    @Operation(summary = "Get all songs", description = "Returns a paginated list of all songs in the database, with an option to limit results.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of songs retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid query parameters.")

    })
    @GetMapping
    ResponseEntity<GetAllSongsResponse> getAllSongs(
            SongSearchRequestDto searchRequestDto,
            @ParameterObject @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        if(searchRequestDto.maxPrice() != null){
            if(searchRequestDto.currency() == null || searchRequestDto.license() == null){
                throw new InvalidSearchCriteriaException("maxPrice",
                        "Filtering by maxPrice requires declaring currency and license.");
            }
        }
        SongSearchCriteria songSearchCriteria = songControllerMapper.mapFromSearchRequestToDomain(searchRequestDto);
        Slice<SongSummaryDto> allSongs = songFacade.findAllSongs(songSearchCriteria, pageable);

        return ResponseEntity.ok(songControllerMapper.mapFromSongToGetAllSongsResponseDto(allSongs));
    }

    @Operation(summary = "Get song by ID", description = "Retrieves detailed information about a specific song by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Song found and returned successfully."),
            @ApiResponse(responseCode = "404", description = "Song with the provided ID does not exist.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @GetMapping("/{id}")
    ResponseEntity<SongDetailsResponse> getSongById(@PathVariable Long id) {
        SongDetailsDto songDetails = songFacade.getSongDetailsById(id);
        return ResponseEntity.ok(songControllerMapper.mapFromDomainToSongDetailsResponse(songDetails));
    }

    @Operation(summary = "Create a new song", description = "Adds a new song to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Song created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., negative duration).",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @PostMapping
    ResponseEntity<SongResponse> postSong(@RequestBody @Valid CreateSongRequest createSongRequest) {
        SongRequestDto domainRequest = songControllerMapper.mapFromCreateSongRequestToDomainRequest(createSongRequest);
        SongDto savedSong = songFacade.addSong(domainRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(songControllerMapper.mapFromDomainToResponse(savedSong));
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
     ResponseEntity<SongResponse> updateSong(@PathVariable Long id,
                                                    @RequestBody UpdateSongRequest request) {
        UpdateSongDto updateSongDto = songControllerMapper.mapUpdateRequestToDto(request);
        SongDto songDto = songFacade.updateSongById(id, updateSongDto);
        return ResponseEntity.ok(songControllerMapper.mapFromDomainToResponse(songDto));
    }

    @Operation(summary = "Assign genre to song", description = "Links an existing musical genre to a specific song.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Genre successfully assigned to the song."),
            @ApiResponse(responseCode = "404", description = "Song or Genre not found.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping("/{songId}/genre/{genreId}")
    ResponseEntity<AssignGenreToSongResponseDto> assignGenreByIdToSongById(@PathVariable Long songId, @PathVariable Long genreId) {
        SongDtoOld songDtoOld = songFacade.assignGenreByIdToSongById(songId, genreId);
        return ResponseEntity.ok(songControllerMapper.mapFromSongDtoToAssignGenreToSongResponseDto(songDtoOld));
    }
}
