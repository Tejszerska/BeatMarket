package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.GenreDto;
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

    GenreDto.Info update(final Long id, final GenreDto.Update dto) {
        Genre genreById = genreRetriever.getActive(id);
        genreById.setName(dto.name().trim());
        genreRepository.save(genreById);
        return genreMapper.toInfoDto(genreById);
    }
}
