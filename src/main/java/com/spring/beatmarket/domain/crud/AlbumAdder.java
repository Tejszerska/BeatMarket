package com.spring.beatmarket.domain.crud;

import com.spring.beatmarket.domain.crud.dto.AlbumDto;
import com.spring.beatmarket.domain.crud.exception.TitleIsBlankException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Service
class AlbumAdder {
    private final SongRetriever songRetriever;
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;


    AlbumDto addAlbum(final Long songId, final String title, final Instant instant) {
        if(title == null || title.isBlank()) throw new TitleIsBlankException("Album needs a specified title!");
        Song songById = songRetriever.findSongById(songId);
        Album album = new Album();
        album.setTitle(title);
        album.addSongToAlbum(songById);
        album.setReleaseDate(instant);
        return albumMapper.mapFromEntityToAlbumDto(albumRepository.save(album));
    }

    AlbumDto addDefaultAlbum (final Long songId){
        Song songById = songRetriever.findSongById(songId);
        Album album = new Album();
        album.assignDefaultTitle();
        album.addSongToAlbum(songById);
        return albumMapper.mapFromEntityToAlbumDto(albumRepository.save(album));
    }
}
