package com.spring.beatmarket.domain.catalog.dto;
import java.time.LocalDate;
import java.util.List;

public interface AlbumDto {

    record Create(
            String title,
            LocalDate releaseDate
    ) {}

    record Update(
            LocalDate releaseDate,
            List<Long> artistIds
    ) {}

    record Details(
            Long id,
            String title,
            LocalDate releaseDate,
            String coverUrl
    ) {}

    record Summary(
            Long id,
            String title,
            String coverUrl
    ) {}

    record Reference(Long id, String title) {}

    record Basic(Long id, String title, String coverUrl) {}
}