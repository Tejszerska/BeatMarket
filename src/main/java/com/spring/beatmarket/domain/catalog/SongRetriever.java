package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.domain.catalog.dto.SongSummaryDto;
import com.spring.beatmarket.domain.catalog.exception.SongNotFoundException;
import com.spring.beatmarket.domain.licensing.LicensingFacade;
import com.spring.beatmarket.domain.licensing.dto.SongPriceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
class SongRetriever {
    private final SongRepository songRepository;
    private final SongMapper songMapper;
    private final LicensingFacade licensingFacade;

    Slice<SongSummaryDto> findAll(Pageable pageable) {
        log.info("retrieving all songs: ");
        Slice<SongSummaryDto> map = songRepository.findAllSongs(pageable)
                .map(songMapper::mapFromEntityToSongSummaryDto);

     }

    SongDto findSongDtoById(Long id) {
        Song s = findSongById(id);
        return songMapper.mapFromEntityToSongDto(s);
    }

    Song findSongById(Long id) {
        return songRepository.findSongByIdWithGenre(id)
                .orElseThrow(() -> new SongNotFoundException("Song with id " + id + " not found"));
    }

    void existsById(Long id) {
        if (!songRepository.existsById(id)) {
            throw new SongNotFoundException("Song with id " + id + " not found");
        }
    }
}
