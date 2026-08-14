package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.GenreDto;
import com.spring.beatmarket.domain.catalog.dto.SaveGenreDto;
import com.spring.beatmarket.domain.catalog.exception.NameIsBlankException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
class GenreUpdater {
    private final GenreRepository genreRepository;
    private final GenreRetriever genreRetriever;
    private final GenreMapper genreMapper;


    GenreDto update(final Long id, final SaveGenreDto dto) {
        Genre genreById = genreRetriever.findGenreById(id);

        genreById.setName(dto.name().trim());
        genreRepository.save(genreById);
        return genreMapper.toDto(genreById);
    }
}
