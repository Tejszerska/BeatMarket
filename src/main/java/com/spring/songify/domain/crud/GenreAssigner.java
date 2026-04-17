package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.GenreDto;
import com.spring.songify.domain.crud.dto.SongDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
class GenreAssigner {
    private final SongRetriever songRetriever;
    private final GenreRetriever genreRetriever;


    SongDto assignGenreByIdToSongById(final Long songId, final Long genreId) {
        Song song = songRetriever.findSongById(songId);
        Genre genre = genreRetriever.findGenreById(genreId);
        song.setGenre(genre);
        GenreDto genreDto = GenreDto.builder()
                .name(genre.getName())
                .id(genreId).build();

        return SongDto.builder()
                .id(song.getId())
                .title(song.getTitle())
                .genre(genreDto)
                .build();
    }
}
