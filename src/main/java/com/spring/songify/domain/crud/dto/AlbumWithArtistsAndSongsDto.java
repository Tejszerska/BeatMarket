package com.spring.songify.domain.crud.dto;

import java.util.Set;

public record AlbumWithArtistsAndSongsDto(
        AlbumDto album,
        Set<ArtistDto> artists,
        Set<SongDto> songs
) {
}
