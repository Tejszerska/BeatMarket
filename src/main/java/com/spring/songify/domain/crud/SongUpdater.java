package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.GenreDto;
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

    public SongDto updatePartiallyByIdAndDto(Long id, SongDto requestDto) {
        Song songFromDatabase = songRetriever.findSongById(id);


        if (requestDto.title() != null) {
            if (requestDto.title().isBlank()) {
                throw new TitleIsBlankException("Song title cannot be blank!");
            }
            songFromDatabase.setTitle(requestDto.title());
        }

        if (requestDto.genre() != null && requestDto.genre().id() != null) {
            Genre genreById = genreRetriever.findGenreById(requestDto.genre().id());
            songFromDatabase.setGenre(genreById);
        }

        return SongDto.builder()
                .id(songFromDatabase.getId())
                .title(songFromDatabase.getTitle())
                .genre(new GenreDto(songFromDatabase.getGenre().getId(), songFromDatabase.getGenre().getName()))
                .build();
    }
}
