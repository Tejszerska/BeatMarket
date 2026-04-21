package com.spring.songify.infrastructure.crud.controller.song;

import com.spring.songify.domain.crud.SongifyCrudFacade;
import com.spring.songify.domain.crud.dto.SongDto;
import com.spring.songify.domain.crud.dto.SongRequestDto;
import com.spring.songify.infrastructure.crud.controller.song.dto.request.CreateSongRequestDto;
import com.spring.songify.infrastructure.crud.controller.song.dto.request.PartiallyUpdateSongRequestDto;
import com.spring.songify.infrastructure.crud.controller.song.dto.response.AssignGenreToSongResponseDto;
import com.spring.songify.infrastructure.crud.controller.song.dto.response.CreateSongResponseDto;
import com.spring.songify.infrastructure.crud.controller.song.dto.response.GetAllSongsResponseDto;
import com.spring.songify.infrastructure.crud.controller.song.dto.response.GetSongResponseDto;
import com.spring.songify.infrastructure.crud.controller.song.dto.response.PartiallyUpdateSongResponseDto;
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

@RestController
@Log4j2
@RequestMapping("/songs")
@AllArgsConstructor
public
class SongRestController {

    private final SongifyCrudFacade songFacade;
    private final SongControllerMapper songControllerMapper;

    @GetMapping
    ResponseEntity<GetAllSongsResponseDto> getAllSongs(
            @ParameterObject @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Slice<SongDto> allSongs = songFacade.findAllSongs(pageable);
        return ResponseEntity.ok(songControllerMapper.mapFromSongToGetAllSongsResponseDto(allSongs));
    }

    @GetMapping("/{id}")
    ResponseEntity<GetSongResponseDto> getSongById(@PathVariable Long id, @RequestHeader(required = false) String requestId) {
        log.info(requestId);
        SongDto song = songFacade.findSongDtoById(id);
        return ResponseEntity.ok(songControllerMapper.mapFromSongToGetSongResponseDto(song));
    }

    @PostMapping
    ResponseEntity<CreateSongResponseDto> postSong(@RequestBody @Valid CreateSongRequestDto createSongRequestDto) {
        SongRequestDto domainRequest = songControllerMapper.mapFromSongCreateSongRequestDtoToDomainRequest(createSongRequestDto);
        SongDto savedSong = songFacade.addSong(domainRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(songControllerMapper.mapFromSongDtoToCreateSongResponseDto(savedSong));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteSongByIdUsingPathVariable(@PathVariable Long id) {
        songFacade.deleteSongById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    ResponseEntity<PartiallyUpdateSongResponseDto> partiallyUpdateSong(@PathVariable Long id,
                                                                       @RequestBody PartiallyUpdateSongRequestDto request) {
        SongDto updatedSong = songControllerMapper.mapFromPartiallyUpdateSongRequestDtoToSong(request);
        SongDto savedSong = songFacade.updateSongPartiallyById(id, updatedSong);
        return ResponseEntity.ok(songControllerMapper.mapFromSongDtoToPartiallyUpdateSongResponseDto(savedSong));
    }

    @PutMapping("/{songId}/genre/{genreId}")
    ResponseEntity<AssignGenreToSongResponseDto> assignGenreByIdToSongById(@PathVariable Long songId, @PathVariable Long genreId) {
        SongDto songDto = songFacade.assignGenreByIdToSongById(songId, genreId);
        return ResponseEntity.ok(songControllerMapper.mapFromSongDtoToAssignGenreToSongResponseDto(songDto));
    }
}
