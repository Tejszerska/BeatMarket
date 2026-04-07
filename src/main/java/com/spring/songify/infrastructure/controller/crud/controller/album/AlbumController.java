package com.spring.songify.infrastructure.controller.crud.controller.album;

import com.spring.songify.domain.crud.SongifyCrudFacade;
import com.spring.songify.domain.crud.dto.AlbumDto;
import com.spring.songify.domain.crud.dto.AlbumInfo;
import com.spring.songify.domain.crud.dto.AlbumRequestDto;
import com.spring.songify.domain.crud.dto.AlbumSongsDto;
import com.spring.songify.infrastructure.controller.crud.controller.album.dto.request.CreateAlbumRequest;
import com.spring.songify.infrastructure.controller.crud.controller.album.dto.response.AssignAlbumSongResponseDto;
import com.spring.songify.infrastructure.controller.crud.controller.album.dto.response.CreateAlbumResponse;
import com.spring.songify.infrastructure.controller.crud.controller.album.dto.response.GetAlbumDetailsResponse;
import com.spring.songify.infrastructure.controller.crud.controller.album.dto.response.GetAllAlbumsResponseDto;
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
    ResponseEntity<CreateAlbumResponse> postAlbum(@RequestBody CreateAlbumRequest createAlbumRequest) {
        AlbumRequestDto albumRequestDto = AlbumControllerMapper.mapFromCreateAlbumRequestToDomainDto(createAlbumRequest);
        AlbumDto albumDto = songifyCrudFacade.addAlbumWithSong(albumRequestDto);
        CreateAlbumResponse createAlbumResponse = AlbumControllerMapper.mapFromAlbumDtoToCreateAlbumResponse(albumDto);
        return ResponseEntity.ok(createAlbumResponse);
    }

    @GetMapping("/{albumId}")
    ResponseEntity<GetAlbumDetailsResponse> getAlbumById(@PathVariable Long albumId) {
        AlbumInfo albumInfo = songifyCrudFacade.findAlbumByIdReturnAlbumInfo(albumId);
        GetAlbumDetailsResponse getAlbumDetailsResponse = AlbumControllerMapper.mapFromAlbumInfoToGetAlbumDetailsResponse(albumInfo);
        return ResponseEntity.ok(getAlbumDetailsResponse);
    }

    @GetMapping
    ResponseEntity<GetAllAlbumsResponseDto> getAllAlbums(@ParameterObject @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Slice<AlbumDto> allAlbumsSlice = songifyCrudFacade.findAllAlbums(pageable);
        GetAllAlbumsResponseDto getAllAlbumsResponseDto = AlbumControllerMapper.mapSliceToGetAllAlbumsResponseDto(allAlbumsSlice);
        return ResponseEntity.ok(getAllAlbumsResponseDto);
    }

    @PutMapping("{albumId}/songs/{songId}")
    ResponseEntity<AssignAlbumSongResponseDto> assignSongToAlbum(@PathVariable Long albumId, @PathVariable Long songId) {
        AlbumSongsDto albumSongsDto = songifyCrudFacade.assignSongByIdToAlbumById(albumId, songId);
        AssignAlbumSongResponseDto assignAlbumSongResponseDto = AlbumControllerMapper.mapFromAlbumSongsDtoToAssignAlbumSongResponseDto(albumSongsDto);
        return ResponseEntity.ok(assignAlbumSongResponseDto);
    }
}
