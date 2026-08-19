package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.GenreDto;
import com.spring.beatmarket.domain.catalog.exception.ResourceNotFoundException;
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
    private final GenreMapper genreMapper;

    Slice<GenreDto.Summary> findAll(Pageable pageable) {
        return genreRepository.findAll(pageable)
                .map(genreMapper::toSummary);
    }

    GenreDto.Details getGenreDtoById(final Long genreId) {
        return genreMapper.toDetails(findGenreById(genreId));
    }

    Genre findGenreById(final Long genreId) {
        return genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre", genreId));
    }

    void existsById(Long id) {
        if (!genreRepository.existsById(id)) {
            throw new ResourceNotFoundException("Genre", id);
        }
    }

    Genre getGenreReference(Long id){
        existsById(id);
        return  genreRepository.getReferenceById(id);
    }

}
