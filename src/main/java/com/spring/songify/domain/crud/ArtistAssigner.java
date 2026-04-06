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

    ArtistWithAlbumDto addArtistToAlbum(final Long artistId, final Long albumId) {
        Artist artist = artistRetriever.findById(artistId);
        Album album = albumRetriever.findById(albumId);
        artist.addAlbum(album);
        ArtistDto artistDto = ArtistDto.builder()
                .id(artist.getId())
                .name(artist.getName())
                .build();

        AlbumDto albumDto = AlbumDto.builder()
                .id(album.getId())
                .title(album.getTitle())
                .build();

        return new ArtistWithAlbumDto(artistDto, albumDto);

    }
}
