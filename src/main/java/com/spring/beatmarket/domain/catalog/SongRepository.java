package com.spring.beatmarket.domain.catalog;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@org.springframework.stereotype.Repository
interface SongRepository extends Repository<Song, Long> {

//    @Query("SELECT s FROM Song s")
//    Slice<Song> findAll(Pageable pageable);

    @Query("SELECT s FROM Song s WHERE s.id = :id")
    Optional<Song> findById(Long id);

    @Query("SELECT s FROM Song s JOIN FETCH s.genre")
    Slice<Song> findAllSongsWithGenre(Pageable pageable);

    @Query("SELECT s FROM Song s JOIN FETCH s.genre WHERE s.id = :id")
    Optional<Song> findSongByIdWithGenre(Long id);

    @Modifying
    @Query("DELETE FROM Song s WHERE s.id = :id")
    int deleteById(Long id);

    @Modifying
    @Query("UPDATE Song s SET s.title = :#{#newSong.title} where s.id = :id")
    void updateById(Long id, Song newSong);

    boolean existsById(Long id);

    Song save(Song song);

    @Transactional
    @Modifying
    @Query("delete from Song s where s.id in :ids")
    int deleteByIdIn(Collection<Long> ids);
}
