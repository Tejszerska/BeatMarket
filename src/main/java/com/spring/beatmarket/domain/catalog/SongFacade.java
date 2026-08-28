package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.SongDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface SongFacade {

    Slice<SongDto.Summary> searchSongs(SongDto.SearchCriteria searchCriteria, Pageable pageable);

    SongDto.Details  getSongDetails(Long id);

    SongDto.Info addSong(final SongDto.Create dto);

    SongDto.Info updateSong(Long id, SongDto.Update songFromRequest);

    void deactivateSong(Long id);

    Integer bulkUpdateSongsByGenreId(Long oldId, Long newId);
}
