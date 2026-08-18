package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.LegacyArtistDto;
import com.spring.beatmarket.domain.catalog.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
class ArtistRetriever {
    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;

    Slice<LegacyArtistDto> findAllArtist(Pageable pageable) {
        Slice<Artist> all = artistRepository.findAll(pageable);
        return all.map(artistMapper::mapFromEntityToArtistDto);
    }

    Artist findById(final Long artistId) {
        return artistRepository.findById(artistId)
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
}
