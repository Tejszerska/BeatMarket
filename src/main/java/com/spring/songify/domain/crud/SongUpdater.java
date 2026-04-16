package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.SongDto;
import com.spring.songify.domain.crud.exception.GenreNotfoundException;
import com.spring.songify.domain.crud.exception.TitleIsBlankException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
class SongUpdater {
    private final SongRepository songRepository;
    private final SongRetriever songRetriever;
    private final GenreRetriever genreRetriever;

    void updateById(Long id, Song newSong) {
        if (newSong.getTitle() == null || newSong.getTitle().isBlank()) {
            throw new TitleIsBlankException("Song title cannot be blank!");
        }
        songRetriever.existsById(id);
        songRepository.updateById(id, newSong);
    }

    void updateByIdAndDto(Long id, SongDto newSongDto) {
        String title = newSongDto.title();
        if (title == null || title.isBlank()) {
            throw new TitleIsBlankException("Song needs a title specified!");
        }

        if (newSongDto.genre() == null || newSongDto.genre().id() == null) {
            throw new GenreNotfoundException("Song's Genre must be specified!");
        }

        Genre genreById = genreRetriever.findGenreById(newSongDto.genre().id());

        Song songToUpdate = new Song(title);
        songToUpdate.setGenre(genreById);

        updateById(id, songToUpdate);
    }
}
