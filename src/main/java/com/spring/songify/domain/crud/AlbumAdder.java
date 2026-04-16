package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.AlbumDto;
import com.spring.songify.domain.crud.exception.TitleIsBlankException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
class AlbumAdder {
    private final SongRetriever songRetriever;
    private final AlbumRepository albumRepository;


    AlbumDto addAlbum(final Long songId, final String title, final Instant instant) {
        if(title == null || title.isBlank()) throw new TitleIsBlankException("Album needs a specified title!");
        Song songById = songRetriever.findSongById(songId);
        Album album = new Album();
        album.setTitle(title);
        album.addSongToAlbum(songById);
        album.setReleaseDate(instant);
        albumRepository.save(album);
        return new AlbumDto(album.getId(), album.getTitle());
    }

    AlbumDto addDefaultAlbum (final Long songId){
        Song songById = songRetriever.findSongById(songId);
        Album album = new Album();
        album.assignDefaultTitle();
        album.addSongToAlbum(songById);
        albumRepository.save(album);
        return new AlbumDto(album.getId(), album.getTitle());
    }
}
