package com.spring.songify.domain.crud;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
class ArtistAssigner {
    private final ArtistRetriever artistRetriever;
    private final AlbumRetriever albumRetriever;

    void addArtistToAlbum(final Long artistId, final Long albumId) {
        Artist artist = artistRetriever.findById(artistId);
        Album album = albumRetriever.findById(albumId);
        artist.addAlbum(album);
    }
}
