package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
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

    @Query(""" 
            SELECT a FROM Album a
            JOIN FETCH a.songs songs
            JOIN FETCH a.artists artists
            WHERE a.id = :id
            """)
    Optional<AlbumInfo> findAlbumByIdReturnAlbumInfo(Long id);

    @Query("""
            select a from Album a
            inner join a.artists artists
            where artists.id = :id
            """)
    List<Album> findAllAlbumsByArtistId(Long id);

    @Transactional
    @Modifying
    @Query("delete from Album a where a.id in :ids")
    int deleteByIdIn(Collection<Long> ids);

    Optional<Album> findById(Long id);

    @Query("SELECT a FROM Album a")
    Slice<Album> findAllAlbums(Pageable pageable);

    Album getReferenceById(Long id);

    boolean existsById(Long id);

    Optional<Album> findByIdAndActiveTrue(Long id);

    @Query("SELECT DISTINCT a FROM Album a LEFT JOIN FETCH a.artists WHERE a.id IN :ids AND a.active = true")
    List<Album> findActiveWithArtistsByIds(@Param("ids") Collection<Long> ids);

    List<Album> findByIdInAndActiveTrue (Collection<Long> ids);

    @Modifying
    @Query("UPDATE Album a SET a.active = false, a.version = a.version + 1, a.editedOn = :now WHERE a.id IN :albumIds")
    void deactivateAllByIds(@Param("albumIds")Set<Long> albumIds,
                            @Param("now") Instant now);

    Slice<Album> findByActiveTrue(Pageable pageable);

    Slice<Album> findByActiveTrueAndTitleContainingIgnoreCase( final String title, final Pageable pageable);

    Slice<Album> findByActiveTrueAndArtists_Id(Long artistId, Pageable pageable);

    Slice<Album> findByActiveTrueAndArtists_IdAndTitleContainingIgnoreCase(Long artistId, String title, Pageable pageable);
}
