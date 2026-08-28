package com.spring.beatmarket.domain.catalog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
class GenreDeleter {
    private final GenreRetriever genreRetriever;
    private final SongRetriever songRetriever;

    void deactivate(Long id) {
        log.info("soft deleting genre by id: " + id);
        songRetriever.validateGenreHasNoActiveSongs(id);
        Genre genreById = genreRetriever.getActive(id);
        genreById.deactivate();
    }

}
