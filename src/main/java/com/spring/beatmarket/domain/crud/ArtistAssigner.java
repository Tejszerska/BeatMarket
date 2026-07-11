package com.spring.beatmarket.domain.crud;

import com.spring.beatmarket.domain.crud.dto.ArtistWithAlbumDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
class ArtistAssigner {
    private final ArtistRetriever artistRetriever;
    private final AlbumRetriever albumRetriever;
    private final ArtistWithAlbumMapper artistWithAlbumMapper;


    ArtistWithAlbumDto addArtistToAlbum(final Long artistId, final Long albumId) {
        Artist artist = artistRetriever.findById(artistId);
        Album album = albumRetriever.findById(albumId);
        artist.addAlbum(album);
        return artistWithAlbumMapper.mapToDto(artist, album);
    }
}
