package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.LegacyAlbumDto;
import com.spring.beatmarket.domain.catalog.exception.TitleIsBlankException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
@Service
class AlbumAdder {
    private final SongRetriever songRetriever;
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;


    LegacyAlbumDto addAlbum(final Long songId, final String title, final LocalDate releaseDate) {
        if(title == null || title.isBlank()) throw new TitleIsBlankException("Album needs a specified title!");
        Song songById = songRetriever.findSongByIdEagerly(songId);
        Album album = new Album();
        album.changeTitle(title);
        album.addSong(songById);
        album.changeReleaseDate(releaseDate);
        return albumMapper.mapFromEntityToAlbumDto(albumRepository.save(album));
    }

    LegacyAlbumDto addDefaultAlbum (final Long songId){
        Song songById = songRetriever.findSongByIdEagerly(songId);
        Album album = new Album();
        album.assignDefaultTitle();
        album.addSong(songById);
        return albumMapper.mapFromEntityToAlbumDto(albumRepository.save(album));
    }
}
