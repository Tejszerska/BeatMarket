package com.spring.beatmarket.domain.catalog;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.Repository;

import java.util.Optional;

@org.springframework.stereotype.Repository
interface GenreRepository extends Repository<Genre, Long> {
    Genre save(Genre genre);

    Slice<Genre> findByActiveTrue(Pageable pageable);

    boolean existsByIdAndActiveTrue(Long id);

    Optional<Genre> findByIdAndActiveTrue(Long id);
}
