package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import com.spring.beatmarket.domain.catalog.exception.NameIsBlankException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
class ArtistUpdater {
    private final ArtistRetriever artistRetriever;
    private final ArtistMapper artistMapper;

    ArtistDto.Info updateArtistNameById(final Long artistId, final String name) {
        Artist artist = artistRetriever.findById(artistId);
        if (name == null || name.isBlank()) throw new NameIsBlankException("Artist needs name specified!");
        artist.changeName(name);
        return artistMapper.toInfoDto(artist);
    }
}
