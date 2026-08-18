package com.spring.beatmarket.domain.catalog.dto;

import com.spring.beatmarket.domain.catalog.SongLanguage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SongDto {

    record Create(String title,
                  LocalDate releaseDate,
                  Integer duration,
                  SongLanguage language,
                  Long genreId,
                  List<Long> artistIds,
                  Long albumId) {
    }

    record Update(Optional<String> title,
                  Optional<LocalDate> releaseDate,
                  Optional<Integer> duration,
                  Optional<SongLanguage> language,
                  Optional<Long> genreId,
                  Optional<List<Long>> artistIds,
                  Optional<Long> albumId) {
    }

    record Details(Long id,
                   String title,
                   String language,
                   Integer duration,
                   LocalDate releaseDate,
                   String previewUrl,
                   List<ArtistDto.Basic> artists,
                   GenreDto.Reference genre,
                   AlbumDto.Summary album,
                   Map<String, Price> pricing) {
    }

    record Summary(Long id,
                   String title,
                   List<ArtistDto.Reference> artists,
                   GenreDto.Reference genre,
                   String previewUrl,
                   AlbumDto.Reference album,
                   String language,
                   LocalDate releaseDate,
                   Integer duration,
                   Map<String, Price> pricing) {
    }

    // for nesting - the smallest
    record Reference(Long id, String title) {
    }

    // for nesting in detailed records
    record Basic(Long id,
                 String title,
                 Integer duration) {
    }

    // previewUrl is never updated from regular C / U so it is not returned -
    // HATEOAS in the future
    record Info(Long id,
                String title,
                Integer duration,
                String language,
                LocalDate releaseDate,
                List<ArtistDto.Reference> artists,
                GenreDto.Reference genre,
                AlbumDto.Reference album) {
    }

    record Price(
            BigDecimal amount,
            String currency) {
    }

    record SearchCriteria(String genre,
                          String artist,
                          String language,
                          String album,
                          Integer minDuration,
                          Integer maxDuration,
                          LocalDate releaseDate,
                          BigDecimal maxPrice,
                          String currency,
                          String license) {
    }
}
