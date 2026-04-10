package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.ArtistDto;
import com.spring.songify.domain.crud.exception.ArtistNotFoundException;
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

    Slice<ArtistDto> findAllArtist(Pageable pageable) {
        Slice<Artist> all = artistRepository.findAll(pageable);
        return all.map(a -> new ArtistDto(a.getId(), a.getName()));
    }

    Artist findById(final Long artistId) {
        return artistRepository.findById(artistId)
                .orElseThrow(() -> new ArtistNotFoundException("Artist by id=" + artistId + " not found"));
    }
}
