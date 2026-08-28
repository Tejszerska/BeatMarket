package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.domain.catalog.exception.DataConflictException;
import com.spring.beatmarket.domain.catalog.exception.ResourceNotFoundException;
import com.spring.beatmarket.domain.licensing.LicensingFacade;
import com.spring.beatmarket.domain.licensing.dto.SongPriceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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

        Specification<Song> spec = Specification.where(SongSpecifications.isActive());

        if (searchCriteria.maxPrice() != null) {
            matchingIdsFromLicensing = licensingFacade.findSongIdByMaxPrice(
                    searchCriteria.currency(),
                    searchCriteria.license(),
                    searchCriteria.maxPrice()
            );
        }

        spec = spec
                .and(SongSpecifications.hasGenre(searchCriteria.genre()))
                .and(SongSpecifications.hasArtist(searchCriteria.artist()))
                .and(SongSpecifications.hasLanguage(searchCriteria.language()))
                .and(SongSpecifications.hasAlbum(searchCriteria.album()))
                .and(SongSpecifications.hasMinDuration(searchCriteria.minDuration()))
                .and(SongSpecifications.hasMaxDuration(searchCriteria.maxDuration()))
                .and(SongSpecifications.hasReleaseDate(searchCriteria.releaseDate()))
                .and(SongSpecifications.hasIdsIn(matchingIdsFromLicensing));


        Slice<Song> songsSlice = songRepository.findAll(spec, pageable);

        if (songsSlice.isEmpty()) {
            return new SliceImpl<>(Collections.emptyList(), pageable, false);
        }

        List<Long> idsList = songsSlice.getContent().stream().map(Song::getId).toList();

        Map<Long, List<SongPriceDto>> pricing = licensingFacade.getMultiplePricingDto(idsList);

        return songsSlice.map(song -> {
            List<SongPriceDto> pricesForSong = pricing.getOrDefault(song.getId(), Collections.emptyList());
            return songMapper.toSummaryDto(song, pricesForSong);
        });
    }

    SongDto.Details getDetails(Long id) {
        Song song = getEagerly(id);
        List<SongPriceDto> pricing = licensingFacade.getPricingForSingleSong(id);
        return songMapper.toDetailsDto(song, pricing);
    }

    Song getEagerly(Long id) {
        return songRepository.findSongByIdEagerly(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song", id));
    }

    Song getLazily(Long id) {
        return songRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song", id));
    }

    void validateGenreHasNoActiveSongs(final Long genreId) {
       if(songRepository.existsByGenreId(genreId)) {
           throw new DataConflictException(String.format("Genre by id='%s' has songs assigned", genreId));
       }
    }

    List<Song> getActiveWithArtist(final List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        List<Song> foundSongs = songRepository.findActiveWithArtistsByIds(ids);

        if (foundSongs.size() != new HashSet<>(ids).size()) {
            List<Long> foundIds = foundSongs.stream().map(Song::getId).toList();
            Long missingId = ids.stream()
                    .filter(id -> !foundIds.contains(id))
                    .findFirst()
                    .orElse(0L);
            throw new ResourceNotFoundException("Song", missingId);
        }
        return foundSongs;
    }
}
