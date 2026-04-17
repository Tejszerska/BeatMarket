package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.SongDto;
import com.spring.songify.domain.crud.exception.TitleIsBlankException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
class SongUpdater {
    private final SongRetriever songRetriever;
    private final GenreRetriever genreRetriever;
    private final SongMapper songMapper;

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

        return songMapper.mapFromEntityToSongDto(songFromDatabase);
    }
}
