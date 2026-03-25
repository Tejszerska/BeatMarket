package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.ArtistDto;
import com.spring.songify.domain.crud.dto.GenreDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
class GenreAdder {
    private final GenreRepository genreRepository;


    GenreDto addGenre(final String name) {
        Genre genre = new Genre(name);
        Genre save = genreRepository.save(genre);
        return new GenreDto(save.getId(), save.getName());
    }
}
