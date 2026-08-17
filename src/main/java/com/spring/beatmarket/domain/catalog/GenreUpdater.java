package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.LegacyGenreDto;
import com.spring.beatmarket.domain.catalog.dto.SaveGenreDto;
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


    LegacyGenreDto update(final Long id, final SaveGenreDto dto) {
        Genre genreById = genreRetriever.findGenreById(id);

        genreById.setName(dto.name().trim());
        genreRepository.save(genreById);
        return genreMapper.toDto(genreById);
    }
}
