package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.ArtistDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
class ArtistRetriever {
    private final ArtistRepository artistRepository;

    Set<ArtistDto> findAllArtist(Pageable pageable) {
        Set<Artist> all = artistRepository.findAll(pageable);
        return all.stream()
                .map(a -> new ArtistDto(a.getId(), a.getName()))
                .collect(Collectors.toSet());
    }

    Artist findById(final Long artistId) {
        return artistRepository.findById(artistId)
                .orElseThrow(() -> new ArtistNotFoundException("Artist by id=" + artistId + " not found"));
    }
}
