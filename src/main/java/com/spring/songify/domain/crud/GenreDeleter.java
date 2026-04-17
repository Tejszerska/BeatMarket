package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.exception.GenreNotfoundException;
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
        if( deletedRows == 0) throw new GenreNotfoundException("Genre by id=" + id + " was not found");
        return 1;
    }

}
