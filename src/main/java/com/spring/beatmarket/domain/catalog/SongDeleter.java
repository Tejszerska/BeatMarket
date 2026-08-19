package com.spring.beatmarket.domain.catalog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
class SongDeleter {
    private final SongRepository songRepository;
    private final SongRetriever songRetriever;

    void deleteById(Long id) {
        log.info("soft deleting song by id: " + id);
        Song songById = songRetriever.findSongByIdLazily(id);
        songById.deactivate();
    }

    void deleteAllSongsById(final Set<Long> songIds) {
        Collection<Song> byIdIn = songRepository.findAllById(songIds);
        for(Song song : byIdIn) song.deactivate();
    }
}
