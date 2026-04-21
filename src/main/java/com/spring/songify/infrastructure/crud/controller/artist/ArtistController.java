package com.spring.songify.infrastructure.crud.controller.artist;

import com.spring.songify.domain.crud.SongifyCrudFacade;
import com.spring.songify.domain.crud.dto.ArtistDto;
import com.spring.songify.domain.crud.dto.ArtistRequestDto;
import com.spring.songify.domain.crud.dto.ArtistWithAlbumDto;
import com.spring.songify.infrastructure.crud.controller.artist.dto.request.ArtistUpdateRequestDto;
import com.spring.songify.infrastructure.crud.controller.artist.dto.request.CreateArtistRequest;
import com.spring.songify.infrastructure.crud.controller.artist.dto.request.CreateArtistWithDefaultAlbumAndSongRequest;
import com.spring.songify.infrastructure.crud.controller.artist.dto.response.ArtistUpdateNameResponseDto;
import com.spring.songify.infrastructure.crud.controller.artist.dto.response.ArtistWithAlbumResponseDto;
import com.spring.songify.infrastructure.crud.controller.artist.dto.response.CreateArtistResponse;
import com.spring.songify.infrastructure.crud.controller.artist.dto.response.CreateArtistWithDefaultAlbumAndSongResponse;
import com.spring.songify.infrastructure.crud.controller.artist.dto.response.GetAllArtistsResponseDto;
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

@RestController
@AllArgsConstructor
@RequestMapping("/artists")
class ArtistController {
    private final SongifyCrudFacade songifyCrudFacade;
    private final ArtistControllerMapper artistControllerMapper;

    @PostMapping
    ResponseEntity<CreateArtistResponse> postArtist(@RequestBody CreateArtistRequest createArtistRequest) {
        ArtistRequestDto artistRequestDto = artistControllerMapper.mapFromCreateArtistRequestToDomainDto(createArtistRequest);
        ArtistDto artistDto = songifyCrudFacade.addArtist(artistRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(artistControllerMapper.mapFromArtistDtoToCreateArtistResponse(artistDto));
    }

    @GetMapping
    ResponseEntity<GetAllArtistsResponseDto> getAllArtists(@ParameterObject @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Slice<ArtistDto> allArtistsSlice = songifyCrudFacade.findAllArtists(pageable);
        return ResponseEntity.ok(artistControllerMapper.mapSliceToGetAllArtistsResponseDto(allArtistsSlice));
    }

    @DeleteMapping("/{artistId}")
    ResponseEntity<Void> deleteArtistByIdWithAlbumsAndSongs(@PathVariable Long artistId) {
        songifyCrudFacade.deleteArtistByIdWithAlbumsAndSongs(artistId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{artistId}/albums/{albumId}")
    ResponseEntity<ArtistWithAlbumResponseDto> addArtistToAlbum(@PathVariable Long artistId, @PathVariable Long albumId) {
        ArtistWithAlbumDto artistWithAlbumDto = songifyCrudFacade.addArtistToAlbum(artistId, albumId);
        return ResponseEntity.ok(artistControllerMapper.mapFromDomainDtoToArtistWithAlbumResponseDto(artistWithAlbumDto));
    }

    @PatchMapping("/{artistId}")
    ResponseEntity<ArtistUpdateNameResponseDto> updateArtistNameById(@PathVariable Long artistId,
                                                                     @Valid @RequestBody ArtistUpdateRequestDto updateRequestDto) {
        ArtistDto artistDto = songifyCrudFacade.updateArtistNameById(artistId, updateRequestDto.name());
        return ResponseEntity.ok(artistControllerMapper.mapFromArtistDtoToArtistUpdateNameResponseDto(artistDto));
    }

    @PostMapping("/default")
    ResponseEntity<CreateArtistWithDefaultAlbumAndSongResponse> addArtistWithDefaultAlbumAndSong(@RequestBody CreateArtistWithDefaultAlbumAndSongRequest userRequest) {
        ArtistRequestDto requestDto = artistControllerMapper.mapFromCreateArtistWithDefaultAlbumAndSongRequestToDomainDto(userRequest);
        ArtistDto artistDto = songifyCrudFacade.addArtistWithDefaultAlbumAndSong(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(artistControllerMapper.mapFromArtistDtoToCreateArtistWithDefaultAlbumAndSongResponse(artistDto));
    }
}
