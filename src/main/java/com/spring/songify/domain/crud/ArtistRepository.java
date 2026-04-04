package com.spring.songify.domain.crud;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.Repository;

import java.util.Optional;

@org.springframework.stereotype.Repository
interface ArtistRepository extends Repository<Artist, Long> {
    Artist save(Artist artist);

    Slice<Artist> findAll(Pageable pageable);

    Optional<Artist> findById(Long artistId);

    int deleteArtistById(Long id);
}
