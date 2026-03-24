package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.ArtistDto;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
interface ArtistRepository extends Repository<Artist, Long> {
    Artist save(Artist artist);
}
