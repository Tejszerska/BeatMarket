package com.spring.beatmarket.domain.catalog;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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

    boolean existsByGenreId(Long genreId);

    @Query("SELECT DISTINCT s FROM Song s LEFT JOIN FETCH s.artists WHERE s.id IN :ids AND s.active = true")
    List<Song> findActiveWithArtistsByIds(@Param("ids") Collection<Long> ids);

    @Modifying
    @Query("UPDATE Song s SET s.genre.id = :newGenreId, s.version = s.version + 1, s.editedOn = :now WHERE s.genre.id = :oldGenreId AND s.active = true")    Integer bulkUpdateGenre(@Param("oldGenreId") Long oldGenreId,
                            @Param("newGenreId") Long newGenreId,
                            @Param("now") Instant now);

    @Modifying
    @Query("UPDATE Song s SET s.active = false, s.version = s.version + 1, s.editedOn = :now WHERE s.id IN :songIds")
    void deactivateAllByIds(@Param("songIds") Set<Long> songIds,
                            @Param("now") Instant now);
}
