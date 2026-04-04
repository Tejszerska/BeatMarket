package com.spring.songify.domain.crud;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
class SongDeleter {
    private final SongRepository songRepository;
    private final SongRetriever songRetriever;

    void deleteById(Long id) {
        log.info("deleting song by id: " + id);
        songRepository.deleteById(id);
    }

    void deleteAllSongsById(final Set<Long> songIds) {
        songRepository.deleteByIdIn(songIds);
    }
}
