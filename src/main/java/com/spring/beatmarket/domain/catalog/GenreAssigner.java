package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.SongDtoOld;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
class GenreAssigner {
    private final SongRetriever songRetriever;
    private final GenreRetriever genreRetriever;
    private final SongMapper songMapper;

    SongDtoOld assignGenreByIdToSongById(final Long songId, final Long genreId) {
        Song song = songRetriever.findSongById(songId);
        Genre genre = genreRetriever.findGenreById(genreId);
        song.assignToGenre(genre);
        return songMapper.mapFromEntityToSongDto(song);
    }
}
