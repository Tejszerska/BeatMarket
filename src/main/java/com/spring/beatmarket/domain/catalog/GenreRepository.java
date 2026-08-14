package com.spring.beatmarket.domain.catalog;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@org.springframework.stereotype.Repository
interface GenreRepository extends Repository<Genre, Long> {
    Genre save(Genre genre);


    Slice<Genre> findAll(Pageable pageable);

    Optional<Genre> findById(Long id);

    Genre getReferenceById(Long id);

    boolean existsById(Long id);

    @Modifying
    @Query("DELETE FROM Genre g WHERE g.id = :id")
    void deleteGenreDirectly(@Param("id") Long id);
}
