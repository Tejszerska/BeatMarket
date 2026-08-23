package com.spring.beatmarket.domain.catalog;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
interface ArtistRepository extends Repository<Artist, Long> {
    Artist save(Artist artist);

    Slice<Artist> findByActiveTrue(Pageable pageable);

    Slice<Artist> findByActiveTrueAndNameContainsIgnoreCase(String name, Pageable pageable);


    Optional<Artist> findByIdAndActiveTrue(Long artistId);

    int deleteArtistById(Long id);

    boolean existsById(Long id);

    Artist getReferenceById(Long id);

    List<Artist> findByIdInAndActiveTrue(List<Long> ids);


    @Query("SELECT a FROM Artist a " +
            "LEFT JOIN FETCH a.songs " +
            "WHERE a.id = :id AND a.active = true")
    Optional<Artist> findByIdWithSongs(@Param("id") Long id);

    @Query("SELECT a FROM Artist a " +
            "LEFT JOIN FETCH a.albums " +
            "WHERE a.id = :id AND a.active = true")
    Optional<Artist> findByIdWithAlbums(@Param("id") Long id);
}
