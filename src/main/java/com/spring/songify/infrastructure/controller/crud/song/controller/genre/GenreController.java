package com.spring.songify.infrastructure.controller.crud.song.controller.genre;

import com.spring.songify.domain.crud.SongifyCrudFacade;
import com.spring.songify.domain.crud.dto.GenreDto;
import com.spring.songify.domain.crud.dto.GenreRequestDto;
import com.spring.songify.infrastructure.controller.crud.song.controller.genre.response.GetAllGenresResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
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
    ResponseEntity<GenreDto> postGenre(@RequestBody GenreRequestDto genreRequestDto) {
        GenreDto genreDto = songifyCrudFacade.addGenre(genreRequestDto);
        return ResponseEntity.ok(genreDto);
    }
    @GetMapping
    ResponseEntity<GetAllGenresResponseDto> getGenres(Pageable pageable){
        Slice<GenreDto> allGenresSlice = songifyCrudFacade.findAllGenres(pageable);
        GetAllGenresResponseDto getAllGenresResponseDto = GenreControllerMapper.mapSliceToGetAllGenresResponseDto(allGenresSlice);
        return ResponseEntity.ok(getAllGenresResponseDto);
    }

    @GetMapping("/{genreId}")
    ResponseEntity<GenreDto> getGenreById(@PathVariable Long genreId){
        GenreDto dto = songifyCrudFacade.findGenreById(genreId);
        return ResponseEntity.ok(dto);
    }

}
