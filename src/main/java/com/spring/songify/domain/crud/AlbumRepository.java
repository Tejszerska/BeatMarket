package com.spring.songify.domain.crud;

import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
interface AlbumRepository extends Repository<Album, Long> {
    Album save(Album album);
}
