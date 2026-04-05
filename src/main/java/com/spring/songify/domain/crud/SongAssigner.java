package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.AlbumDto;
import com.spring.songify.domain.crud.dto.AlbumSongsDto;
import com.spring.songify.domain.crud.dto.GenreDto;
import com.spring.songify.domain.crud.dto.SongDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
class SongAssigner {
    private final AlbumRetriever albumRetriever;
    private final SongRetriever songRetriever;


    AlbumSongsDto assignSongByIdToAlbumById(final Long albumId, final Long songId) {
        Album album = albumRetriever.findById(albumId);
        Song song = songRetriever.findSongById(songId);
        album.addSongToAlbum(song);
        AlbumDto albumDto = AlbumDto.builder()
                .id(album.getId())
                .title(album.getTitle())
                .build();
        SongDto songDto = SongDto.builder()
                .id(song.getId())
                .title(song.getTitle())
                .genre(new GenreDto(song.getGenre().getId(), song.getGenre().getName()))
                .build();

        return new AlbumSongsDto(albumDto, songDto);
    }
}
