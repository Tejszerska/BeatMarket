package com.spring.songify.domain.crud;

import org.springframework.data.repository.Repository;

import java.util.Optional;

@org.springframework.stereotype.Repository
interface GenreRepository extends Repository<Genre, Long> {
    Genre save(Genre genre);

    Integer deleteGenreById(Long genreId);
}
