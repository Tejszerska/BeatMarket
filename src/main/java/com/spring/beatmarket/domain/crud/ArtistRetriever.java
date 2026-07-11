package com.spring.beatmarket.domain.crud;

import com.spring.beatmarket.domain.crud.dto.ArtistDto;
import com.spring.beatmarket.domain.crud.exception.ArtistNotFoundException;
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
                .orElseThrow(() -> new ArtistNotFoundException("Artist by id=" + artistId + " not found"));
    }

    ArtistDto findByIdReturnDto(final Long artistId){
      return artistMapper.mapFromEntityToArtistDto(findById(artistId));
    }

    boolean existsById(final Long artistId) {
       return artistRepository.existsById(artistId);
    }
}
