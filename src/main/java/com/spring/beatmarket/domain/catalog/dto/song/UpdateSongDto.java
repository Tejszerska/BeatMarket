package com.spring.beatmarket.domain.catalog.dto.song;

import com.spring.beatmarket.domain.catalog.SongLanguage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public record UpdateSongDto(
        Optional<String> title,
        Optional<LocalDate> releaseDate,
        Optional<Integer> duration,
        Optional<SongLanguage> language,
        Optional<Long> genreId,
        Optional<List<Long>> artistIds,
        Optional<Long> albumId

) {
}
