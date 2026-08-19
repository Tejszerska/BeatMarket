package com.spring.beatmarket.domain.catalog;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

@org.springframework.stereotype.Repository
interface SongRepository extends Repository<Song, Long>, JpaSpecificationExecutor<Song> {

    Optional<Song> findById(Long id);

    @Query("SELECT s FROM Song s " +
            "LEFT JOIN FETCH s.genre " +
            "LEFT JOIN FETCH s.artists " +
            "LEFT JOIN FETCH s.album " +
            "WHERE s.id = :id")
    Optional<Song> findSongByIdEagerly(Long id);

    boolean existsById(Long id);

    Song save(Song song);

    @Query("SELECT s FROM Song s where s.id in :ids")
    Collection<Song> findAllById(@Param("ids") Collection<Long> ids);
}
