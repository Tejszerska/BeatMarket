package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.GenreDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Transactional
class GenreFacadeImpl implements GenreFacade {
    private final GenreAdder genreAdder;
    private final GenreRetriever genreRetriever;
    private final GenreDeleter genreDeleter;
    private final GenreUpdater genreUpdater;
    private final SongFacade songFacade;

    public Slice<GenreDto.Summary> findAllGenres(Pageable pageable) {
        return genreRetriever.findAll(pageable);
    }
    public GenreDto.Details getGenreDetails(final Long genreId) {
        return genreRetriever.getDetails(genreId);
    }
    public GenreDto.Info addGenre(GenreDto.Create dto) {
        return genreAdder.add(dto);
    }
    public GenreDto.Info updateGenre(final Long id, final GenreDto.Update dto) {
        return genreUpdater.update(id, dto);
    }
    public GenreDto.Transfer transferGenre(final Long oldId, final Long newId) {
        if (oldId.equals(newId)) {
            throw new IllegalArgumentException("Source and target genre IDs cannot be the same.");
        }
        genreRetriever.validateExistsAndActive(oldId);
        genreRetriever.validateExistsAndActive(newId);

        Integer updatedSongsCount = songFacade.bulkUpdateSongsByGenreId(oldId, newId);

        return new GenreDto.Transfer(updatedSongsCount, oldId, newId);
    }
    public void deactivateGenre(final Long genreId) {
        genreDeleter.deactivate(genreId);
    }
}
