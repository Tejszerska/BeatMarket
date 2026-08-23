package com.spring.beatmarket.domain.catalog.dto;

import java.util.List;
import java.util.Optional;

public interface ArtistDto {

    record Create(
            String name,
            List<Long> songIds,
            List<Long> albumIds
    ) {}

    record Update(Optional<String> name,
                  Optional<List<Long>> songIds,
                  Optional<List<Long>> albumIds) {}


    // GET **/artists
    record Summary(Long id, String name, String imageUrl) {}

    // GET **/artists/{id}
    record Details(
            Long id,
            String name,
            String imageUrl
    ) {}

    record Info(
            Long id,
            String name,
            List<SongDto.Reference> songs,
            List<AlbumDto.Reference> albums

    ) {}

    // for nesting - the smallest
    record Reference(Long id, String name) {}

    // 4. for nesting in detailed records (eg. SongDetailsDto)
    record Basic(Long id, String name, String imageUrl, Integer displayOrder) {}
}