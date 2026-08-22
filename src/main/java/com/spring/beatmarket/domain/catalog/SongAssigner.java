package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumSongsDto;
import com.spring.beatmarket.domain.catalog.dto.LegacyAlbumDto;
import com.spring.beatmarket.domain.catalog.dto.song.LegacySongDto;
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
        Song song = songRetriever.findSongByIdEagerly(songId);
        album.addSong(song);
        LegacyAlbumDto legacyAlbumDto = albumMapper.mapFromEntityToAlbumDto(album);
        LegacySongDto legacySongDtoOld = songMapper.toDto(song);

        return new AlbumSongsDto(legacyAlbumDto, legacySongDtoOld);
    }
}
