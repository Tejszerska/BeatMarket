package com.spring.beatmarket.domain.licensing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
class SongPriceDeleter {
    private final SongPriceRepository songPriceRepository;

    void deactivatePricesForSong(Long songId){
        log.info("soft deleting prices for song by id: " + songId);
        songPriceRepository.deactivateAllBySongId(songId, Instant.now());
    }

    void deactivatePricesForSongs(final Set<Long> songIds) {
        log.info("soft deleting prices for songs by id: " + songIds);
        songPriceRepository.deactivateAllBySongIds(songIds, Instant.now());
    }
}
