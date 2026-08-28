package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.GenreDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface GenreFacade {
    Slice<GenreDto.Summary> findAllGenres(Pageable pageable);

    GenreDto.Details getGenreDetails(final Long genreId);

    GenreDto.Info addGenre(GenreDto.Create dto);

    GenreDto.Info updateGenre(final Long id, final GenreDto.Update dto);

    GenreDto.Transfer transferGenre(final Long oldId, final Long newId);

    void deactivateGenre(final Long genreId);
}
