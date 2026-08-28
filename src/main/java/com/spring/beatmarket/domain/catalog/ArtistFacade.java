package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ArtistFacade {
    Slice<ArtistDto.Summary> findAllArtists(String name, Pageable pageable);

    ArtistDto.Details getArtistDetails(final Long artistId);

    ArtistDto.Info addArtist(ArtistDto.Create createDto);

    ArtistDto.Info updateArtist(Long artistId, ArtistDto.Update dto);

    void deactivateArtist(final Long artistId);
}
