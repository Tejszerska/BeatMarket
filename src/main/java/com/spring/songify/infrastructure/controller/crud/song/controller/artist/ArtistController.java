package com.spring.songify.infrastructure.controller.crud.song.controller.artist;

import com.spring.songify.domain.crud.SongifyCrudFacade;
import com.spring.songify.domain.crud.dto.ArtistDto;
import com.spring.songify.domain.crud.dto.ArtistRequestDto;
import com.spring.songify.infrastructure.controller.crud.song.controller.artist.dto.response.GetAllArtistsResponseDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

    @PostMapping
    ResponseEntity<ArtistDto> postArtist(@RequestBody ArtistRequestDto artistRequestDto) {
        ArtistDto artistDto = songifyCrudFacade.addArtist(artistRequestDto);
        return ResponseEntity.ok(artistDto);
    }

    @GetMapping
    ResponseEntity<GetAllArtistsResponseDto> getAllArtists(Pageable pageable) {
        Slice<ArtistDto> allArtistsSlice = songifyCrudFacade.findAllArtists(pageable);
        GetAllArtistsResponseDto getAllArtistsResponseDto = ArtistControllerMapper.mapSliceToGetAllArtistsResponseDto(allArtistsSlice);
        return ResponseEntity.ok(getAllArtistsResponseDto);
    }

    @DeleteMapping("/{artistId}")
    ResponseEntity<String> deleteArtistByIdWithAlbumsAndSongs(@PathVariable Long artistId) {
        songifyCrudFacade.deleteArtistByIdWithAlbumsAndSongs(artistId);
        return ResponseEntity.ok("Artist, album, songs deleted... probably ");
    }

    @PutMapping("/{artistId}/albums/{albumId}")
    ResponseEntity<String> addArtistToAlbum(@PathVariable Long artistId, @PathVariable Long albumId) {
        songifyCrudFacade.addArtistToAlbum(artistId, albumId);
        return ResponseEntity.ok("Artist added to album - to be tested!");
    }

    @PatchMapping("/{artistId}")
    ResponseEntity<ArtistDto> updateArtistNameById(@PathVariable Long artistId,
                                                   @Valid @RequestBody ArtistUpdateRequestDto updateRequestDto) {
        ArtistDto artistDto = songifyCrudFacade.updateArtistNameById(artistId, updateRequestDto.newArtistName());
        return ResponseEntity.ok(artistDto);
    }

    @PostMapping("/default")
    ResponseEntity<ArtistDto> addArtistWithDefaultAlbumAndSong(@RequestBody ArtistRequestDto requestDto) {
        ArtistDto artistDto = songifyCrudFacade.addArtistWithDefaultAlbumAndSong(requestDto);
        return ResponseEntity.ok(artistDto);
    }
}
