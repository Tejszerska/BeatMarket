package com.spring.beatmarket.domain.catalog;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
interface ArtistRepository extends Repository<Artist, Long> {
    Artist save(Artist artist);

    Slice<Artist> findAll(Pageable pageable);

    Optional<Artist> findById(Long artistId);

    int deleteArtistById(Long id);

    boolean existsById(Long id);

    Artist getReferenceById(Long id);

    List<Artist> findByIdInAndActiveTrue(List<Long> ids);
}
