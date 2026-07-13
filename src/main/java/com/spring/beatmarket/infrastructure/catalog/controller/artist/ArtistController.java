package com.spring.beatmarket.infrastructure.catalog.controller.artist;

import com.spring.beatmarket.domain.catalog.BeatMarketCrudFacade;
import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import com.spring.beatmarket.domain.catalog.dto.ArtistRequestDto;
import com.spring.beatmarket.domain.catalog.dto.ArtistWithAlbumDto;
import com.spring.beatmarket.infrastructure.catalog.controller.artist.dto.request.ArtistUpdateRequestDto;
import com.spring.beatmarket.infrastructure.catalog.controller.artist.dto.request.CreateArtistRequest;
import com.spring.beatmarket.infrastructure.catalog.controller.artist.dto.request.CreateArtistWithDefaultAlbumAndSongRequest;
import com.spring.beatmarket.infrastructure.catalog.controller.artist.dto.response.ArtistUpdateNameResponseDto;
import com.spring.beatmarket.infrastructure.catalog.controller.artist.dto.response.ArtistWithAlbumResponseDto;
import com.spring.beatmarket.infrastructure.catalog.controller.artist.dto.response.CreateArtistResponse;
import com.spring.beatmarket.infrastructure.catalog.controller.artist.dto.response.CreateArtistWithDefaultAlbumAndSongResponse;
import com.spring.beatmarket.infrastructure.catalog.controller.artist.dto.response.GetAllArtistsResponseDto;
import com.spring.beatmarket.infrastructure.error.ErrorResponseDto;
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

@Tag(name = "4. Artists", description = "Endpoints for managing artists, their profiles, and relationships with albums.")
@RestController
@AllArgsConstructor
@RequestMapping("/artists")
class ArtistController {
    private final BeatMarketCrudFacade beatmarketCrudFacade;
    private final ArtistControllerMapper artistControllerMapper;

    @Operation(summary = "Create a new artist", description = "Adds a new artist to the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Artist created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., blank name).",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping
    ResponseEntity<CreateArtistResponse> postArtist(@RequestBody CreateArtistRequest createArtistRequest) {
        ArtistRequestDto artistRequestDto = artistControllerMapper.mapFromCreateArtistRequestToDomainDto(createArtistRequest);
        ArtistDto artistDto = beatmarketCrudFacade.addArtist(artistRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(artistControllerMapper.mapFromArtistDtoToCreateArtistResponse(artistDto));
    }

    @Operation(summary = "Get all artists", description = "Returns a paginated list of all artists.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of artists retrieved successfully.")
    })
    @GetMapping
    ResponseEntity<GetAllArtistsResponseDto> getAllArtists(@ParameterObject @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Slice<ArtistDto> allArtistsSlice = beatmarketCrudFacade.findAllArtists(pageable);
        return ResponseEntity.ok(artistControllerMapper.mapSliceToGetAllArtistsResponseDto(allArtistsSlice));
    }

    @Operation(summary = "Delete artist", description = "Removes an artist from the database by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artist deleted successfully."),
            @ApiResponse(responseCode = "404", description = "Artist not found.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @DeleteMapping("/{artistId}")
    ResponseEntity<Void> deleteArtistByIdWithAlbumsAndSongs(@PathVariable Long artistId) {
        beatmarketCrudFacade.deleteArtistByIdWithAlbumsAndSongs(artistId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add artist to album", description = "Links an existing artist to an existing album.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artist successfully linked to the album."),
            @ApiResponse(responseCode = "404", description = "Artist or Album not found.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping("/{artistId}/albums/{albumId}")
    ResponseEntity<ArtistWithAlbumResponseDto> addArtistToAlbum(@PathVariable Long artistId, @PathVariable Long albumId) {
        ArtistWithAlbumDto artistWithAlbumDto = beatmarketCrudFacade.addArtistToAlbum(artistId, albumId);
        return ResponseEntity.ok(artistControllerMapper.mapFromDomainDtoToArtistWithAlbumResponseDto(artistWithAlbumDto));
    }

    @Operation(summary = "Update artist's name", description = "Changes the name of an existing artist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artist's name updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid name provided.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Artist not found.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PatchMapping("/{artistId}")
    ResponseEntity<ArtistUpdateNameResponseDto> updateArtistNameById(@PathVariable Long artistId,
                                                                     @Valid @RequestBody ArtistUpdateRequestDto updateRequestDto) {
        ArtistDto artistDto = beatmarketCrudFacade.updateArtistNameById(artistId, updateRequestDto.name());
        return ResponseEntity.ok(artistControllerMapper.mapFromArtistDtoToArtistUpdateNameResponseDto(artistDto));
    }

    @Operation(summary = "Create artist with default album and song", description = "Quickly creates a new artist along with a default placeholder album and song.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Artist, default album, and default song created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/default")
    ResponseEntity<CreateArtistWithDefaultAlbumAndSongResponse> addArtistWithDefaultAlbumAndSong(@RequestBody CreateArtistWithDefaultAlbumAndSongRequest userRequest) {
        ArtistRequestDto requestDto = artistControllerMapper.mapFromCreateArtistWithDefaultAlbumAndSongRequestToDomainDto(userRequest);
        ArtistDto artistDto = beatmarketCrudFacade.addArtistWithDefaultAlbumAndSong(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(artistControllerMapper.mapFromArtistDtoToCreateArtistWithDefaultAlbumAndSongResponse(artistDto));
    }
}
