package com.spring.beatmarket.domain.crud;

import com.spring.beatmarket.domain.crud.dto.SongDto;
import com.spring.beatmarket.domain.crud.dto.SongRequestDto;
import com.spring.beatmarket.domain.crud.exception.TitleIsBlankException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
class SongAdder {
    private final SongRepository songRepository;
    private final GenreRetriever genreRetriever;
    private final SongMapper songMapper;


    SongDto addSong(final SongRequestDto dto) {
        if (dto.name() == null || dto.name().isBlank()) {
            throw new TitleIsBlankException("Song needs a specified title!");
        }
        Genre defaultGenre = genreRetriever.retrieveDefaultGenre();
        Song save = songRepository.save(
                new Song(dto.name(), dto.releaseDate(), dto.duration(), dto.language(), defaultGenre));
        return songMapper.mapFromEntityToSongDto(save);
    }

    SongDto addDefaultSong() {
        Song song = new Song();
        song.assignDefaultTitle();
        Genre defaultGenre = genreRetriever.retrieveDefaultGenre();
        song.setGenre(defaultGenre);
        Song savedSong = songRepository.save(song);
        return songMapper.mapFromEntityToSongDto(savedSong);
    }
}
