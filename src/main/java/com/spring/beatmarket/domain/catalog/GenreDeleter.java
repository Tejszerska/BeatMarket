package com.spring.beatmarket.domain.catalog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
class GenreDeleter {
    private final GenreRepository genreRepository;

    void deleteById(Long id) {
        log.info("deleting genre by id: " + id);
        genreRepository.deleteGenreDirectly(id);
    }

}
