package com.spring.beatmarket.domain.catalog.dto;

import lombok.Builder;

import java.util.List;
import java.util.Optional;

public interface ArtistDto {
    @Builder
    record Create(
            String name,
            List<Long> mainSongIds,
            List<Long> featSongIds,
            List<Long> mainAlbumIds,
            List<Long> featAlbumIds
    ) {
    }

    @Builder
    record Update(Optional<String> name,
                  Optional<List<Long>> mainSongIds,
                  Optional<List<Long>> featSongIds,
                  Optional<List<Long>> mainAlbumIds,
                  Optional<List<Long>> featAlbumIds) {
    }


    // GET **/artists
    record Summary(Long id, String name, String imageUrl) {
    }

    // GET **/artists/{id}
    record Details(
            Long id,
            String name,
            String imageUrl,
            List<SongDto.Reference> songs,
            List<AlbumDto.Reference> albums
    ) {
    }

    record Info(
            Long id,
            String name,
            List<SongDto.Reference> songs,
            List<AlbumDto.Reference> albums

    ) {
    }

    // for nesting - the smallest
    record Reference(Long id, String name) {
    }

    // 4. for nesting in detailed records (eg. SongDetailsDto)
    record Basic(Long id, String name, String imageUrl, Integer displayOrder) {
    }
}