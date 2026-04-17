package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.GenreDto;
import com.spring.songify.domain.crud.exception.NameIsBlankException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
class GenreAdder {
    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;


    GenreDto addGenre(final String name) {
        Genre genre = new Genre(name);
        if(name == null || name.isBlank()) throw new NameIsBlankException("Genre needs a specified name!");
        return genreMapper.mapFromEntityToGenreDto(
                           genreRepository.save(genre)
        );
    }
}
