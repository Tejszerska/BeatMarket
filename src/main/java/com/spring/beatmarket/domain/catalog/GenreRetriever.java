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
        return genreRepository.findByActiveTrue(pageable)
                .map(genreMapper::toSummaryDto);
    }

    GenreDto.Details getDetails(final Long genreId) {
        return genreMapper.toDetailsDto(getActive(genreId));
    }

    Genre getActive(final Long id) {
        return genreRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre", id));
    }

    void validateExistsAndActive(Long id) {
        if (!genreRepository.existsByIdAndActiveTrue(id)) {
            throw new ResourceNotFoundException("Genre", id);
        }
    }

}
