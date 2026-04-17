package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.AlbumDto;
import com.spring.songify.domain.crud.dto.ArtistDto;
import com.spring.songify.domain.crud.dto.ArtistWithAlbumDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
class ArtistAssigner {
    private final ArtistRetriever artistRetriever;
    private final AlbumRetriever albumRetriever;
    private final ArtistMapper artistMapper;
    private final AlbumMapper albumMapper;


    ArtistWithAlbumDto addArtistToAlbum(final Long artistId, final Long albumId) {
        Artist artist = artistRetriever.findById(artistId);
        Album album = albumRetriever.findById(albumId);
        artist.addAlbum(album);

        ArtistDto artistDto = artistMapper.mapFromEntityToArtistDto(artist);
        AlbumDto albumDto = albumMapper.mapFromEntityToAlbumDto(album);
        return new ArtistWithAlbumDto(artistDto, albumDto);
    }
}
