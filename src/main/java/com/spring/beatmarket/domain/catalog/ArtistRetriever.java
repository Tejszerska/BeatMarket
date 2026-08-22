package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import com.spring.beatmarket.domain.catalog.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
class ArtistRetriever {
    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;

    Slice<ArtistDto.Summary> findAllArtist(String name, Pageable pageable) {
        Slice<Artist> all;
        if(name == null || name.isBlank()) {
            all = artistRepository.findByActiveTrue(pageable);
        } else {
            all = artistRepository.findByActiveTrueAndNameContainsIgnoreCase(name, pageable);
        }

        return all.map(artistMapper::toSummaryDto);
    }

    Artist findById(final Long artistId) {
        return artistRepository.findByIdAndActiveTrue(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist", artistId));
    }

    void existsById(Long id) {
        if (!artistRepository.existsById(id)) {
            throw new ResourceNotFoundException("Artist", id);
        }
    }
    Artist getArtistReference (Long id){
        existsById(id);
        return artistRepository.getReferenceById(id);
    }
    public List<Artist> getActiveArtists(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        List<Artist> foundArtists = artistRepository.findByIdInAndActiveTrue(ids);

        if (foundArtists.size() != new HashSet<>(ids).size()) {

            List<Long> foundIds = foundArtists.stream().map(Artist::getId).toList();
            Long missingId = ids.stream()
                    .filter(id -> !foundIds.contains(id))
                    .findFirst()
                    .orElse(0L);

            throw new ResourceNotFoundException("Artist", missingId);
        }

        return foundArtists;
    }
}
