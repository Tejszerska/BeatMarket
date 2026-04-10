package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.exception.GenreNotfoundException;
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

    boolean deleteById(Long id) {
        int deletedRows = genreRepository.deleteGenreById(id);
        if( deletedRows == 0) throw new GenreNotfoundException("Genre by id=" + id + " was not found");
        return true;
    }

}
