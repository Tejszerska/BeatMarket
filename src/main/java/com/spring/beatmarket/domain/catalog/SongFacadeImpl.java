package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.SongDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Transactional
class SongFacadeImpl implements SongFacade {
    private final SongAdder songAdder;
    private final SongRetriever songRetriever;
    private final SongDeleter songDeleter;
    private final SongUpdater songUpdater;

    public Slice<SongDto.Summary> searchSongs(SongDto.SearchCriteria searchCriteria, Pageable pageable) {
        return songRetriever.findAll(searchCriteria, pageable);    }

    public SongDto.Details  getSongDetails(Long id) {
        return songRetriever.getDetails(id);
    }

    public SongDto.Info addSong(final SongDto.Create dto) {
        return songAdder.add(dto);
    }

    public SongDto.Info updateSong(Long id, SongDto.Update songFromRequest) {
        return songUpdater.update(id, songFromRequest);    }

    public void deactivateSong(Long id) {
        songDeleter.deactivate(id);
    }
}