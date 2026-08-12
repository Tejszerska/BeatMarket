package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
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

    Slice<ArtistDto> findAllArtist(Pageable pageable) {
        Slice<Artist> all = artistRepository.findAll(pageable);
        return all.map(artistMapper::mapFromEntityToArtistDto);
    }

    Artist findById(final Long artistId) {
        return artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist", artistId));
    }

    boolean existsById(final Long artistId) {
       return artistRepository.existsById(artistId);
    }

    Artist getArtistReference (Long id){
        return artistRepository.getReferenceById(id);
    }
}
