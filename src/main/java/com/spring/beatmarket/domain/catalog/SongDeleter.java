package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.licensing.LicensingFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
class SongDeleter {
    private final SongRepository songRepository;
    private final SongRetriever songRetriever;
    private final LicensingFacade licensingFacade;

    void deleteById(Long id) {
        log.info("soft deleting song by id: " + id);
        Song songById = songRetriever.findSongByIdLazily(id);
        songById.deactivate();
        licensingFacade.deactivatePricesForSong(id);
    }

    void deleteAllSongsByIds(final Set<Long> songIds) {
        if (songIds == null || songIds.isEmpty()) return;
        log.info("soft deleting songs by ids: " + songIds);
        songRepository.deactivateAllByIds(songIds, Instant.now());
        licensingFacade.deactivatePricesForSongs(songIds);
    }
}
