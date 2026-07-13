package com.spring.beatmarket.domain.catalog;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.Optional;

@org.springframework.stereotype.Repository
interface GenreRepository extends Repository<Genre, Long> {
    Genre save(Genre genre);

    Integer deleteGenreById(Long genreId);

    Slice<Genre> findAll(Pageable pageable);

    @Query("SELECT g FROM Genre g where g.id=:id")
    Optional<Genre> findGenreById(Long id);
}
