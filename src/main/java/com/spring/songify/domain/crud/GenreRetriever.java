package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.GenreDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
class GenreRetriever {
    private final GenreRepository genreRepository;
    private final SongRepository songRepository;

    Slice<GenreDto> findAllGenres(Pageable pageable) {
        return genreRepository.findAll(pageable).map(g -> new GenreDto(g.getId(), g.getName()));
    }

    GenreDto findGenreDtoById(final Long genreId) {
        Genre genre = findGenreById(genreId);
        return new GenreDto(genre.getId(),genre.getName());
    }
    Genre findGenreById(final Long genreId) {
        return genreRepository.findGenreById(genreId)
                .orElseThrow(() -> new GenreNotfound("Genre by id=" + genreId + "wasn't found"));

    }
}
