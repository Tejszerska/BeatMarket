package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.GenreDto;
import com.spring.songify.domain.crud.exception.GenreNotfoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
class GenreRetriever {
    private final GenreRepository genreRepository;
    private final static Long DEFAULT_GENRE_ID = 1L;
    private final GenreMapper genreMapper;

    Slice<GenreDto> findAllGenres(Pageable pageable) {
        return genreRepository.findAll(pageable)
                .map(genreMapper::mapFromEntityToGenreDto);
    }

    GenreDto findGenreDtoById(final Long genreId) {
        return genreMapper.mapFromEntityToGenreDto(findGenreById(genreId));
    }
    Genre findGenreById(final Long genreId) {
        return genreRepository.findGenreById(genreId)
                .orElseThrow(() -> new GenreNotfoundException("Genre by id=" + genreId + "wasn't found"));
    }

    Genre retrieveDefaultGenre(){
       return findGenreById(DEFAULT_GENRE_ID);
    }
}
