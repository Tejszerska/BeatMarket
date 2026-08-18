package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.domain.catalog.exception.ResourceNotFoundException;
import com.spring.beatmarket.domain.licensing.LicensingFacade;
import com.spring.beatmarket.domain.licensing.dto.SongPriceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
class SongRetriever {
    private final SongRepository songRepository;
    private final SongMapper songMapper;
    private final LicensingFacade licensingFacade;

    Slice<SongDto.Summary> findAll(SongDto.SearchCriteria searchCriteria, Pageable pageable) {

        Set<Long> matchingIdsFromLicensing = null;

        if (searchCriteria.maxPrice() != null) {
            matchingIdsFromLicensing = licensingFacade.findSongIdByMaxPrice(
                    searchCriteria.currency(),
                    searchCriteria.license(),
                    searchCriteria.maxPrice()
            );
        }

        Specification<Song> spec = Specification
                .where(SongSpecifications.hasGenre(searchCriteria.genre()))
                .and(SongSpecifications.hasArtist(searchCriteria.artist()))
                .and(SongSpecifications.hasLanguage(searchCriteria.language()))
                .and(SongSpecifications.hasAlbum(searchCriteria.album()))
                .and(SongSpecifications.hasMinDuration(searchCriteria.minDuration()))
                .and(SongSpecifications.hasMaxDuration(searchCriteria.maxDuration()))
                .and(SongSpecifications.hasReleaseDate(searchCriteria.releaseDate()))
                .and(SongSpecifications.hasIdsIn(matchingIdsFromLicensing));


        Slice<Song> songsSlice = songRepository.findAll(spec, pageable);

        if (songsSlice.isEmpty()) {
            return songsSlice.map(song -> null);
        }

        List<Long> idsList = songsSlice.getContent().stream().map(Song::getId).toList();

        Map<Long, List<SongPriceDto>> pricing = licensingFacade.getMultiplePricingDto(idsList);

        return songsSlice.map(song -> {
            List<SongPriceDto> pricesForSong = pricing.getOrDefault(song.getId(), Collections.emptyList());
            return songMapper.toSummaryDto(song, pricesForSong);
        });
    }

    SongDto.Details getSongDetailsById(Long id) {
        Song song = findSongById(id);
        List<SongPriceDto> pricing = licensingFacade.getPricingForSingleSong(id);
        return songMapper.toDetailsDto(song, pricing);
    }

    Song findSongById(Long id) {
        return songRepository.findSongByIdEagerly(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song", id));
    }

    void existsById(Long id) {
        if (!songRepository.existsById(id)) {
            throw new ResourceNotFoundException("Song", id);
        }
    }
}
