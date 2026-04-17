package com.spring.songify.infrastructure.crud.controller.genre;

import com.spring.songify.domain.crud.SongifyCrudFacade;
import com.spring.songify.domain.crud.dto.GenreDto;
import com.spring.songify.domain.crud.dto.GenreRequestDto;
import com.spring.songify.infrastructure.crud.controller.genre.dto.request.CreateGenreRequest;
import com.spring.songify.infrastructure.crud.controller.genre.dto.response.CreateGenreResponse;
import com.spring.songify.infrastructure.crud.controller.genre.dto.response.GetAllGenresResponseDto;
import com.spring.songify.infrastructure.crud.controller.genre.dto.response.GetGenreResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/genres")
class GenreController {
    private final SongifyCrudFacade songifyCrudFacade;

    @PostMapping
    ResponseEntity<CreateGenreResponse> postGenre(@RequestBody CreateGenreRequest createGenreRequest) {
        GenreRequestDto genreRequestDto = GenreControllerMapper.mapFromCreateGenreRequestDtoToDomainRequest(createGenreRequest);
        GenreDto genreDto = songifyCrudFacade.addGenre(genreRequestDto);
        CreateGenreResponse createGenreResponse = GenreControllerMapper.mapFromGenreDtoToCreateGenreResponse(genreDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createGenreResponse);
    }

    @GetMapping
    ResponseEntity<GetAllGenresResponseDto> getGenres(
            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Slice<GenreDto> allGenresSlice = songifyCrudFacade.findAllGenres(pageable);
        GetAllGenresResponseDto getAllGenresResponseDto = GenreControllerMapper.mapSliceToGetAllGenresResponseDto(allGenresSlice);
        return ResponseEntity.ok(getAllGenresResponseDto);
    }

    @GetMapping("/{genreId}")
    ResponseEntity<GetGenreResponseDto> getGenreById(@PathVariable Long genreId) {
        GenreDto dto = songifyCrudFacade.findGenreById(genreId);
        GetGenreResponseDto getGenreResponseDto = GenreControllerMapper.mapGenreDtoToGetGenreResponseDto(dto);

        return ResponseEntity.ok(getGenreResponseDto);
    }

    @DeleteMapping("/{genreId}")
    ResponseEntity<Void> deleteGenreById(@PathVariable Long genreId) {
        songifyCrudFacade.deleteGenreById(genreId);
        return ResponseEntity.noContent().build();
    }
}
