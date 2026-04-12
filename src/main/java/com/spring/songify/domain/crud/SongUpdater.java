package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.GenreDto;
import com.spring.songify.domain.crud.dto.SongDto;
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
        songRetriever.existsById(id);
        songRepository.updateById(id, newSong);
    }

    void updateByIdAndDto(Long id, SongDto newSongDto) {
        Genre genreById = genreRetriever.findGenreById(newSongDto.genre().id());
        Song songToUpdate = new Song(newSongDto.title());
        updateById(id, songToUpdate);
    }


}
