package com.spring.beatmarket.domain.catalog.dto;

public interface ArtistDto {

    record Create(
            String name
    ) {}

    record Update(String name) {}


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
            String imageUrl
    ) {}

    // for nesting - the smallest
    record Reference(Long id, String name) {}

    // 4. for nesting in detailed records (eg. SongDetailsDto)
    record Basic(Long id, String name, String imageUrl, Integer displayOrder) {}
}