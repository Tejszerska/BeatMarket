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
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
class SongRetriever {
    private final SongRepository songRepository;
    private final SongMapper songMapper;
    private final LicensingFacade licensingFacade;

    Slice<SongSummaryDto> findAll(Pageable pageable) {
        log.info("retrieving all songs: ");

        Slice<Long> idsSlice = songRepository.findSongIds(pageable);
        if (idsSlice.isEmpty()) {
            return new SliceImpl<>(List.of(), pageable, idsSlice.hasNext());
        }
        List<Long> ids = idsSlice.getContent();

        List<Song> allSongs = songRepository.findSongsWithDetailsByIds(ids);
        Map<Long, List<SongPriceDto>> pricing = licensingFacade.getMultiplePricingDto(ids);

        List<SongSummaryDto> dtos = allSongs.stream()
                .map(song -> {
                    List<SongPriceDto> pricesForSong = pricing.getOrDefault(song.getId(), Collections.emptyList());
                    return songMapper.mapFromEntityToSongSummaryDto(song, pricesForSong);
                })
                .toList();

        return new SliceImpl<>(dtos, pageable, idsSlice.hasNext());
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
