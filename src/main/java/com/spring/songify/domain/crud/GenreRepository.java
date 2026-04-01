package com.spring.songify.domain.crud;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
interface GenreRepository extends Repository<Genre, Long> {
    Genre save(Genre genre);

    Integer deleteGenreById(Long genreId);

    List<Genre> findAll(Pageable pageable);

    Optional<Genre> findGenreById(Long id);
}
