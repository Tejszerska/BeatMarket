package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.AlbumInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.Optional;

@org.springframework.stereotype.Repository
interface AlbumRepository extends Repository<Album, Long> {
    Album save(Album album);


    @Query("""
            SELECT a FROM Album a
            JOIN FETCH a.songs songs
            JOIN FETCH a.artists artists
            WHERE a.id = :id
            """)
    Optional<Album> findAlbumByIdWithSongsAndArtists(Long id);

    @Query("SELECT a FROM Album a WHERE a.id = :id")
    Optional<AlbumInfo> findAlbumByIdReturnAlbumInfo(Long id);


}
