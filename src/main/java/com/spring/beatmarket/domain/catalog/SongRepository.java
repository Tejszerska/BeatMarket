package com.spring.beatmarket.domain.catalog;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
interface SongRepository extends Repository<Song, Long>, JpaSpecificationExecutor<Song> {

    Optional<Song> findByIdAndActiveTrue(Long id);

    @Query("SELECT s FROM Song s " +
            "LEFT JOIN FETCH s.genre " +
            "LEFT JOIN FETCH s.artists " +
            "LEFT JOIN FETCH s.album " +
            "WHERE s.id = :id AND s.active = true")
    Optional<Song> findSongByIdEagerly(Long id);

    boolean existsByIdAndActiveTrue(Long id);

    Song save(Song song);

    List<Song> findByIdInAndActiveTrue(Collection<Long> ids);

    boolean existsByGenreId(Long genreId);

    @Modifying
    @Query("UPDATE Song s SET s.genre.id = :newGenreId WHERE s.genre.id = :oldGenreId AND s.active = true")
    Integer bulkUpdateGenre(@Param("oldGenreId") Long oldGenreId, @Param("newGenreId") Long newGenreId);
}
