package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.licensing.LicensingFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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

    void deleteAllSongsById(final Set<Long> songIds) {
        List<Song> byIdIn = songRepository.findByIdInAndActiveTrue(songIds);
        for(Song song : byIdIn) song.deactivate();
    }
}
