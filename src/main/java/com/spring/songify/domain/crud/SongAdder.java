package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.GenreDto;
import com.spring.songify.domain.crud.dto.SongDto;
import com.spring.songify.domain.crud.dto.SongRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
class SongAdder {
    private final SongRepository songRepository;
    private final GenreRetriever genreRetriever;

    SongDto addSong(final SongRequestDto dto) {
        String languageFromDto = dto.language().name();
        SongLanguage songLanguage = SongLanguage.valueOf(languageFromDto);
        Genre defaultGenre = genreRetriever.retrieveDefaultGenre();
        Song song = new Song(dto.name(), dto.releaseDate(), dto.duration(), songLanguage, defaultGenre);
        Song save = songRepository.save(song);
        GenreDto genreDto = new GenreDto(defaultGenre.getId(), defaultGenre.getName());
        return new SongDto(save.getId(), save.getTitle(), genreDto);
    }

    SongDto addDefaultSong() {
        Song song = new Song();
        song.assignDefaultTitle();
        Genre defaultGenre = genreRetriever.retrieveDefaultGenre();
        GenreDto genreDto = new GenreDto(defaultGenre.getId(), defaultGenre.getName());
        song.setGenre(defaultGenre);
        Song savedSong = songRepository.save(song);
        return SongDto.builder()
                .id(savedSong.getId())
                .title(savedSong.getTitle())
                .genre(genreDto)
                .build();
    }
}
