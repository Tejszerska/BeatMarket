package com.spring.beatmarket.domain.catalog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
class GenreDeleter {
    private final GenreRetriever genreRetriever;

    void deleteById(Long id) {
        log.info("soft deleting genre by id: " + id);
        Genre genreById = genreRetriever.getGenre(id);
        genreById.deactivate();
    }

}
