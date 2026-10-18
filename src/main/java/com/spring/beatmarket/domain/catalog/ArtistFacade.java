package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import com.spring.beatmarket.domain.catalog.dto.ImageDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ArtistFacade {
    Slice<ArtistDto.Summary> findAllArtists(String name, Pageable pageable);

    ArtistDto.Details getArtistDetails(final Long artistId);

    ArtistDto.Info addArtist(ArtistDto.Create createDto);

    ArtistDto.Info updateArtist(Long artistId, ArtistDto.Update dto);

    ImageDto.Upload updateArtistImage(Long artistId, byte[] imageBytes);

    void deactivateArtist(final Long artistId);
}
