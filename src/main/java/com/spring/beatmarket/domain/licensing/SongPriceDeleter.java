package com.spring.beatmarket.domain.licensing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
class SongPriceDeleter {
    private final SongPriceRepository songPriceRepository;

    void deactivatePricesForSong(Long songId){
        log.info("soft deleting prices for song by id: " + songId);
        songPriceRepository.deactivateAllBySongId(songId);
    }
}
