package com.spring.songify.infrastructure.controller.crud.song.controller.album;

import com.spring.songify.domain.crud.SongifyCrudFacade;
import com.spring.songify.domain.crud.dto.AlbumDto;
import com.spring.songify.domain.crud.dto.AlbumInfo;
import com.spring.songify.domain.crud.dto.AlbumRequestDto;
import com.spring.songify.domain.crud.dto.AlbumSongsDto;
import com.spring.songify.domain.crud.dto.AlbumWithArtistsAndSongsDto;
import com.spring.songify.infrastructure.controller.crud.song.controller.album.response.GetAllAlbumsResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/albums")
class AlbumController {
    private final SongifyCrudFacade songifyCrudFacade;

    @PostMapping
    ResponseEntity<AlbumDto> postAlbum(@RequestBody AlbumRequestDto albumRequestDto) {
        AlbumDto albumDto = songifyCrudFacade.addAlbumWithSong(albumRequestDto);
        return ResponseEntity.ok(albumDto);
    }

    @GetMapping("/v2/{albumId}")
    ResponseEntity<AlbumWithArtistsAndSongsDto> getAlbumsWithArtistsAndSongs(@PathVariable Long albumId) {
        AlbumWithArtistsAndSongsDto albumByIdWithArtistsAndSongs = songifyCrudFacade.findAlbumByIdWithArtistsAndSongs(albumId);
        return ResponseEntity.ok(albumByIdWithArtistsAndSongs);
    }

    @GetMapping("/{albumId}")
    ResponseEntity<AlbumInfo> getAlbumsReturnAlbumInfo(@PathVariable Long albumId) {
        AlbumInfo albumByReturnAlbumInfo = songifyCrudFacade.findAlbumByIdReturnAlbumInfo(albumId);
        return ResponseEntity.ok(albumByReturnAlbumInfo);
    }

    @GetMapping
    ResponseEntity<GetAllAlbumsResponseDto> getAllAlbums(Pageable pageable) {
        Slice<AlbumDto> allAlbumsSlice = songifyCrudFacade.findAllAlbums(pageable);
        GetAllAlbumsResponseDto getAllAlbumsResponseDto = AlbumControllerMapper.mapSliceToGetAllAlbumsResponseDto(allAlbumsSlice);
        return ResponseEntity.ok(getAllAlbumsResponseDto);
    }

    @PutMapping("{albumId}/songs/{songId}")
    ResponseEntity<AlbumSongsDto> assignSongToAlbum(@PathVariable Long albumId, @PathVariable Long songId) {
        AlbumSongsDto albumSongsDto = songifyCrudFacade.assignSongByIdToAlbumById(albumId, songId);
        return ResponseEntity.ok(albumSongsDto);
    }
}
