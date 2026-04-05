package com.spring.songify.infrastructure.controller.crud.controller.song;

import com.spring.songify.domain.crud.SongifyCrudFacade;
import com.spring.songify.domain.crud.dto.SongDto;
import com.spring.songify.domain.crud.dto.SongRequestDto;
import com.spring.songify.infrastructure.controller.crud.controller.song.dto.request.CreateSongRequestDto;
import com.spring.songify.infrastructure.controller.crud.controller.song.dto.request.PartiallyUpdateSongRequestDto;
import com.spring.songify.infrastructure.controller.crud.controller.song.dto.request.UpdateSongRequestDto;
import com.spring.songify.infrastructure.controller.crud.controller.song.dto.response.AssignGenreToSongResponseDto;
import com.spring.songify.infrastructure.controller.crud.controller.song.dto.response.CreateSongResponseDto;
import com.spring.songify.infrastructure.controller.crud.controller.song.dto.response.DeleteSongResponseDto;
import com.spring.songify.infrastructure.controller.crud.controller.song.dto.response.GetAllSongsResponseDto;
import com.spring.songify.infrastructure.controller.crud.controller.song.dto.response.GetSongResponseDto;
import com.spring.songify.infrastructure.controller.crud.controller.song.dto.response.PartiallyUpdateSongResponseDto;
import com.spring.songify.infrastructure.controller.crud.controller.song.dto.response.UpdateSongResponseDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

@RestController
@Log4j2
@RequestMapping("/songs")
@AllArgsConstructor
public
class SongRestController {

    private final SongifyCrudFacade songFacade;

    @GetMapping
    ResponseEntity<GetAllSongsResponseDto> getAllSongs(
            @ParameterObject @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Slice<SongDto> allSongs = songFacade.findAllSongs(pageable);
        GetAllSongsResponseDto response = SongControllerMapper.mapFromSongToGetAllSongsResponseDto(allSongs);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    ResponseEntity<GetSongResponseDto> getSongById(@PathVariable Long id, @RequestHeader(required = false) String requestId) {
        log.info(requestId);
        SongDto song = songFacade.findSongDtoById(id);
        GetSongResponseDto response = SongControllerMapper.mapFromSongToGetSongResponseDto(song);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    ResponseEntity<CreateSongResponseDto> postSong(@RequestBody @Valid CreateSongRequestDto createSongRequestDto) {
        SongRequestDto domainRequest = SongControllerMapper.mapFromSongCreateSongRequestDtoToDomainRequest(createSongRequestDto);
        SongDto savedSong = songFacade.addSong(domainRequest);
        CreateSongResponseDto createSongResponseDto = SongControllerMapper.mapFromSongDtoToCreateSongResponseDto(savedSong);
        return ResponseEntity.ok(createSongResponseDto);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<DeleteSongResponseDto> deleteSongByIdUsingPathVariable(@PathVariable Long id) {
        songFacade.deleteSongById(id);
        DeleteSongResponseDto body = SongControllerMapper.mapFromSongToDeleteSongResponseDto(id);
        return ResponseEntity.ok(body);
    }

    @PutMapping("/{id}")
    ResponseEntity<UpdateSongResponseDto> update(@PathVariable Long id,
                                                 @RequestBody @Valid UpdateSongRequestDto request) {
        SongDto newSongDto = SongControllerMapper.mapFromUpdateSongRequestDtoToSongDto(request);
        songFacade.updateSongById(id, newSongDto);
        UpdateSongResponseDto body = SongControllerMapper.mapFromSongToUpdateSongResponseDto(newSongDto);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{id}")
    ResponseEntity<PartiallyUpdateSongResponseDto> partiallyUpdateSong(@PathVariable Long id,
                                                                       @RequestBody PartiallyUpdateSongRequestDto request) {
        SongDto updatedSong = SongControllerMapper.mapFromPartiallyUpdateSongRequestDtoToSong(request);
        SongDto savedSong = songFacade.updateSongPartiallyById(id, updatedSong);
        PartiallyUpdateSongResponseDto body = SongControllerMapper.mapFromSongDtoToPartiallyUpdateSongResponseDto(savedSong);
        return ResponseEntity.ok(body);
    }

    @PutMapping("/{songId}/genre/{genreId}")
    ResponseEntity<AssignGenreToSongResponseDto> assignGenreByIdToSongById(@PathVariable Long songId, @PathVariable Long genreId) {
        SongDto songDto = songFacade.assignGenreByIdToSongById(songId, genreId);
        AssignGenreToSongResponseDto assignGenreToSongResponseDto = SongControllerMapper.mapFromSongDtoToAssignGenreToSongResponseDto(songDto);
        return ResponseEntity.ok(assignGenreToSongResponseDto);
    }
}
