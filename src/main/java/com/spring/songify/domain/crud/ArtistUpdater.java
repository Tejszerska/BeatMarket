package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.ArtistDto;
import com.spring.songify.domain.crud.exception.NameIsBlankException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
class ArtistUpdater {
    private final ArtistRetriever artistRetriever;
    private final ArtistMapper artistMapper;

    ArtistDto updateArtistNameById(final Long artistId, final String name) {
        Artist artist = artistRetriever.findById(artistId);
        if (name == null || name.isBlank()) throw new NameIsBlankException("Artist needs name specified!");
        artist.setName(name);
        return artistMapper.mapFromEntityToArtistDto(artist);
    }
}
