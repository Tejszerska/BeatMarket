package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.GenreDto;
import com.spring.songify.domain.crud.dto.SongDto;
import com.spring.songify.domain.crud.dto.SongRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
class SongAdder {
    private final SongRepository songRepository;
    private final GenreRetriever genreRetriever;

    SongDto addSong(final SongRequestDto dto) {
        String languageFromDto = dto.languageDto().name();
        SongLanguage songLanguage = SongLanguage.valueOf(languageFromDto);
        Genre defaultGenre = genreRetriever.findGenreById(1L);
        Song song = new Song(dto.name(), dto.releaseDate(), dto.duration(), songLanguage, defaultGenre);
        Song save = songRepository.save(song);
        GenreDto genreDto = new GenreDto(defaultGenre.getId(), defaultGenre.getName());
        return new SongDto(save.getId(), save.getName(), genreDto);
    }
}
