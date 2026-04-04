package com.spring.songify.domain.crud;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Transactional
@RequiredArgsConstructor
@Service
class GenreAssigner {
    private final SongRetriever songRetriever;
    private final GenreRetriever genreRetriever;


    void assignGenreByIdToSongById(final Long songId, final Long genreId) {
        Song song = songRetriever.findSongById(songId);
        Genre genre = genreRetriever.findGenreById(genreId);
        song.setGenre(genre);
    }
}
