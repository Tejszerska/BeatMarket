package com.spring.beatmarket.domain.catalog.dto;

import com.spring.beatmarket.domain.catalog.SongLanguage;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record SongRequestDto(
        String title,
        LocalDate releaseDate,
        Long duration,
        SongLanguage language,
        Long genreId,
        List<Long> artistIds,
        Long albumId
)  {
}
