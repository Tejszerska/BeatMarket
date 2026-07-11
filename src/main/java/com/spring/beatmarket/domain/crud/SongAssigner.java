package com.spring.beatmarket.domain.crud;

import com.spring.beatmarket.domain.crud.dto.AlbumDto;
import com.spring.beatmarket.domain.crud.dto.AlbumSongsDto;
import com.spring.beatmarket.domain.crud.dto.SongDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
class SongAssigner {
    private final AlbumRetriever albumRetriever;
    private final SongRetriever songRetriever;
    private final SongMapper songMapper;
    private final AlbumMapper albumMapper;


    AlbumSongsDto assignSongByIdToAlbumById(final Long albumId, final Long songId) {
        Album album = albumRetriever.findById(albumId);
        Song song = songRetriever.findSongById(songId);
        album.addSongToAlbum(song);
        AlbumDto albumDto = albumMapper.mapFromEntityToAlbumDto(album);
        SongDto songDto = songMapper.mapFromEntityToSongDto(song);

        return new AlbumSongsDto(albumDto, songDto);
    }
}
