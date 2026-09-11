package com.spring.beatmarket.domain.catalog;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@org.springframework.stereotype.Repository
interface AlbumRepository extends Repository<Album, Long> {
    Album save(Album album);

    Optional<Album> findByIdAndActiveTrue(Long id);

    Slice<Album> findByActiveTrue(Pageable pageable);

    Slice<Album> findByActiveTrueAndTitleContainingIgnoreCase(final String title, final Pageable pageable);

    Slice<Album> findByActiveTrueAndArtists_Id(Long artistId, Pageable pageable);

    Slice<Album> findByActiveTrueAndArtists_IdAndTitleContainingIgnoreCase(Long artistId, String title, Pageable pageable);

    @Query("SELECT DISTINCT a FROM Album a LEFT JOIN FETCH a.artists WHERE a.id IN :ids AND a.active = true")
    List<Album> findActiveWithArtistsByIds(@Param("ids") Collection<Long> ids);

    @Query("SELECT a FROM Album a " +
            "LEFT JOIN FETCH a.artists " +
            "LEFT JOIN FETCH a.songs " +
            "WHERE a.id = :id AND a.active = true")
    Optional<Album> findAlbumByIdEagerly(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Album a SET a.active = false, a.version = a.version + 1, a.editedOn = :now WHERE a.id = :id")
    void deactivateId(@Param("id") Long id,
                      @Param("now") Instant now);

    @Modifying
    @Query("UPDATE Album a SET a.active = false, a.version = a.version + 1, a.editedOn = :now WHERE a.id IN :albumIds")
    void deactivateAllByIds(@Param("albumIds") Set<Long> albumIds,
                            @Param("now") Instant now);
}
