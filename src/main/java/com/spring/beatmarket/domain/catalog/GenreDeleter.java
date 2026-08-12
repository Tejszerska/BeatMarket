package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
class GenreDeleter {
    private final GenreRepository genreRepository;

    int deleteById(Long id) {
        int deletedRows = genreRepository.deleteGenreById(id);
        if( deletedRows == 0) throw new ResourceNotFoundException("Genre", id);
        return 1;
    }

}
