package com.spring.songify.domain.crud;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.Set;

@org.springframework.stereotype.Repository
interface ArtistRepository extends Repository<Artist, Long> {
    Artist save(Artist artist);

    Set<Artist> findAll(Pageable pageable);

    Optional<Artist> findById(Long artistId);

    void deleteArtistById(Long id);
}
