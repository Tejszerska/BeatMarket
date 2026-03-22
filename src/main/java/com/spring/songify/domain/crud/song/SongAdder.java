package com.spring.songify.domain.crud.song;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional

class SongAdder {
    private final SongRepository songRepository;

    Song addSong(final Song song) {
        log.info("adding new song: " + song);
        song.setDuration(200L);
        song.setReleaseDate(Instant.now());
        return songRepository.save(song);
    }
}
