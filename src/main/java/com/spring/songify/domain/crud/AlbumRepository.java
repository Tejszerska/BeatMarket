package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.AlbumInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

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

    @Query("""
            select a from Album a 
            inner join a.artists artists 
            where artists.id = :id
            """)
    Set<Album> findAllAlbumsByArtistId(Long id);

    @Transactional
    @Modifying
    @Query("delete from Album a where a.id in :ids")
    int deleteByIdIn(Collection<Long> ids);

    Optional<Album> findById(Long id);

    @Query("SELECT a FROM Album a")
    Set<Album> findAllAlbums(Pageable pageable);
}
