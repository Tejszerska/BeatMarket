package com.spring.songify.domain.crud;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
class GenreDeleter {
    private final GenreRepository genreRepository;
    private final GenreRetriever genreRetriever;

    boolean deleteById(Long id) {
        int i = genreRepository.deleteGenreById(id);
        if( i != 1) throw new GenreNotDeletedException("Genre by id=" + id + " was not deleted");
        return true;
    }

}
