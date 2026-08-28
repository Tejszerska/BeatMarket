package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Transactional
class ArtistFacadeImpl implements ArtistFacade {

    private final ArtistAdder artistAdder;
    private final ArtistRetriever artistRetriever;
    private final ArtistDeleter artistDeleter;
    private final ArtistUpdater artistUpdater;

    public Slice<ArtistDto.Summary> findAllArtists(String name, Pageable pageable) {
        return artistRetriever.findAll(name, pageable);
    }

    public ArtistDto.Details getArtistDetails(final Long artistId) {
        return artistRetriever.getDetails(artistId);
    }

    public ArtistDto.Info addArtist(ArtistDto.Create createDto) {
        return artistAdder.add(createDto);
    }

    public ArtistDto.Info updateArtist(Long artistId, ArtistDto.Update dto) {
        return artistUpdater.update(artistId, dto);
    }

    public void deactivateArtist(final Long artistId) {
        artistDeleter.deactivate(artistId);
    }
}