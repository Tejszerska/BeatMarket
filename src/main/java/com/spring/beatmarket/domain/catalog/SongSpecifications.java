package com.spring.beatmarket.domain.catalog;

import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

class SongSpecifications {
    static Specification<Song> hasGenre(String genre) {
        return (root, query, criteriaBuilder) -> {
            if (genre == null || genre.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.join("genre").get("name"), genre);
        };
    }


    static Specification<Song> hasAlbum(String album) {
        return (root, query, criteriaBuilder) -> {
            if (album == null || album.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.join("album").get("title"), album);
        };
    }

    static Specification<Song> hasArtist(String artist) {
        return (root, query, criteriaBuilder) -> {
            if (artist == null || artist.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            query.distinct(true);
            Join<Song, Artist> artistJoin = root.join("artists");
            return criteriaBuilder.equal(artistJoin.get("name"), artist);
        };
    }

    static Specification<Song> hasLanguage(String language) {
        return (root, query, criteriaBuilder) -> {
            if (language == null || language.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            SongLanguage enumValue = SongLanguage.valueOf(language.toUpperCase());
            return criteriaBuilder.equal(root.get("language"), enumValue);
        };
    }

    static Specification<Song> hasMinDuration(Integer minDuration) {
        return (root, query, criteriaBuilder) -> {
            if (minDuration == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("duration"), minDuration);
        };
    }

    static Specification<Song> hasMaxDuration(Integer maxDuration) {
        return (root, query, criteriaBuilder) -> {
            if (maxDuration == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("duration"), maxDuration);
        };
    }


    static Specification<Song> hasReleaseDate(LocalDate releaseDate) {
        return (root, query, criteriaBuilder) -> {
            if (releaseDate == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("releaseDate"), releaseDate);
        };
    }
}

