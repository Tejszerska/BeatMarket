package com.spring.beatmarket.domain.catalog.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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
            String coverUrl,
            List<ArtistDto.Reference> artists,
            Set<SongDto.Reference> songs
    ) {}

    record Summary(
            Long id,
            String title,
            String coverUrl,
            List<ArtistDto.Reference> artists
    ) {}

    record Reference(Long id, String title) {}
    record Info(Long id, String title) {}

    record Basic(Long id, String title, String coverUrl) {}
}